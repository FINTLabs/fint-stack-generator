package no.fint.stack.generator


import no.fint.stack.generator.http.AuthenticatingRequestInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations

@Configuration
class Config {
    @Value('${fint.admin.uri}')
    String adminuri

    @Value('${fint.registry.username}')
    String username

    @Value('${fint.registry.password}')
    String password

    @Value('${fint.registry.name}')
    String registryname

    @Bean
    @Qualifier('registry')
    RestOperations restForRegistry(RestTemplateBuilder builder) {
        return builder
                .rootUri("https://$registryname/v2")
                .additionalCustomizers(new AuthenticatingRequestInterceptor(username, password))
                .build()
    }

    @Bean
    @Qualifier('admin')
    RestOperations restForAdmin(RestTemplateBuilder builder) {
        return builder
                .rootUri(adminuri)
                .build()
    }
}
