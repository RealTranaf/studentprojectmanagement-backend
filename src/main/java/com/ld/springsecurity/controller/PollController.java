package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.Poll;
import com.ld.springsecurity.model.PollVote;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PollResponse;
import com.ld.springsecurity.response.PollVoteResponse;
import com.ld.springsecurity.service.PollService;
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
@RequestMapping("/rooms/{roomId}/polls")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPoll(@PathVariable String roomId,
                                        @RequestParam String title,
                                        @RequestParam String description,
                                        @RequestParam("options") List<String> options,
                                        @RequestParam("deadline") String deadline,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            pollService.createPoll(userDetails.getUsername(), roomId, title, description, files, options, LocalDateTime.parse(deadline));
            return ResponseEntity.ok(new MessageResponse("Poll created successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<?> getPolls(@PathVariable String roomId) {
        try {
            List<Poll> polls = pollService.getPollsForRoom(roomId);
            List<PollResponse> responses = polls.stream().map(PollResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("polls", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{pollId}/votes")
    public ResponseEntity<?> getVotes(@PathVariable String pollId) {

        try {
            List<PollVote> pollVotes = pollService.getVotes(pollId);
            List<PollVoteResponse> responses = pollVotes.stream().map(PollVoteResponse::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("votes", responses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping("/{pollId}/vote")
    public ResponseEntity<?> vote(@PathVariable String pollId,
                                  @RequestParam int optionIndex,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            pollService.vote(pollId, userDetails.getUsername(), optionIndex);
            return ResponseEntity.ok(new MessageResponse("Vote successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{pollId}")
    public ResponseEntity<?> updatePoll(@PathVariable String roomId,
                                        @PathVariable String pollId,
                                        @RequestParam String title,
                                        @RequestParam String description,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @RequestParam(value = "filesToDelete", required = false) List<String> filesToDelete,
                                        @RequestParam(value = "options") List<String> options,
                                        @RequestParam String deadline,
                                        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            pollService.updatePoll(userDetails.getUsername(), pollId, roomId, title, description, files, filesToDelete, options, LocalDateTime.parse(deadline));
            return ResponseEntity.ok(new MessageResponse("Update successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{pollId}")
    public ResponseEntity<?> deletePoll(@PathVariable String roomId,
                                        @PathVariable String pollId,
                                        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            pollService.deletePoll(roomId, pollId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Delete successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
