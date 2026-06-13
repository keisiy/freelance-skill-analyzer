package com.kc.freelance_skill_analyzer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "freelance_projects")
@Getter
@Setter
public class FreelanceProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String language;

    private String framework;

    private String industry;

    private String role;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer unitPrice;

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;
}