package no.fint.stack.generator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class RepoServiceSpec extends Specification {
    @Autowired
    RepoService repoService
    def "Query for beta consumers"() {
        when:
        def result = repoService.search('beta', 'consumer')

        then:
        result
        result.size() > 0

        when:
        def version = repoService.tags('beta', result[0])

        then:
        version
        version.size() > 0
    }
}
