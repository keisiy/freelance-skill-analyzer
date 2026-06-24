package com.kc.freelance_skill_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;

public interface FreelanceProjectRepository
        extends JpaRepository<FreelanceProject, Long> {

        List<FreelanceProject> findAllByOrderByUnitPriceDesc();

        List<FreelanceProject> findByLanguageContainingIgnoreCase(String language);

        List<FreelanceProject> findByLanguageContainingIgnoreCaseAndFrameworkContainingIgnoreCase(
                String language,
                String framework
        );

        List<FreelanceProject> findByUnitPriceGreaterThanEqual(Integer unitPrice);

        boolean existsBySourceUrl(String sourceUrl);

}