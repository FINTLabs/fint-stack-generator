package no.fint.stack.generator

import spock.lang.Specification

class GeneratorSpec extends Specification {

    def "Able to generate a stack file from input"() {
        given:
        def generator = new Generator()
        def stack = new StackModel(
                environment: 'beta',
                stack: 'utdanning-elev',
                uri: '/utdanning/elev',
                port: 8113,
                repository: 'dtr.fintlabs.no/beta',
                consumer: 'consumer-utdanning-elev',
                version: '0.8.0-3.1.0',
                provider: '2.0.1',
                assets: new URI('https://admin.fintlabs.no/api/components/assets/utdanning_elev')
        )

        when:
        def result = generator.generate(stack)
        println(result)

        then:
        noExceptionThrown()
        result
    }

    def 'Removing prefix from string'() {
        given:
        def string = 'beta/consumer-utdanning-elev'
        def prefix = 'beta/'

        when:
        def result = string - prefix

        then:
        result == 'consumer-utdanning-elev'

    }
}
