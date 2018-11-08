package no.fint.stack.generator

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties('fint.stack')
class StackDefinitions {
    Map<String, Stack> definitions
}
