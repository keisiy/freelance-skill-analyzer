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
    
    private Map<String, Long> languageRanking;

    private Map<String, Long> frameworkRanking;

    private Map<String, Long> requiredSkillRanking;

}