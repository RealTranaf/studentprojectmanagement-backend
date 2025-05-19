package com.ld.springsecurity.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WeeklyReportPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String content;

    private LocalDateTime deadline;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    public WeeklyReportPost(String title, String content, LocalDateTime deadline, Room room, User author) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.room = room;
        this.author = author;
    }
}
