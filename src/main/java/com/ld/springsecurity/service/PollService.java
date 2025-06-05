package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Poll;
import com.ld.springsecurity.model.PollVote;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.PollRepository;
import com.ld.springsecurity.repo.PollVoteRepository;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PollService {
    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;

    public PollService(PollRepository pollRepository, PollVoteRepository pollVoteRepository, UserRepository userRepository, RoomRepository roomRepository, FileStorageService fileStorageService) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.fileStorageService = fileStorageService;
    }

    public void createPoll(String username, String roomId, String title, String description, List<MultipartFile> files, List<String> options, LocalDateTime deadline) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User teacher = optionalUser.get();
            Room room = optionalRoom.get();
            Poll poll = new Poll();
            poll.setTitle(title);
            poll.setDescription(description);
            poll.setCreatedBy(teacher);
            poll.setCreatedAt(LocalDateTime.now());
            poll.setDeadline(deadline);
            poll.setRoom(room);
            poll.setOptions(options);
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    poll.getFileUrls().add(url);
                }
            }
            pollRepository.save(poll);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }
    public List<Poll> getPollsForRoom(String roomId) {
        return pollRepository.findByRoom_Id(roomId);
    }

    public void vote(String pollId, String username, int optionIndex) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Poll> optionalPoll = pollRepository.findById(pollId);
        if (optionalUser.isPresent() && optionalPoll.isPresent()) {
            User user = optionalUser.get();
            Poll poll = optionalPoll.get();
            PollVote vote = pollVoteRepository.findByPoll_IdAndUser_Id(pollId, user.getId())
                    .orElse(new PollVote());
            vote.setPoll(poll);
            vote.setUser(user);
            vote.setOptionIndex(optionIndex);
            pollVoteRepository.save(vote);
        } else {
            throw new RuntimeException("User or poll not found");
        }
    }
    public List<PollVote> getVotes(String pollId) {
        return pollVoteRepository.findByPoll_Id(pollId);
    }

    public void updatePoll(String username,String pollId, String roomId, String title, String description, List<MultipartFile> files, List<String> filesToDelete, List<String> options, LocalDateTime deadline) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Poll> optionalPoll = pollRepository.findById(pollId);
        if (optionalUser.isPresent() && optionalRoom.isPresent() && optionalPoll.isPresent()) {
            User user = optionalUser.get();
            Poll poll = optionalPoll.get();
            Room room = optionalRoom.get();
            if (!poll.getCreatedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not the author of this poll");
            }

            List<String> oldOptions = poll.getOptions();
            List<Integer> remove = new ArrayList<>();
            for (int i = 0; i < oldOptions.size(); i++) {
                String oldOption = oldOptions.get(i);
                if (options.size() <= i || !options.get(i).equals(oldOption)) {
                    remove.add(i);
                }
            }

            if (!remove.isEmpty()) {
                List<PollVote> pollVotes = pollVoteRepository.findByPoll_Id(pollId);
                for (PollVote vote : pollVotes) {
                    if (remove.contains(vote.getOptionIndex())) {
                        pollVoteRepository.delete(vote);
                    }
                }
                for (PollVote vote : pollVotes) {
                    int shift = 0;
                    for (int idx : remove) {
                        if (vote.getOptionIndex() > idx) shift++;
                    }
                    if (shift > 0 && !remove.contains(vote.getOptionIndex())) {
                        vote.setOptionIndex(vote.getOptionIndex() - shift);
                        pollVoteRepository.save(vote);
                    }
                }
            }

            poll.setTitle(title);
            poll.setDescription(description);
            poll.setDeadline(deadline);
            poll.setOptions(options);

            if (filesToDelete != null && poll.getFileUrls() != null) {
                List<String> updatedFileUrls = new ArrayList<>(poll.getFileUrls());
                for (String fileUrl : filesToDelete) {
                    fileStorageService.deleteFile(fileUrl);
                    updatedFileUrls.remove(fileUrl);
                }
                poll.setFileUrls(updatedFileUrls);
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    poll.getFileUrls().add(url);
                }
            }
            pollRepository.save(poll);
        } else {
            throw new RuntimeException("User/room/poll not found");
        }
    }
    @Transactional
    public void deletePoll(String roomId, String pollId, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Poll> optionalPoll = pollRepository.findById(pollId);
        if (optionalUser.isPresent() && optionalRoom.isPresent() && optionalPoll.isPresent()) {
            User user = optionalUser.get();
            Poll poll = optionalPoll.get();
            Room room = optionalRoom.get();
            if (!poll.getCreatedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not the author of this poll");
            }

            if (poll.getFileUrls() != null) {
                for (String url : poll.getFileUrls()) {
                    fileStorageService.deleteFile(url);
                }
            }
            pollVoteRepository.deleteAll(pollVoteRepository.findByPoll_Id(pollId));
            pollRepository.delete(poll);
        } else {
            throw new RuntimeException("User/room/poll not found");
        }
    }
}
