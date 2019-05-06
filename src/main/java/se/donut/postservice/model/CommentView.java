package se.donut.postservice.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CommentView {

    private UUID uuid;
    private UUID authorUuid;
    private String authorName;
    private String content;
    private int score;
    private Date createdAt;
    private Direction myVote;

}
