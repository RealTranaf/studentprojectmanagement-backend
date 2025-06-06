package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.*;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.StudentTopicSelectionResponse;
import com.ld.springsecurity.response.TopicResponse;
import com.ld.springsecurity.service.RoomService;
import com.ld.springsecurity.service.TopicService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/topics")
public class TopicController {
    private final TopicService topicService;
    private final RoomService roomService;

    public TopicController(TopicService topicService, RoomService roomService) {
        this.topicService = topicService;
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTopics() {
        try {
            List<Topic> topics = topicService.getAllTopics();
            List<TopicResponse> responses = topics.stream().map(TopicResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("topics", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/non-custom")
    public ResponseEntity<?> getNonCustomTopics() {
        try {
            List<Topic> topics = topicService.getNonCustomTopics();
            List<TopicResponse> responses = topics.stream().map(TopicResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("topics", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/select")
    public ResponseEntity<?> selectExistingTopic(@PathVariable String roomId,
                                                 @RequestParam String topicId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            topicService.selectExistingTopic(userDetails.getUsername(), topicId, roomId);
            return ResponseEntity.ok(new MessageResponse("Selected successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/custom")
    public ResponseEntity<?> submitCustomTopic(@PathVariable String roomId,
                                               @RequestParam String title,
                                               @RequestParam String description,
                                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                               @AuthenticationPrincipal UserDetails userDetails ) {
        try {
            topicService.submitCustomTopic(userDetails.getUsername(), title, description, files, roomId);
            return ResponseEntity.ok(new MessageResponse("Submitted successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/teacher")
    public ResponseEntity<?> submitTopicTeacher(@PathVariable String roomId,
                                                @RequestParam String title,
                                                @RequestParam String description,
                                                @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                                @AuthenticationPrincipal UserDetails userDetails ) {
        try {
            topicService.submitTopicTeacher(roomId, title, description, files, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Teacher topic added successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/selected")
    public ResponseEntity<?> getStudentSelectedTopic(@PathVariable String roomId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            StudentTopicSelection selection = topicService.getStudentSelection(userDetails.getUsername(), roomId);
            StudentTopicSelectionResponse selected = new StudentTopicSelectionResponse(selection);
            return ResponseEntity.ok(selected);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/selections")
    public ResponseEntity<?> getAllStudentsSelections(@PathVariable String roomId) {
        try {
            List<StudentTopicSelection> selectionList = topicService.getAllStudentSelections(roomId);
            List<StudentTopicSelectionResponse> responses = selectionList.stream().map(StudentTopicSelectionResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("selections", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PatchMapping("/selections/{selectionId}/verify")
    public ResponseEntity<?> verifyStudentSelection(@PathVariable String roomId,
                                                    @PathVariable String selectionId,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            topicService.verifySelection(roomId, selectionId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Selection verified!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable String roomId,
                                         @PathVariable String topicId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            topicService.deleteTopic(roomId, topicId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Topic deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PutMapping("/{topicId}")
    public ResponseEntity<?> editTopic(@PathVariable String roomId,
                                       @PathVariable String topicId,
                                       @RequestParam String title,
                                       @RequestParam String description,
                                       @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                       @RequestParam(value = "filesToDelete", required = false) List<String> filesToDelete,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            topicService.editTopic(roomId, topicId, userDetails.getUsername(), title, description, files, filesToDelete);
            return ResponseEntity.ok(new MessageResponse("Topic updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/export-excel")
    public void exportStudentSelectionToExcel(@PathVariable String roomId,
                                              HttpServletResponse response) {
        try {
            Room room = roomService.getRoomDetail(roomId);
            Set<User> students = room.getUsers().stream().filter(u -> u.getRole().equals(Role.STUDENT)).collect(Collectors.toSet());
            List<StudentTopicSelection> selections = topicService.getAllStudentSelections(roomId);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Student Topic Selections");

            int rowIndex = 0;

            Row roomNameRow = sheet.createRow(rowIndex++);
            roomNameRow.createCell(0).setCellValue("Room Name:");
            roomNameRow.createCell(1).setCellValue(room.getName());

            Row roomTypeRow = sheet.createRow(rowIndex++);
            roomTypeRow.createCell(0).setCellValue("Room Type:");
            roomTypeRow.createCell(1).setCellValue(room.getType() != null ? room.getType().toString() : "-");

            rowIndex++;

            Row header = sheet.createRow(rowIndex++);
            String[] headers = {"Student", "Topic", "Description", "Type", "Status"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (User student : students) {
                Row row = sheet.createRow(rowIndex++);
                StudentTopicSelection selection = selections.stream()
                        .filter(sel -> sel.getStudent().getUsername().equals(student.getUsername()))
                        .findFirst().orElse(null);

                row.createCell(0).setCellValue(student.getUsername());

                if (selection != null && selection.getTopic() != null) {
                    row.createCell(1).setCellValue(selection.getTopic().getTitle());
                    row.createCell(2).setCellValue(selection.getTopic().getDescription());
                    row.createCell(3).setCellValue(selection.isCustom() ? "Custom" : "Existing");
                    row.createCell(4).setCellValue(selection.isVerified() ? "Verified" : "Pending");
                } else {
                    row.createCell(1).setCellValue("Not Selected");
                    row.createCell(2).setCellValue("");
                    row.createCell(3).setCellValue("");
                    row.createCell(4).setCellValue("");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "student_topic_selections_" + roomId + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (RuntimeException | IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


}
