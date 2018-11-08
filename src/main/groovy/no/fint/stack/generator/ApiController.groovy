package no.fint.stack.generator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = '/api', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
class ApiController {

    @Autowired
    private RepoService repoService

    @Autowired
    private Generator generator

    @PostMapping(path = '/generate', produces = 'text/x-yaml', consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity generate(@RequestBody StackModel model) {
        return ResponseEntity.ok(generator.generate(model))
    }

    @GetMapping('/tags/{namespace}/{reponame}')
    ResponseEntity getTags(@PathVariable String namespace, @PathVariable String reponame) {
        return ResponseEntity.ok(repoService.tags(namespace, reponame))
    }

    @GetMapping('/search/{namespace}/{query}')
    ResponseEntity search(@PathVariable String namespace, @PathVariable String query) {
        return ResponseEntity.ok(repoService.search(namespace, query))
    }
}
