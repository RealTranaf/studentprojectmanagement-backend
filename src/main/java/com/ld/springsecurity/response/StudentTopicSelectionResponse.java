package com.ld.springsecurity.response;

import com.ld.springsecurity.model.StudentTopicSelection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentTopicSelectionResponse {
    private String id;
    private String student;
    private TopicResponse topic;
    private String roomId;
    private boolean isVerified;
    private boolean isCustom;

    public StudentTopicSelectionResponse(StudentTopicSelection selection) {
        this.id = selection.getId();
        this.student = selection.getStudent().getUsername();
        this.topic = new TopicResponse(selection.getTopic());
        this.isVerified = selection.isVerified();
        this.isCustom = selection.isCustom();
        this.roomId = selection.getRoom().getId();
    }
}
