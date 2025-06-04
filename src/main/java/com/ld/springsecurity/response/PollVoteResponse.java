package com.ld.springsecurity.response;

import com.ld.springsecurity.model.PollVote;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PollVoteResponse {
    private String id;
    private PollResponse poll;
    private String username;
    private int optionIndex;

    public PollVoteResponse(PollVote vote) {
        this.id = vote.getId();
        this.poll = new PollResponse(vote.getPoll());
        this.username = vote.getUser().getUsername();
        this.optionIndex = vote.getOptionIndex();
    }
}
