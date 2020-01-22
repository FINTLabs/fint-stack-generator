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
    AdminService adminService

    @GetMapping
    String form(Model model) {
        model.addAttribute('stack', new StackModel(repository: repoService.registry()))
        model.addAttribute('configurations', adminService.configurations)
        return 'stack'
    }

    @PostMapping
    String stack(Model model, @ModelAttribute StackModel body) {
        model.addAttribute('stack', body)
        model.addAttribute('configurations', adminService.configurations)
        model.addAttribute('result', generator.generate(body))
        return 'stack'
    }
}
