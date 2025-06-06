package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.StudentTopicSelection;
import com.ld.springsecurity.model.Topic;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.StudentTopicSelectionRepository;
import com.ld.springsecurity.repo.TopicRepository;
import com.ld.springsecurity.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final StudentTopicSelectionRepository studentTopicSelectionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final RoomRepository roomRepository;

    public TopicService(TopicRepository topicRepository, StudentTopicSelectionRepository studentTopicSelectionRepository, UserRepository userRepository, FileStorageService fileStorageService, RoomRepository roomRepository) {
        this.topicRepository = topicRepository;
        this.studentTopicSelectionRepository = studentTopicSelectionRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.roomRepository = roomRepository;
    }
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
    public List<Topic> getNonCustomTopics() {
        return topicRepository.findByIsCustomFalse();
    }


    public void selectExistingTopic(String username, String topicId, String roomId){
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalTopic.isPresent()) {
            User student = optionalUser.get();
            Topic topic = optionalTopic.get();
            Room room = optionalRoom.get();

            studentTopicSelectionRepository.findByStudent_IdAndRoom_Id(student.getId(), room.getId())
                    .ifPresent(studentTopicSelectionRepository::delete);

            StudentTopicSelection selection = new StudentTopicSelection();
            selection.setStudent(student);
            selection.setTopic(topic);
            selection.setRoom(room);
            selection.setVerified(false);
            selection.setCustom(false);
            studentTopicSelectionRepository.save(selection);
        } else {
            throw new RuntimeException("User or topic not found");
        }
    }

    public void submitCustomTopic(String username, String title, String description, List<MultipartFile> files, String roomId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User student = optionalUser.get();
            Room room = optionalRoom.get();
            Topic newTopic = new Topic();
            newTopic.setTitle(title);
            newTopic.setDescription(description);
            newTopic.setProposedBy(student);
            newTopic.setCustom(true);
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    newTopic.getFileUrls().add(url);
                }
            }
            Topic savedTopic = topicRepository.save(newTopic);

            studentTopicSelectionRepository.findByStudent_IdAndRoom_Id(student.getId(), room.getId())
                    .ifPresent(studentTopicSelectionRepository::delete);

            StudentTopicSelection selection = new StudentTopicSelection();
            selection.setStudent(student);
            selection.setTopic(savedTopic);
            selection.setRoom(room);
            selection.setVerified(false);
            selection.setCustom(true);
            studentTopicSelectionRepository.save(selection);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public void submitTopicTeacher(String roomId, String title, String description, List<MultipartFile> files, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User teacher = optionalUser.get();
            Room room = optionalRoom.get();
            Topic newTopic = new Topic();
            newTopic.setTitle(title);
            newTopic.setDescription(description);
            newTopic.setProposedBy(teacher);
            newTopic.setCustom(false);
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    newTopic.getFileUrls().add(url);
                }
            }
            topicRepository.save(newTopic);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public List<StudentTopicSelection> getAllStudentSelections(String roomId) {
        return studentTopicSelectionRepository.findByRoom_Id(roomId);
    }

    public StudentTopicSelection getStudentSelection(String username, String roomId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User student = optionalUser.get();
            Room room = optionalRoom.get();
            Optional<StudentTopicSelection> optionalSelection = studentTopicSelectionRepository.findByStudent_IdAndRoom_Id(student.getId(), room.getId());

            if (optionalSelection.isPresent()) {
                return optionalSelection.get();
            } else {
                throw new RuntimeException("Selection not found!");
            }
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public void verifySelection (String roomId, String selectionId, String username) {
        Optional<StudentTopicSelection> optionalSelection = studentTopicSelectionRepository.findById(selectionId);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalSelection.isPresent()) {
            StudentTopicSelection selection = optionalSelection.get();
            User teacher = optionalUser.get();
            Room room = optionalRoom.get();
            if (!room.getCreatedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not permitted to verify this selection");
            }
            selection.setVerified(true);
            studentTopicSelectionRepository.save(selection);
        } else {
            throw new RuntimeException("Selection or user not found");
        }
    }

    @Transactional
    public void deleteTopic(String roomId, String topicId, String username) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);
        if (optionalRoom.isPresent() && optionalTopic.isPresent()) {
            Room room = optionalRoom.get();
            Topic topic = optionalTopic.get();
            if (!topic.getProposedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not permitted to edit this topic.");
            }
            if (topic.getFileUrls() != null) {
                for (String url : topic.getFileUrls()) {
                    fileStorageService.deleteFile(url);
                }
            }
            studentTopicSelectionRepository.deleteAllByTopic_Id(topicId);
            topicRepository.delete(topic);
        } else {
            throw new RuntimeException("Room or topic not found");
        }
    }

    @Transactional
    public void editTopic(String roomId, String topicId, String username, String title, String description, List<MultipartFile> files, List<String> filesToDelete) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);
        if (optionalRoom.isPresent() && optionalTopic.isPresent()) {
            Room room = optionalRoom.get();
            Topic topic = optionalTopic.get();
            if (!topic.getProposedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not permitted to edit this topic.");
            }
            topic.setTitle(title);
            topic.setDescription(description);

            // Remove files
            if (filesToDelete != null && topic.getFileUrls() != null) {
                List<String> updatedFileUrls = new ArrayList<>(topic.getFileUrls());
                for (String fileUrl : filesToDelete) {
                    fileStorageService.deleteFile(fileUrl);
                    updatedFileUrls.remove(fileUrl);
                }
                topic.setFileUrls(updatedFileUrls);
            }

            // Add new files
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    topic.getFileUrls().add(url);
                }
            }
            topicRepository.save(topic);
        } else {
            throw new RuntimeException("Room or topic not found");
        }
    }

}
