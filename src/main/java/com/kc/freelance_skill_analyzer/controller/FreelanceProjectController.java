package com.kc.freelance_skill_analyzer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;
import com.kc.freelance_skill_analyzer.service.FreelanceProjectService;
import com.kc.freelance_skill_analyzer.dto.AnalysisDto;

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
    * 月単価が高い順で案件一覧を表示する
    */
    @GetMapping("/projects/sort/unit-price-desc")
    public String listOrderByUnitPriceDesc(Model model) {
        model.addAttribute("projects",
            freelanceProjectService.findAllOrderByUnitPriceDesc());
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

    /**
     * 案件詳細画面を表示する
     */
    @GetMapping("/projects/{id}")
    public String detail(@PathVariable Long id, Model model) {
        FreelanceProject project = freelanceProjectService.findById(id);
        model.addAttribute("project", project);
        return "projects/detail";
    }

    /**
    * 案件編集画面を表示する
    */
    @GetMapping("/projects/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        FreelanceProject project = freelanceProjectService.findById(id);
        model.addAttribute("freelanceProject", project);
        return "projects/edit";
    }

    /**
    * 案件を更新する
    */
    @PostMapping("/projects/{id}/edit")
    public String update(@PathVariable Long id,
                     @ModelAttribute FreelanceProject freelanceProject) {
        freelanceProject.setId(id);
        freelanceProjectService.save(freelanceProject);
        return "redirect:/projects/" + id;
    }

    /**
    * 案件を削除する
    */
    @PostMapping("/projects/{id}/delete")
    public String delete(@PathVariable Long id) {
        freelanceProjectService.deleteById(id);
        return "redirect:/projects";
    }

    /**
    * 案件を言語・フレームワークで検索する
    */
    @GetMapping("/projects/search")
    public String search(
        @RequestParam(required = false) String language,
        @RequestParam(required = false) String framework,
        Model model) {

        model.addAttribute(
            "projects",
            freelanceProjectService.search(language, framework));

        model.addAttribute("language", language);
        model.addAttribute("framework", framework);

        return "projects/list";
    }

    /**
    * 案件を分析する
    */
    @GetMapping("/analysis")
    public String analysis(Model model) {

        AnalysisDto analysis =
            freelanceProjectService.getAnalysis();

        model.addAttribute("analysis", analysis);

        return "analysis/dashboard";
    }

    /**
    * 月単価80万円以上の案件一覧を表示する
    */
    @GetMapping("/projects/high-price")
    public String highPriceProjects(Model model) {
        model.addAttribute("projects", freelanceProjectService.findHighPriceProjects());
        return "projects/list";
    }

    /**
    * CSVファイルから案件をインポートする画面
    */
    @GetMapping("/projects/import")
    public String importForm() {
        return "projects/import";
    }

    /**
    * CSVファイルから案件をインポートする画面
    */
    @PostMapping("/projects/import")
    public String importCsv(@RequestParam("file") MultipartFile file) {
        freelanceProjectService.importCsv(file);
        return "redirect:/projects";
    }

    @PostMapping("/projects/import-url")
    public String importCsvFromUrl(@RequestParam String csvUrl) {
        freelanceProjectService.importCsvFromUrl(csvUrl);
        return "redirect:/projects";
    }
}