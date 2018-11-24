package no.fint.stack.generator

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

@Service
class RepoService {

    @Autowired
    @Qualifier("registry")
    RestOperations restTemplate

    String[] search(String query) {
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/_catalog', String)
        def data = slurper.parseText(result)
        return data.repositories.findAll { it.contains(query) }
    }

    String[] tags(String name) {
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/{name}/tags/list', String, name)
        def data = slurper.parseText(result)
        return data.tags
    }
}
