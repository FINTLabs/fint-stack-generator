package no.fint.stack.generator


import io.kubernetes.client.models.V1Deployment
import io.kubernetes.client.models.V1EnvVar
import io.kubernetes.client.util.Yaml
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = "fint.stack.type", havingValue = "Kubernetes")
class KubeGenerator implements Generator {

    @Override
    String generate(StackModel model) throws Exception {
        def resource = getClass().getResourceAsStream('/k8s-stack.yaml')
        def stack = Yaml.loadAll(new InputStreamReader(resource))

        def provider = getDeployment('provider', stack)
        def consumer = getDeployment('consumer', stack)

        if (model.stack) {
            stack.each {
                it.metadata.labels['fint.stack'] = model.stack
                it.metadata.name = "${it.metadata.labels['fint.role']}-${model.stack}"
            }
            stack.findAll { it.kind == 'Service'}.each { it.spec.selector['fint.stack'] = model.stack }
            stack.findAll { it.kind == 'Deployment'}.each {
                it.spec.selector.matchLabels['fint.stack'] = model.stack
                it.spec.template.metadata.labels['fint.stack'] = model.stack
                it.spec.template.spec.containers*.name = "${it.metadata.labels['fint.role']}-${model.stack}"
                setenv(it, 'fint.hazelcast.kubernetes.labelValue', model.stack)
            }
        }

        if (model.environment == 'play-with-fint') {
            stack.findAll { it.kind == 'Deployment' }.each {
                putenv(it, 'fint.events.orgIds', 'health.fintlabs.no,pwf.no')
            }
            putenv(consumer, 'fint.consumer.default-org-id', 'pwf.no')
            putenv(consumer, 'fint.consumer.override-org-id','true')
        }

        if (model.uri) {
            setenv(consumer, 'server.context-path', model.uri)
            setenv(provider, 'server.context-path', model.uri + '/provider')

            consumer.spec.template.spec.containers.each { it.readinessProbe.httpGet.path = model.uri + '/health' }
            provider.spec.template.spec.containers.each { it.readinessProbe.httpGet.path = model.uri + '/provider/health' }
        }

        if (!model.assets?.isEmpty() && model.environment != 'play-with-fint') {
            putenv(provider, 'fint.provider.assets.endpoint', model.assets)
        }

        if (model.repository) {
            if (model.consumer && model.version) {
                consumer.spec.template.spec.containers.each { it.image = "${model.repository}/${model.consumer}:${model.version}" }
            }
            if (model.provider) {
                provider.spec.template.spec.containers.each { it.image = "${model.repository}/provider:${model.provider}" }
            }
        }

        return Yaml.dumpAll(stack.iterator())
    }

    static V1Deployment getDeployment(name, stack) {
        stack.find { it.metadata.labels['fint.role'] == name && it.kind == 'Deployment' } as V1Deployment
    }

    static V1EnvVar getenv(deployment, name) {
        deployment.spec.template.spec.containers.env.collect { it.find { it.name == name } }.first() as V1EnvVar
    }

    static setenv(deployment, name, value) {
        getenv(deployment, name).value = value
    }

    static putenv(deployment, name, value) {
        deployment.spec.template.spec.containers.each { it.addEnvItem(new V1EnvVar(name: name, value: value)) }
    }
}
