package no.fint.stack.generator

import io.kubernetes.client.JSON
import io.kubernetes.client.util.Yaml
import spock.lang.Specification

class KubeGeneratorSpec extends Specification {

    def "Parse k8s-stack.yaml and serialize as JSON"() {
        given:
        def res = getClass().getResourceAsStream('/k8s-stack.yaml')
        def stack = Yaml.loadAll(new InputStreamReader(res))
        def json = new JSON();

        when:
        def result = json.serialize(stack)

        then:
        result
    }

    def "Can parse the k8s-stack.yaml file"() {
        given:
        def res = getClass().getResourceAsStream('/k8s-stack.yaml')

        when:
        def result = Yaml.loadAll(new InputStreamReader(res))

        then:
        result
        result.size() == 4
        result.any { it.metadata.name == "consumer-administrasjon-kodeverk" }
        result.every { it.metadata.labels['fint.stack'] == 'administrasjon-kodeverk' }

        when:
        def consumerDeployment = KubeGenerator.getDeployment('consumer', result)
        println(consumerDeployment.spec.template.spec.containers*.resources)

        then:
        consumerDeployment

        when:
        def contextPath = KubeGenerator.getenv(consumerDeployment, 'server.context-path')
        println(contextPath)

        then:
        contextPath

        when:
        def providerDeployment = KubeGenerator.getDeployment('provider', result)

        then:
        providerDeployment

        when:
        def servletPath = providerDeployment.spec.template.spec.containers[0].env.find {
            it.name == 'server.context-path'
        }.value
        println(servletPath)

        then:
        servletPath
    }

    def 'Produces valid output for default input'() {
        given:
        def generator = new KubeGenerator()
        def model = new StackModel()

        when:
        def result = generator.generate(model)

        then:
        result
    }

    def 'Produces valid output from a defined stack'() {
        given:
        def stack = new StackModel(
                environment: 'beta',
                stack: 'utdanning-elev',
                uri: '/utdanning/elev',
                repository: 'fintlabsacr.azurecr.io',
                consumer: 'consumer-utdanning-elev',
                version: '0.8.0-3.1.0',
                provider: '2.0.1',
                assets: new URI('https://admin.fintlabs.no/api/components/assets/utdanning_elev')
        )
        def generator = new KubeGenerator()

        when:
        def result = generator.generate(stack)
        println(result)

        then:
        result
    }
}
