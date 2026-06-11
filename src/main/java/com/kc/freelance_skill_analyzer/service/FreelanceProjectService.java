package com.kc.freelance_skill_analyzer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;
import com.kc.freelance_skill_analyzer.repository.FreelanceProjectRepository;

@Service
public class FreelanceProjectService {

    private final FreelanceProjectRepository freelanceProjectRepository;

    public FreelanceProjectService(FreelanceProjectRepository freelanceProjectRepository) {
        this.freelanceProjectRepository = freelanceProjectRepository;
    }

    /**
     * 案件一覧を取得する
     */
    public List<FreelanceProject> findAll() {
        return freelanceProjectRepository.findAll();
    }

    /**
    * 月単価が高い順で案件一覧を取得する
    */
    public List<FreelanceProject> findAllOrderByUnitPriceDesc() {
        return freelanceProjectRepository.findAllByOrderByUnitPriceDesc();
    }

    /**
     * 案件を保存する
     */
    public FreelanceProject save(FreelanceProject freelanceProject) {
        return freelanceProjectRepository.save(freelanceProject);
    }

    /**
     * IDを指定して案件を1件取得する
     */
    public FreelanceProject findById(Long id) {
        return freelanceProjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("案件が見つかりません。ID: " + id));
    }

    /**
    * 案件を削除する
    */
    public void deleteById(Long id) {
        freelanceProjectRepository.deleteById(id);
    }

    /**
    * 言語で検索する
    */
    public List<FreelanceProject> searchByLanguage(String language) {
        return freelanceProjectRepository.findByLanguageContainingIgnoreCase(language);
    }

    /**
    * 言語＋フレームワークで検索する
    */
    public List<FreelanceProject> search(String language, String framework) {

        if ((language == null || language.isBlank())
            && (framework == null || framework.isBlank())) {
            return freelanceProjectRepository.findAll();
        }

        if (language == null) {
            language = "";
        }

        if (framework == null) {
            framework = "";
        }

        return freelanceProjectRepository
            .findByLanguageContainingIgnoreCaseAndFrameworkContainingIgnoreCase(
                    language,
                    framework
            );
    }
}