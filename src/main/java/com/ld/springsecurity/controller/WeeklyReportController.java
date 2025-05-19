package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.WeeklyReportPost;
import com.ld.springsecurity.model.WeeklyReportSubmission;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.WeeklyReportPostListResponse;
import com.ld.springsecurity.response.WeeklyReportSubmissionListResponse;
import com.ld.springsecurity.service.WeeklyReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/weekly-reports")
public class WeeklyReportController {
    private final WeeklyReportService weeklyReportService;

    public WeeklyReportController(WeeklyReportService weeklyReportService) {
        this.weeklyReportService = weeklyReportService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@PathVariable String roomId,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam("deadline") String deadline,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            WeeklyReportPost post = weeklyReportService.createPost(roomId, title, content, LocalDateTime.parse(deadline), userDetails.getUsername(), files);
            return ResponseEntity.ok(new MessageResponse("Post created successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<?> getPosts(@PathVariable String roomId) {
        try{
            List<WeeklyReportPost> posts = weeklyReportService.getPosts(roomId);
            List<WeeklyReportPostListResponse> responses = posts.stream().map(WeeklyReportPostListResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();

            response.put("posts", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{reportPostId}/create")
    public ResponseEntity<?> submitReport(@PathVariable String roomId,
                                          @PathVariable String reportPostId,
                                          @RequestParam("content") String content,
                                          @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                          @AuthenticationPrincipal UserDetails userDetails){
        try{
            WeeklyReportSubmission submission = weeklyReportService.submitReport(reportPostId, userDetails.getUsername(), content, files);
            return ResponseEntity.ok(new MessageResponse("Submission created successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{reportPostId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable String reportPostId){
        try {

            List<WeeklyReportSubmission> submissionList = weeklyReportService.getSubmissions(reportPostId);
            List<WeeklyReportSubmissionListResponse> responses = submissionList.stream().map(WeeklyReportSubmissionListResponse::new).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();

            response.put("submissions", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable String submissionId,
                                             @RequestParam String grade,
                                             @RequestParam String note){
        try {
            WeeklyReportSubmission submission = weeklyReportService.gradeSubmission(submissionId, grade, note);
            return ResponseEntity.ok(new MessageResponse("Submission graded successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
