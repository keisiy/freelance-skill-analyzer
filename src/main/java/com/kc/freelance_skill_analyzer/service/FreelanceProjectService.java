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
     * 案件を保存する
     */
    public FreelanceProject save(FreelanceProject freelanceProject) {
        return freelanceProjectRepository.save(freelanceProject);
    }
}