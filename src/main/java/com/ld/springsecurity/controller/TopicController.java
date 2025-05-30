package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.StudentTopicSelection;
import com.ld.springsecurity.model.Topic;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.StudentTopicSelectionResponse;
import com.ld.springsecurity.response.TopicResponse;
import com.ld.springsecurity.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/topics")
public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
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

    @PostMapping("/select")
    public ResponseEntity<?> selectExistingTopic(@PathVariable String roomId,
                                                 @RequestParam String topicId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            StudentTopicSelection selection = topicService.selectExistingTopic(userDetails.getUsername(), topicId, roomId);
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
            StudentTopicSelection selection = topicService.submitCustomTopic(userDetails.getUsername(), title, description, files, roomId);
            return ResponseEntity.ok(new MessageResponse("Submitted successfully"));
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

}
