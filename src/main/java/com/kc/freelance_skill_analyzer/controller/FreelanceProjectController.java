package com.kc.freelance_skill_analyzer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;
import com.kc.freelance_skill_analyzer.service.FreelanceProjectService;

@Controller
public class FreelanceProjectController {

    private final FreelanceProjectService freelanceProjectService;

    public FreelanceProjectController(FreelanceProjectService freelanceProjectService) {
        this.freelanceProjectService = freelanceProjectService;
    }

    /**
     * 案件一覧画面を表示する
     */
    @GetMapping("/projects")
    public String list(Model model) {
        model.addAttribute("projects", freelanceProjectService.findAll());
        return "projects/list";
    }

    /**
    * 案件登録画面を表示する
    */
    @GetMapping("/projects/new")
    public String newProject(Model model) {
        model.addAttribute("freelanceProject", new FreelanceProject());
        return "projects/new";
    }

    /**
    * 案件を登録する
    */
    @PostMapping("/projects")
    public String create(@ModelAttribute FreelanceProject freelanceProject) {
        freelanceProjectService.save(freelanceProject);
        return "redirect:/projects";
    }
}