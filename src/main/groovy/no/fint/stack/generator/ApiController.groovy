package no.fint.stack.generator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerMapping

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(value = '/api', produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
class ApiController {

    @Autowired
    private RepoService repoService

    @Autowired
    private Generator generator

    @Autowired
    private AdminService adminService

    @PostMapping(path = '/generate', produces = 'text/x-yaml', consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity generate(@RequestBody StackModel model) {
        ResponseEntity.ok(generator.generate(model))
    }

    @GetMapping('/tags/**')
    ResponseEntity getTags(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString()
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path)
        ResponseEntity.ok(repoService.tags(arguments))
    }

    @GetMapping('/search/{query}')
    ResponseEntity search(@PathVariable String query) {
        ResponseEntity.ok(repoService.search(query))
    }

    @GetMapping('/configurations')
    ResponseEntity getConfigurations() {
        ResponseEntity.ok(adminService.configurations)
    }
}
