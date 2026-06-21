package com.kc.freelance_skill_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AnalysisDto {

    private long totalCount;

    private double averageUnitPrice;

    private int maxUnitPrice;

    private int minUnitPrice;

    // 月単価が高い案件の件数
    private long highPriceCount;
    // 月単価が高い案件の平均月単価
    private double highPriceAverageUnitPrice;
    // 月単価が高い案件の最大月単価
    private int highPriceMaxUnitPrice;
    // 月単価が高い案件の最小月単価
    private int highPriceMinUnitPrice;
    // 言語のランキング
    private Map<String, Long> languageRanking;
    // フレームワークのランキング
    private Map<String, Long> frameworkRanking;
    // 必須スキルのランキング
    private Map<String, Long> requiredSkillRanking;
    // 歓迎スキルのランキング
    private Map<String, Long> preferredSkillRanking;
    // 月単価が高い案件の言語のランキング
    private Map<String, Long> highPriceLanguageRanking;
    // 月単価が高い案件のフレームワークのランキング
    private Map<String, Long> highPriceFrameworkRanking;
    // 月単価が高い案件の必須スキルのランキング
    private Map<String, Long> highPriceRequiredSkillRanking;
    // 月単価が高い案件の歓迎スキルのランキング
    private Map<String, Long> highPricePreferredSkillRanking;

}