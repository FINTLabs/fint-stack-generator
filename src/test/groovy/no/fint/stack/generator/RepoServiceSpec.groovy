package no.fint.stack.generator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Specification

@SpringBootTest
class RepoServiceSpec extends Specification {
    @Autowired
    RepoService repoService

    // TODO Enable this using password from environment
    @Ignore
    def "Query for beta consumers"() {
        when:
        def result = repoService.search('consumer')
        println(result)

        then:
        result
        result.size() > 0
        result.every { it.startsWith('consumer-')}

        when:
        def version = repoService.tags(result[0])
        println(version)

        then:
        version
        version.size() > 0
    }
}
