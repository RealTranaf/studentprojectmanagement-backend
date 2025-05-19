package com.ld.springsecurity.response;

import com.ld.springsecurity.model.WeeklyReportSubmission;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WeeklyReportSubmissionListResponse {
    private String id;
    private String author;
    private String content;
    private LocalDateTime submittedAt;
    private List<String> fileUrls;
    private String grade;
    private String teacherNote;
    private LocalDateTime gradedAt;

    public WeeklyReportSubmissionListResponse(WeeklyReportSubmission weeklyReportSubmission) {
        this.id = weeklyReportSubmission.getId();
        this.author = weeklyReportSubmission.getStudent().getUsername();
        this.content = weeklyReportSubmission.getContent();
        this.submittedAt = weeklyReportSubmission.getSubmittedAt();
        this.fileUrls = weeklyReportSubmission.getFileUrls();
        this.grade = weeklyReportSubmission.getGrade();
        this.teacherNote = weeklyReportSubmission.getTeacherNote();
        this.gradedAt = weeklyReportSubmission.getGradedAt();
    }
}
