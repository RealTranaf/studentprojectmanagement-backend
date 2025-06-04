package com.ld.springsecurity.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    @ElementCollection
    private List<String> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
