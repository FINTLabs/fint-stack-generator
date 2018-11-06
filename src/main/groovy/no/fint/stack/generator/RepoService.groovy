package no.fint.stack.generator

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

@Service
class RepoService {

    @Autowired
    RestOperations restTemplate

    String[] search(String owner, String query) {
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/api/v0/index/dockersearch?q={query}', String, query)
        def data = slurper.parseText(result)
        println(data)
        return data.results.findAll { it.name.startsWith(owner) }.collect { it.name }
    }

    String[] tags(String namespace, String reponame) {
        def slurper = new JsonSlurper()
        def result = restTemplate.getForObject('/api/v0/repositories/{namespace}/{reponame}/tags', String, namespace, reponame)
        def data = slurper.parseText(result)
        println(data)
        return data.collect { it.name }
    }
}
