package no.fint.stack.generator


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class StackController {

    @Autowired
    Generator generator

    @Autowired
    RepoService repoService

    @Autowired
    StackDefinitions stackDefinitions

    @GetMapping
    String form(Model model) {
        model.addAttribute('stack', new StackModel())
        model.addAttribute('consumers', repoService.search('beta', 'consumer'))
        model.addAttribute('providers', repoService.tags('beta', 'provider'))
        return 'stack'
    }

    @PostMapping
    String stack(Model model, @ModelAttribute StackModel body) {
        model.addAttribute('stack', body)
        model.addAttribute('consumers', repoService.search(body.repository, 'consumer'))
        model.addAttribute('versions', repoService.tags(body.repository, body.consumer))
        model.addAttribute('providers', repoService.tags(body.repository, 'provider'))
        model.addAttribute('result', generator.generate(body))
        return 'stack'
    }
}
