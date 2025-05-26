package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.Role;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.model.WeeklyReportPost;
import com.ld.springsecurity.model.WeeklyReportSubmission;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.WeeklyReportPostResponse;
import com.ld.springsecurity.response.WeeklyReportSubmissionResponse;
import com.ld.springsecurity.service.RoomService;
import com.ld.springsecurity.service.WeeklyReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/weekly-reports")
public class WeeklyReportController {
    private final WeeklyReportService weeklyReportService;

    private final RoomService roomService;

    public WeeklyReportController(WeeklyReportService weeklyReportService, RoomService roomService) {
        this.weeklyReportService = weeklyReportService;
        this.roomService = roomService;
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
            List<WeeklyReportPostResponse> responses = posts.stream().map(WeeklyReportPostResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();

            response.put("posts", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @DeleteMapping("/{reportPostId}/delete")
    public ResponseEntity<?> deletePost(@PathVariable String roomId,
                                        @PathVariable String reportPostId,
                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            weeklyReportService.deleteWeeklyReportPost(roomId, reportPostId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{reportPostId}/edit")
    public ResponseEntity<?> editPost(@PathVariable String roomId,
                                      @PathVariable String reportPostId,
                                      @RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam("deadline") String deadline,
                                      @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                      @RequestParam(value = "filesToDelete", required = false) List<String> filesToDelete,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            weeklyReportService.editPost(roomId, reportPostId, userDetails.getUsername(), title, content, LocalDateTime.parse(deadline), files, filesToDelete);
            return ResponseEntity.ok(new MessageResponse("Post edited successfully"));
        } catch (Exception e) {
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

    @PutMapping("/{reportPostId}/create")
    public ResponseEntity<?> resubmitReport(@PathVariable String roomId,
                                            @PathVariable String reportPostId,
                                            @RequestParam("content") String content,
                                            @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                            @AuthenticationPrincipal UserDetails userDetails){
        try {
            weeklyReportService.resubmitReport(roomId, reportPostId, userDetails.getUsername() ,content, files);
            return ResponseEntity.ok(new MessageResponse("Submission resubmitted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{reportPostId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable String reportPostId){
        try {

            List<WeeklyReportSubmission> submissionList = weeklyReportService.getSubmissions(reportPostId);
            List<WeeklyReportSubmissionResponse> responses = submissionList.stream().map(WeeklyReportSubmissionResponse::new).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();

            response.put("submissions", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/student-submissions")
    public ResponseEntity<?> getStudentSubmissions(@PathVariable String roomId,
                                                   @RequestParam String username) {
        try {
            List<WeeklyReportSubmission> submissionList = weeklyReportService.getStudentSubmissions(roomId, username);
            List<WeeklyReportSubmissionResponse> responses = submissionList.stream().map(WeeklyReportSubmissionResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
//            System.out.println(responses.get(0).getAuthor());
            response.put("studentSubmissions", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
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

    @GetMapping("/{reportPostId}/export-excel")
    public void exportSubmissionToExcel(@PathVariable String roomId,
                                        @PathVariable String reportPostId,
                                        HttpServletResponse response) {
        try {

            Set<User> students = roomService.getRoomDetail(roomId).getUsers()
                    .stream()
                    .filter(u -> u.getRole().equals(Role.STUDENT))
                    .collect(Collectors.toSet());

            List<WeeklyReportSubmission> submissions = weeklyReportService.getSubmissions(reportPostId);

            WeeklyReportPost post = weeklyReportService.getReportPost(reportPostId);
            String deadline = post != null && post.getDeadline() != null
                    ? post.getDeadline().toString().replaceAll("[: ]", "_")
                    : "no_deadline";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Submissions");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            String headers[] = {"Student", "Status", "Is Late?", "Report", "Files", "Grade", "Note"};
            for (int i = 0; i < headers.length; i++){
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;

            for (User student : students) {
                Row row = sheet.createRow(rowIndex);

                CellStyle cellStyle = null;
                if (rowIndex % 2 != 0){
                    cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }

                WeeklyReportSubmission sub = submissions.stream()
                                .filter(s -> {
                                    if (s.getStudent() != null){
                                        return s.getStudent().getUsername().equals(student.getUsername());
                                    }
                                    return false;
                                }).findFirst().orElse(null);

                String isLateStatus = "-";
                if (sub != null) {
                    isLateStatus = sub.isLate()  ? "Late" : "On Time";
                }

                String[] rowData = {
                        student.getUsername(),
                        sub != null ? "Turned in " + sub.getSubmittedAt() : "Not Turned In",
                        sub != null && sub.getContent() != null ? sub.getContent() : "-",
                        isLateStatus,
                        sub != null && sub.getFileUrls() != null ? String.join(", ", sub.getFileUrls()) : "-",
                        sub != null && sub.getGrade() != null ? sub.getGrade() : "-",
                        sub != null && sub.getTeacherNote() != null ? sub.getTeacherNote() : "-"
                };

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rowData[i]);
                    if (cellStyle != null) {
                        cell.setCellStyle(cellStyle);
                    }
                    sheet.autoSizeColumn(i);
                }
                rowIndex++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = reportPostId + "_" + deadline + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename= " + filename);

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (RuntimeException | IOException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


}
