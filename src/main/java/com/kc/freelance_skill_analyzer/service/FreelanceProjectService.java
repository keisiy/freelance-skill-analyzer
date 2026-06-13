package com.kc.freelance_skill_analyzer.service;

import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;
import com.kc.freelance_skill_analyzer.repository.FreelanceProjectRepository;
import com.kc.freelance_skill_analyzer.dto.AnalysisDto;

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

    /**
    * ダッシュボード用分析データ取得
    */
    public AnalysisDto getAnalysis() {

        var projects = freelanceProjectRepository.findAll();

        long totalCount = projects.size();

        

        double averageUnitPrice = projects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .mapToInt(FreelanceProject::getUnitPrice)
            .average()
            .orElse(0);

        int maxUnitPrice = projects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .max(Comparator.comparing(FreelanceProject::getUnitPrice))
            .map(FreelanceProject::getUnitPrice)
            .orElse(0);

        int minUnitPrice = projects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .min(Comparator.comparing(FreelanceProject::getUnitPrice))
            .map(FreelanceProject::getUnitPrice)
            .orElse(0);

        var highPriceProjects =
            freelanceProjectRepository.findByUnitPriceGreaterThanEqual(800000);

        long highPriceCount = highPriceProjects.size();

        double highPriceAverageUnitPrice = highPriceProjects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .mapToInt(FreelanceProject::getUnitPrice)
            .average()
            .orElse(0);

        int highPriceMaxUnitPrice = highPriceProjects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .mapToInt(FreelanceProject::getUnitPrice)
            .max()
            .orElse(0);

        int highPriceMinUnitPrice = highPriceProjects.stream()
            .filter(p -> p.getUnitPrice() != null)
            .mapToInt(FreelanceProject::getUnitPrice)
            .min()
            .orElse(0);
        
        Map<String, Long> languageRanking = createRanking(
            projects.stream()
                .map(FreelanceProject::getLanguage)
                .toList()
            );
        
        Map<String, Long> frameworkRanking = createRanking(
            projects.stream()
                .map(FreelanceProject::getFramework)
                .toList()
            );

        return new AnalysisDto(
            totalCount,
            averageUnitPrice,
            maxUnitPrice,
            minUnitPrice,
            highPriceCount,
            highPriceAverageUnitPrice,
            highPriceMaxUnitPrice,
            highPriceMinUnitPrice,
            languageRanking,
            frameworkRanking
        );
    }

    /**
    * カンマ区切りの文字列リストからランキングを作成する
    */
    private Map<String, Long> createRanking(List<String> values) {

        return values.stream()
            .filter(value -> value != null)
            .flatMap(value -> Arrays.stream(value.split(",")))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .collect(Collectors.groupingBy(
                    value -> value,
                    Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a,
                    LinkedHashMap::new
                )
            );
    }
}