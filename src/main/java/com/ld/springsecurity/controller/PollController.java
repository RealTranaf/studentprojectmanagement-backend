package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.Poll;
import com.ld.springsecurity.model.PollVote;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PollResponse;
import com.ld.springsecurity.response.PollVoteResponse;
import com.ld.springsecurity.service.PollService;
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

    @GetMapping("/{pollId}/export-excel")
    public void exportPollVotesToExcel(@PathVariable String roomId,
                                       @PathVariable String pollId,
                                       HttpServletResponse response) {
        try {
            Poll poll = pollService.getPollById(pollId);
            List<PollVote> votes = pollService.getVotes(pollId);
            List<String> options = poll.getOptions();
            int[] optionCounts = new int[options.size()];

            for (PollVote vote : votes) {
                if (vote.getOptionIndex() >= 0 && vote.getOptionIndex() < optionCounts.length) {
                    optionCounts[vote.getOptionIndex()]++;
                }
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Poll Votes");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            int rowIndex = 0;

            Row pollTitleRow = sheet.createRow(rowIndex++);
            pollTitleRow.createCell(0).setCellValue("Poll Title:");
            pollTitleRow.createCell(1).setCellValue(poll.getTitle());

            Row pollDescRow = sheet.createRow(rowIndex++);
            pollDescRow.createCell(0).setCellValue("Description:");
            pollDescRow.createCell(1).setCellValue(poll.getDescription());

            Row deadlineRow = sheet.createRow(rowIndex++);
            deadlineRow.createCell(0).setCellValue("Deadline:");
            deadlineRow.createCell(1).setCellValue(poll.getDeadline() != null ? poll.getDeadline().toString() : "-");

            Row createdRow = sheet.createRow(rowIndex++);
            createdRow.createCell(0).setCellValue("Created on:");
            createdRow.createCell(1).setCellValue(poll.getCreatedAt() != null ? poll.getCreatedAt().toString() : "-");

            Row totalVotesRow = sheet.createRow(rowIndex++);
            totalVotesRow.createCell(0).setCellValue("Total Votes:");
            totalVotesRow.createCell(1).setCellValue(votes.size());

            rowIndex++;

            Row optionHeader = sheet.createRow(rowIndex++);
            optionHeader.createCell(0).setCellValue("Option");
            optionHeader.createCell(1).setCellValue("Votes");
            optionHeader.getCell(0).setCellStyle(headerStyle);
            optionHeader.getCell(1).setCellStyle(headerStyle);

            for (int i = 0; i < options.size(); i++) {
                Row optionRow = sheet.createRow(rowIndex++);
                optionRow.createCell(0).setCellValue(options.get(i));
                optionRow.createCell(1).setCellValue(optionCounts[i]);
            }

            Row header = sheet.createRow(rowIndex++);
            String[] headers = {"User", "Vote"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (PollVote vote : votes) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(vote.getUser().getUsername());
                row.createCell(1).setCellValue(
                        vote.getOptionIndex() >= 0 && vote.getOptionIndex() < options.size()
                                ? options.get(vote.getOptionIndex())
                                : "-"
                );
            }
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "poll_" + pollId + "_votes.xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (RuntimeException | IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
