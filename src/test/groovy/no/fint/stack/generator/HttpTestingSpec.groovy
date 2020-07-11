package no.fint.stack.generator

import no.fint.stack.generator.http.AuthenticatingRequestInterceptor
import org.springframework.boot.web.client.RestTemplateBuilder
import spock.lang.Ignore
import spock.lang.Specification

class HttpTestingSpec extends Specification {

    @Ignore // dtr.fintlabs.no is no more.
    def "Fetch from dtr"() {
        given:
        def api = 'https://dtr.fintlabs.no/v2'
        def interceptor = new AuthenticatingRequestInterceptor('jenkins', 'password')
        def resttemplate = new RestTemplateBuilder().additionalCustomizers(interceptor).build()

        when:
        def response = resttemplate.getForObject('{api}/', String, api)
        println(response)

        then:
        response

        when:
        response = resttemplate.getForObject('{api}/_catalog', String, api)
        println(response)

        then:
        response

        when:
        response = resttemplate.getForObject('{api}/{name}/tags/list', String, api, 'beta/consumer-personal')
        println(response)

        then:
        response
    }

    @Ignore // TODO Rewrite to get password from environment
    def "Fetch from Azure CR"() {
        given:
        def api = 'https://fintlabsacr.azurecr.io/v2'
        def interceptor = new AuthenticatingRequestInterceptor('fintlabs', '')
        def resttemplate = new RestTemplateBuilder().additionalCustomizers(interceptor).build()

        when:
        def response = resttemplate.getForObject('{api}/', String, api)
        println(response)

        then:
        response

        when:
        response = resttemplate.getForObject('{api}/_catalog', String, api)
        println(response)

        then:
        response

        when:
        response = resttemplate.getForObject('{api}/{name}/tags/list', String, api, 'provider')
        println(response)

        then:
        response
    }
}
