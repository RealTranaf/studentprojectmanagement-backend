package com.ld.springsecurity.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WeeklyReportSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "report_post_id")
    private WeeklyReportPost reportPost;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime submittedAt;

    @ElementCollection
    private List<String> fileUrls = new ArrayList<>();

    private String grade;

    private String teacherNote;

    private LocalDateTime gradedAt;

    @ColumnDefault("false")
    private boolean isLate;

    @ColumnDefault("true")
    private boolean isActive;

    @ElementCollection
    private List<String> teacherFileUrls = new ArrayList<>();
}
