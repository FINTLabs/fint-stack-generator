package no.fint.stack.generator

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Paths


@SpringBootApplication
class Application implements CommandLineRunner {

	static void main(String[] args) {
		SpringApplication.run Application, args
	}

	@Override
	void run(String... args) throws Exception {

        def dumperOptions = new DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        def yaml = new Yaml(dumperOptions)

		def stack = yaml.load new ClassPathResource('stack-template.yml').inputStream

        def settings = yaml.load Files.newBufferedReader(Paths.get(args[0]))

        stack['services']['provider']['ports'][0] = "${settings['port']}:8080".toString()
        stack['services']['consumer']['ports'][0] = "${settings['port']+1}:8080".toString()

        stack['services']['consumer']['environment']['server.context-path'] = settings['uri']
        stack['services']['provider']['environment']['server.context-path'] = "${settings['uri']}/provider".toString()

        stack['services']['consumer']['environment']['fint.audit.mongo.databasename'] = "fint-audit-${settings['environment']}".toString()
        stack['services']['provider']['environment']['fint.audit.mongo.databasename'] = "fint-audit-${settings['environment']}".toString()

        stack['services']['consumer']['image'] = "${settings['repository']}/${settings['stack']}:${settings['version']}".toString()
        stack['services']['provider']['image'] = settings['provider']

        stack['services']['consumer']['environment']['fint.relations.default-base-url'] = "https://${settings['environment']}.felleskomponent.no".toString()

        stack['services']['health-adapter']['environment']['fint.adapter.sse-endpoint'] = "https://${settings['environment']}.felleskomponent.no${settings['uri']}/provider/sse/%s".toString()
        stack['services']['health-adapter']['environment']['fint.adapter.status-endpoint'] = "https://${settings['environment']}.felleskomponent.no${settings['uri']}/provider/status".toString()
        stack['services']['health-adapter']['environment']['fint.adapter.response-endpoint'] = "https://${settings['environment']}.felleskomponent.no${settings['uri']}/provider/response".toString()

        yaml.dump(stack, Files.newBufferedWriter(Paths.get(args[1])))
	}
}
