package com.ld.springsecurity.service;

import com.ld.springsecurity.model.*;
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
import java.util.stream.Collectors;

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
            LocalDateTime now = LocalDateTime.now();
            if (deadline != null && now.isAfter(deadline)){
                post.setExpired(true);
            } else {
                post.setExpired(false);
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    post.getFileUrls().add(url);
                }
            }
            return weeklyReportPostRepository.save(post);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public void editPost(String roomId, String reportPostId, String username, String title, String content, LocalDateTime deadline, List<MultipartFile> files, List<String> filesToDelete) {
        Optional<WeeklyReportPost> optionalPost = weeklyReportPostRepository.findById(reportPostId);
        if (optionalPost.isPresent()) {
            WeeklyReportPost post = optionalPost.get();
            if (!post.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not the author of this post");
            }

            if (filesToDelete != null && post.getFileUrls() != null) {
                List<String> updatedFileUrls = new ArrayList<>(post.getFileUrls());
                for (String fileUrl : filesToDelete) {
                    fileStorageService.deleteFile(fileUrl);
                    updatedFileUrls.remove(fileUrl);
                }
                post.setFileUrls(updatedFileUrls);
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    post.getFileUrls().add(url);
                }
            }

            post.setTitle(title);
            post.setContent(content);
            post.setDeadline(deadline);
            LocalDateTime now = LocalDateTime.now();
            if (deadline != null && now.isAfter(deadline)){
                post.setExpired(true);
            } else {
                post.setExpired(false);
            }
            weeklyReportPostRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void deleteWeeklyReportPost(String roomId, String reportPostId, String username) {
        Optional<WeeklyReportPost> optionalPost = weeklyReportPostRepository.findById(reportPostId);
        if (optionalPost.isPresent()) {
            WeeklyReportPost post = optionalPost.get();
            User author = post.getAuthor();
            if (author == null || !author.getUsername().equals(username) || !author.getRole().equals(Role.TEACHER)) {
                throw new RuntimeException("Invalid user");
            }
            if (post.getFileUrls() != null) {
                for (String fileUrl : post.getFileUrls()) {
                    fileStorageService.deleteFile(fileUrl);
                }
            }

            List<WeeklyReportSubmission> submissions = weeklyReportSubmissionRepository.findByReportPostId(reportPostId);
            for (WeeklyReportSubmission sub : submissions) {
                if (sub.getFileUrls() != null) {
                    for (String fileUrl : sub.getFileUrls()) {
                        fileStorageService.deleteFile(fileUrl);
                    }
                }
            }
            weeklyReportSubmissionRepository.deleteAll(submissions);
            weeklyReportPostRepository.deleteById(reportPostId);
        }

    }

    public List<WeeklyReportPost> getPosts(String roomId){
        List<WeeklyReportPost> posts = weeklyReportPostRepository.findByRoomIdOrderByDeadlineAsc(roomId);
        LocalDateTime now = LocalDateTime.now();
        for (WeeklyReportPost post : posts) {
            if (post.getDeadline() != null && now.isAfter(post.getDeadline())){
                post.setExpired(true);
            } else {
                post.setExpired(false);
            }
        }
        return posts;
    }

    public WeeklyReportPost getReportPost(String reportPostId) { return weeklyReportPostRepository.findById(reportPostId).get(); }

    public WeeklyReportSubmission submitReport(String reportPostId, String studentName, String content, List<MultipartFile> files) {
        Optional<WeeklyReportPost> optionalWeeklyReportPost = weeklyReportPostRepository.findById(reportPostId);
        Optional<User> optionalUser = userRepository.findByUsername(studentName);
        if (optionalUser.isPresent() && optionalWeeklyReportPost.isPresent()){
            User student = optionalUser.get();
            WeeklyReportPost post = optionalWeeklyReportPost.get();

            List<WeeklyReportSubmission> previousSubs = weeklyReportSubmissionRepository.findByReportPostIdAndStudent_UsernameOrderBySubmittedAtDesc(reportPostId, studentName);
            for (WeeklyReportSubmission sub : previousSubs) {
                if (sub.isActive()) {
                    sub.setActive(false);
                    weeklyReportSubmissionRepository.save(sub);
                }
            }
            WeeklyReportSubmission submission = new WeeklyReportSubmission();
            submission.setReportPost(post);
            submission.setStudent(student);
            submission.setContent(content);

            LocalDateTime now = LocalDateTime.now();
            submission.setSubmittedAt(now);
            if (post.getDeadline() != null && now.isAfter(post.getDeadline())){
                submission.setLate(true);
            } else {
                submission.setLate(false);
            }

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
            submission.setActive(true);
            return weeklyReportSubmissionRepository.save(submission);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public List<WeeklyReportSubmission> getSubmissions(String reportPostId) {
        return weeklyReportSubmissionRepository.findByReportPostIdAndIsActiveTrue(reportPostId);
    }

    public List<WeeklyReportSubmission> getStudentSubmissions(String roomId, String username) {
        List<WeeklyReportPost> posts = weeklyReportPostRepository.findByRoomId(roomId);
        List<String> postIds = posts.stream().map(WeeklyReportPost::getId).collect(Collectors.toList());
        return weeklyReportSubmissionRepository.findByReportPostIdInAndStudent_UsernameAndIsActiveTrue(postIds, username);
    }

    public List<WeeklyReportSubmission> getStudentSubmissionsByPost(String reportPostId, String username) {
        return weeklyReportSubmissionRepository.findByReportPostIdAndStudent_UsernameOrderBySubmittedAtDesc(reportPostId, username);
    }

    public WeeklyReportSubmission gradeSubmission(String submissionId, String grade, String note, List<MultipartFile> files){
        Optional<WeeklyReportSubmission> optionalSub = weeklyReportSubmissionRepository.findById(submissionId);
        if (optionalSub.isPresent()){
            WeeklyReportSubmission submission = optionalSub.get();

            List<String> prevTeacherFiles = submission.getTeacherFileUrls();
            if (prevTeacherFiles != null && !prevTeacherFiles.isEmpty()) {
                for (String url : prevTeacherFiles) {
                    fileStorageService.deleteFile(url);
                }
            }

            submission.setGrade(grade);
            submission.setTeacherNote(note);
            submission.setGradedAt(LocalDateTime.now());

            List<String> teacherFileUrls = new ArrayList<>();
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    teacherFileUrls.add(url);
                }
            }
            submission.setTeacherFileUrls(teacherFileUrls);
            return weeklyReportSubmissionRepository.save(submission);
        } else {
            throw new RuntimeException("Submission not found");
        }
    }


}
