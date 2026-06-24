package com.kc.freelance_skill_analyzer.service;

import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.net.URI;
import java.net.URL;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        List<FreelanceProject> highPriceProjects =
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
        
        Map<String, Long> highPriceLanguageRanking = createRanking(
            highPriceProjects.stream()
                .map(FreelanceProject::getLanguage)
                .toList());

        Map<String, Long> highPriceFrameworkRanking = createRanking(
            highPriceProjects.stream()
                .map(FreelanceProject::getFramework)
                .toList());

        Map<String, Long> highPriceRequiredSkillRanking = createRanking(
            highPriceProjects.stream()
                .map(FreelanceProject::getRequiredSkills)
                .toList());

        Map<String, Long> highPricePreferredSkillRanking = createRanking(
            highPriceProjects.stream()
                .map(FreelanceProject::getPreferredSkills)
                .toList());
        
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
        
        Map<String, Long> requiredSkillRanking = createRanking(
            projects.stream()
                .map(FreelanceProject::getRequiredSkills)
                .toList()
            );
        
        Map<String, Long> preferredSkillRanking = createRanking(
            projects.stream()
                .map(FreelanceProject::getPreferredSkills)
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
            frameworkRanking,
            requiredSkillRanking,
            preferredSkillRanking,
            highPriceLanguageRanking,
            highPriceFrameworkRanking,
            highPriceRequiredSkillRanking,
            highPricePreferredSkillRanking
        );
    }

    /**
    * カンマもしくは改行区切りの文字列リストからランキングを作成する
    */
    private Map<String, Long> createRanking(List<String> values) {

        return values.stream()
            .filter(value -> value != null)
            .flatMap(value -> Arrays.stream(value.split("[,\\n]")))
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

    /**
    * 月単価80万円以上の案件一覧を取得する
    */
    public List<FreelanceProject> findHighPriceProjects() {
        return freelanceProjectRepository.findByUnitPriceGreaterThanEqual(800000);
    }

    /**
    * CSVファイルから案件を一括登録する
    */
    public void importCsv(MultipartFile file) {

        try (
            InputStreamReader reader =
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(reader);

            for (CSVRecord record : records) {

                // 重複チェック（sourceUrlがすでに登録済みの場合はスキップ）
                String sourceUrl = record.get("sourceUrl");
                if (isDuplicateBySourceUrl(sourceUrl)) {
                    continue;
                }

                FreelanceProject project = new FreelanceProject();

                project.setTitle(record.get("title").trim());
                project.setLanguage(record.get("language").trim());
                project.setFramework(record.get("framework").trim());
                project.setIndustry(record.get("industry").trim());
                project.setRole(record.get("role").trim());
                project.setDescription(record.get("description").trim());
                project.setUnitPrice(parseIntegerOrNull(record.get("unitPrice")));
                project.setRequiredSkills(record.get("requiredSkills").trim());
                project.setPreferredSkills(record.get("preferredSkills").trim());
                project.setSourceName(record.get("sourceName").trim());
                project.setSourceUrl(sourceUrl == null ? null : sourceUrl.trim());

                freelanceProjectRepository.save(project);
            }

        } catch (Exception e) {
            throw new RuntimeException("CSVインポートに失敗しました", e);
        }
    }

    /**
    * CSV公開URLから案件を一括登録する
    */
    public void importCsvFromUrl(String csvUrl) {

        try {
            URI uri = URI.create(csvUrl);
            URL url = uri.toURL();

            try (
                InputStream inputStream = url.openStream();
                InputStreamReader reader =
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader);

                for (CSVRecord record : records) {

                    // 重複チェック（sourceUrlがすでに登録済みの場合はスキップ）
                    String sourceUrl = record.get("sourceUrl");
                    if (isDuplicateBySourceUrl(sourceUrl)) {
                        continue;
                    }

                    FreelanceProject project = new FreelanceProject();

                    project.setTitle(record.get("title").trim());
                    project.setLanguage(record.get("language").trim());
                    project.setFramework(record.get("framework").trim());
                    project.setIndustry(record.get("industry").trim());
                    project.setRole(record.get("role").trim());
                    project.setDescription(record.get("description").trim());
                    project.setUnitPrice(parseIntegerOrNull(record.get("unitPrice")));
                    project.setRequiredSkills(record.get("requiredSkills").trim());
                    project.setPreferredSkills(record.get("preferredSkills").trim());
                    project.setSourceName(record.get("sourceName").trim());
                    project.setSourceUrl(sourceUrl == null ? null : sourceUrl.trim());

                    freelanceProjectRepository.save(project);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV URLインポートに失敗しました", e);
        }
    }

    /**
     * 文字列をIntegerに変換する。空文字やnullの場合はnullを返す
     * @param value
     * @return Integer or null
     */
    private Integer parseIntegerOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Integer.valueOf(value.trim());
    }

    /**
    * 情報取得元URLがすでに登録済みか判定する
    * return true: 登録済み, false: 未登録
    */
    private boolean isDuplicateBySourceUrl(String sourceUrl) {
        return sourceUrl != null
            && !sourceUrl.isBlank()
            && freelanceProjectRepository.existsBySourceUrl(sourceUrl.trim());
    }
}