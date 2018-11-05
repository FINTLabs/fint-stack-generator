package no.fint.stack.generator

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

@Service
class Generator {

    String env(String s) {
        switch (s) {
            case "api": return ''
            default: return "-${s}"
        }
    }

    String generate(StackModel model) throws Exception {


        def dumperOptions = new DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        def yaml = new Yaml(dumperOptions)

        def stack = yaml.load new ClassPathResource('stack-template.yml').inputStream

        stack['services']['provider']['ports'][0] = "${model.port}:8080".toString()
        stack['services']['consumer']['ports'][0] = "${model.port + 1}:8080".toString()

        stack['services']['consumer']['environment']['server.context-path'] = model.uri
        stack['services']['provider']['environment']['server.context-path'] = "${model.uri}/provider".toString()

        stack['services']['consumer']['environment']['fint.audit.mongo.databasename'] = "fint-audit${env(model.environment)}".toString()
        stack['services']['provider']['environment']['fint.audit.mongo.databasename'] = "fint-audit${env(model.environment)}".toString()

        /*
        if (settings['resources']) {
            stack['services']['consumer']['environment']['fint.events.orgIds'] = settings['resources']
            stack['services']['provider']['environment']['fint.events.orgIds'] = settings['resources']
        }
        */
        if (model.assets) {
            stack['services']['provider']['environment']['fint.provider.assets.endpoint'] = model.assets.toString()
        }

        if (model.consumer) {
            stack['services']['consumer']['image'] = model.consumer
        } /*else {
            stack['services']['consumer']['image'] = "${settings['repository']}/consumer-${settings['stack']}:${settings['version']}".toString()
        }*/
        stack['services']['provider']['image'] = model.provider

        stack['services']['consumer']['environment']['fint.relations.default-base-url'] = "https://${model.environment}.felleskomponent.no".toString()

        stack['services']['health-adapter']['environment']['fint.adapter.sse-endpoint'] = "https://${model.environment}.felleskomponent.no${model.uri}/provider/sse/%s".toString()
        stack['services']['health-adapter']['environment']['fint.adapter.status-endpoint'] = "https://${model.environment}.felleskomponent.no${model.uri}/provider/status".toString()
        stack['services']['health-adapter']['environment']['fint.adapter.response-endpoint'] = "https://${model.environment}.felleskomponent.no${model.uri}/provider/response".toString()

        return yaml.dump(stack)
    }

}