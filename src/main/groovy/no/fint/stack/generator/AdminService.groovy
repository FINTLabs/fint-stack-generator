package no.fint.stack.generator

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

@Service
@Slf4j
class AdminService {

    @Autowired
    Config config

    @Autowired
    @Qualifier('admin')
    RestOperations restOperations

    List<Stack> getConfigurations() {
        def slurper = new JsonSlurper()
        def result = restOperations.getForObject('/api/components/configurations', String)
        log.debug("Result: {}", result)
        slurper.parseText(result).collect { it.assetPath = config.adminuri + it.assetPath; it }.sort { a, b -> a.name.compareTo(b.name) } as List<Stack>
    }
}
