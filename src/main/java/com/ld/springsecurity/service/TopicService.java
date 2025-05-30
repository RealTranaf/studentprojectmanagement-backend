package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.StudentTopicSelection;
import com.ld.springsecurity.model.Topic;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.StudentTopicSelectionRepository;
import com.ld.springsecurity.repo.TopicRepository;
import com.ld.springsecurity.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public StudentTopicSelection selectExistingTopic(String username, String topicId, String roomId){
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
            selection.setCustom(false);
            return studentTopicSelectionRepository.save(selection);
        } else {
            throw new RuntimeException("User or topic not found");
        }
    }

    public StudentTopicSelection submitCustomTopic(String username, String title, String description, List<MultipartFile> files, String roomId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User student = optionalUser.get();
            Room room = optionalRoom.get();
            Topic newTopic = new Topic();
            newTopic.setTitle(title);
            newTopic.setDescription(description);
            newTopic.setProposedBy(student);
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
            selection.setCustom(true);
            return studentTopicSelectionRepository.save(selection);
        } else {
            throw new RuntimeException("User or room not found");
        }
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

}
