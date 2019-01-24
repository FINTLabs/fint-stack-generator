package no.fint.stack.generator

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

@Slf4j
@Service
class RepoService {

    @Autowired
    @Qualifier("registry")
    RestOperations restTemplate

    @Autowired
    Config config

    def registry() {
        config.registryname
    }

    String[] search(String query) {
        def prefix = ''
        if (config.registryname.contains('/')) {
            prefix = config.registryname.split('/')[1] + '/'
        }
        log.debug('Query: {}{}', prefix, query)
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/_catalog', String)
        def data = slurper.parseText(result)
        log.debug('Catalog: {}', data)
        return data.repositories.findAll { it.contains(prefix + query) }.collect { it - prefix }
    }

    String[] tags(String name) {
        def prefix = ''
        if (config.registryname.contains('/')) {
            prefix = config.registryname.split('/')[1] + '/'
        }
        log.debug('Name: {}{}', prefix, name)
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/{name}/tags/list', String, prefix + name)
        def data = slurper.parseText(result)
        log.debug('Tags: {}', data)
        return data.tags
    }
}
