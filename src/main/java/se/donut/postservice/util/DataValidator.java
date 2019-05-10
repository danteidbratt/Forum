package se.donut.postservice.util;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.User;

import static se.donut.postservice.exception.ExceptionType.*;

public class DataValidator {

    public static void validatePost(Post post) {
        int titleLength = post.getTitle().length();
        if (titleLength == 0 || titleLength > 128) {
            throw new PostServiceException(INVALID_TITLE);
        }
    }

    public static void validateComment(Comment comment) {
        int contentLength = comment.getContent().length();
        if (contentLength == 0 || contentLength > 512) {
            throw new PostServiceException(INVALID_CONTENT);
        }
    }

    public static void validateForum(Forum forum) {
        int nameLength = forum.getName().length();
        if (nameLength < 4 || nameLength > 32) {
            throw new PostServiceException(INVALID_FORUM_NAME);
        }
    }

    public static void validateUsername(String username) {
        int nameLength = username.length();
        if (nameLength < 3 || nameLength > 32) {
            throw new PostServiceException(INVALID_USERNAME);
        }
        if (nameLength != username.replaceAll("\\s*", "").length()) {
            throw new PostServiceException(INVALID_USERNAME);
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 5) {
            throw new PostServiceException(INVALID_PASSWORD);
        }
        if (password.length() != password.replaceAll("\\s*", "").length()) {
            throw new PostServiceException(INVALID_PASSWORD);
        }
    }
}
