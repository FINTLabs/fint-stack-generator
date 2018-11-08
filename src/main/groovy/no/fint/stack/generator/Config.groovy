package no.fint.stack.generator

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestOperations

@Configuration
class Config {
    @Value('${fint.dtr.username}')
    String username

    @Value('${fint.dtr.apikey}')
    String apikey

    @Value('${fint.dtr.uri}')
    String rooturi

    @Bean
    RestOperations rest(RestTemplateBuilder builder) {
        return builder
                .basicAuthorization(username, apikey)
                .rootUri(rooturi)
                .additionalInterceptors({ request, body, execution -> println(request.URI); execution.execute(request, body) } as ClientHttpRequestInterceptor)
                .build()
    }
}
