package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.model.WeeklyReportPost;
import com.ld.springsecurity.model.WeeklyReportSubmission;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.UserRepository;
import com.ld.springsecurity.repo.WeeklyReportPostRepository;
import com.ld.springsecurity.repo.WeeklyReportSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WeeklyReportService {
    private final WeeklyReportPostRepository weeklyReportPostRepository;

    private final WeeklyReportSubmissionRepository weeklyReportSubmissionRepository;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final FileStorageService fileStorageService;

    public WeeklyReportService(WeeklyReportPostRepository weeklyReportPostRepository, WeeklyReportSubmissionRepository weeklyReportSubmissionRepository, UserRepository userRepository, RoomRepository roomRepository, FileStorageService fileStorageService) {
        this.weeklyReportPostRepository = weeklyReportPostRepository;
        this.weeklyReportSubmissionRepository = weeklyReportSubmissionRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.fileStorageService = fileStorageService;
    }

    public WeeklyReportPost createPost(String roomId, String title, String content, LocalDateTime deadline, String teacherName, List<MultipartFile> files){
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<User> optionalUser = userRepository.findByUsername(teacherName);
        if (optionalUser.isPresent() && optionalRoom.isPresent()){
            User teacher = optionalUser.get();
            Room room = optionalRoom.get();
            WeeklyReportPost post = new WeeklyReportPost();
            post.setRoom(room);
            post.setTitle(title);
            post.setContent(content);
            post.setDeadline(deadline);
            post.setAuthor(teacher);

            List<String> fileUrls = new ArrayList<>();
            if (files != null) {
                for (MultipartFile file : files) {
                    String url = fileStorageService.storeFile(file);
                    fileUrls.add(url);
                }
            }
            post.setFileUrls(fileUrls);
            return weeklyReportPostRepository.save(post);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public List<WeeklyReportPost> getPosts(String roomId){
        return weeklyReportPostRepository.findByRoomId(roomId);
    }

    public WeeklyReportSubmission submitReport(String reportPostId, String studentName, String content, List<MultipartFile> files) {
        Optional<WeeklyReportPost> optionalWeeklyReportPost = weeklyReportPostRepository.findById(reportPostId);
        Optional<User> optionalUser = userRepository.findByUsername(studentName);
        if (optionalUser.isPresent() && optionalWeeklyReportPost.isPresent()){
            User student = optionalUser.get();
            WeeklyReportPost post = optionalWeeklyReportPost.get();
            WeeklyReportSubmission submission = weeklyReportSubmissionRepository.findByReportPost_IdAndStudent_Id(reportPostId, student.getId())
                    .orElse(new WeeklyReportSubmission());
            submission.setReportPost(post);
            submission.setStudent(student);
            submission.setContent(content);
            submission.setSubmittedAt(LocalDateTime.now());

            List<String> fileUrls = new ArrayList<>();
            if (files != null){
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    fileUrls.add(url);
                }
            }
            submission.setFileUrls(fileUrls);
            return weeklyReportSubmissionRepository.save(submission);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public List<WeeklyReportSubmission> getSubmissions(String reportPostId) {
        return weeklyReportSubmissionRepository.findByReportPostId(reportPostId);
    }

    public WeeklyReportSubmission gradeSubmission(String submissionId, String grade, String note){
        Optional<WeeklyReportSubmission> optionalSub = weeklyReportSubmissionRepository.findById(submissionId);
        if (optionalSub.isPresent()){
            WeeklyReportSubmission submission = optionalSub.get();
            submission.setGrade(grade);
            submission.setTeacherNote(note);
            submission.setGradedAt(LocalDateTime.now());
            return weeklyReportSubmissionRepository.save(submission);
        } else {
            throw new RuntimeException("Submission not found");
        }
    }


}
