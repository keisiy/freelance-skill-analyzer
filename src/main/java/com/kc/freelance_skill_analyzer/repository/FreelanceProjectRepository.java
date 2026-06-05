package com.kc.freelance_skill_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kc.freelance_skill_analyzer.entity.FreelanceProject;

public interface FreelanceProjectRepository
        extends JpaRepository<FreelanceProject, Long> {

}