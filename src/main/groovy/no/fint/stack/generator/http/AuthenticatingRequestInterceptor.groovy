package no.fint.stack.generator.http

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.*
import org.springframework.web.util.UriComponentsBuilder

import java.nio.charset.Charset

import static org.springframework.util.Base64Utils.encodeToString

@Slf4j
class AuthenticatingRequestInterceptor implements ClientHttpRequestInterceptor, ResponseErrorHandler, RestTemplateCustomizer {
    def UTF_8 = Charset.forName("UTF-8")
    String username
    String password
    RestOperations restOperations

    AuthenticatingRequestInterceptor(String username, String password) {
        this.username = username
        this.password = password
        restOperations = new RestTemplateBuilder().errorHandler(this).basicAuthorization(username, password).build()
    }

    @Override
    void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(this)
        def interceptors = restTemplate.interceptors
        if (!interceptors?.isEmpty()) {
            interceptors.push(this)
            restTemplate.setInterceptors(interceptors)
        } else {
            restTemplate.setInterceptors([this])
        }
    }

    @Override
    ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        def response = execution.execute(request, body)
        if (response.rawStatusCode == 401) {
            def result = WwwAuthenticateHeaderParser.parse(response.headers.getFirst(HttpHeaders.WWW_AUTHENTICATE))
            if (result.scheme == 'Bearer') {
                log.debug('Requesting Bearer token ...')
                def token = getBearerToken(result.attributes)
                request.headers.set(HttpHeaders.AUTHORIZATION, "Bearer $token")

                log.debug('Retrying request with Bearer token ...')
                response = execution.execute(request, body)
            } else if (result.scheme == 'Basic') {
                def token = encodeToString("${username}:${password}".getBytes(UTF_8))
                request.headers.set(HttpHeaders.AUTHORIZATION, "Basic $token")

                log.debug('Retrying request with Basic auth ...')
                response = execution.execute(request, body)
            } else {
                log.warn('Unhandled authentication challenge {}', result)
            }
        }
        response
    }

    def getBearerToken(def p) {
        def realm = p.remove('realm')
        def slurper = new JsonSlurper()
        p.'client_id' = UUID.randomUUID().toString()
        def b = UriComponentsBuilder.fromUriString(realm)
        p.each { entry -> b.queryParam(entry.key, entry.value) }
        URI uri = b.build().toUri()
        log.debug('URI: {}', uri)
        def result = restOperations.getForEntity(uri, String)
        log.debug('Result: {}', result)
        if (!result.statusCode.is2xxSuccessful()) {
            throw new HttpClientErrorException(result.statusCode, result.body)
        }
        slurper.parseText(result.body).'access_token'
    }

    @Override
    boolean hasError(ClientHttpResponse response) throws IOException {
        return response.statusCode.'5xxServerError'
    }

    @Override
    void handleError(ClientHttpResponse response) throws IOException {
        def body = response.body.getBytes()
        def charset = response.headers?.getContentType()?.getCharset()
        def error = new HttpServerErrorException(response.statusCode, response.statusText, response.headers, body, charset)
        log.warn('Error: {}', error)
        throw error
    }

}
