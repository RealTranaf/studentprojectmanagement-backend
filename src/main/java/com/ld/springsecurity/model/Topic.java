package com.ld.springsecurity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "proposed_by_id")
    private User proposedBy;

    private boolean isCustom;
}
