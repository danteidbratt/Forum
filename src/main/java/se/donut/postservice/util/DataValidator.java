package se.donut.postservice.util;

import se.donut.postservice.exception.PostServiceException;

import static se.donut.postservice.exception.ExceptionType.*;

public class DataValidator {

    public static String validatePostTitle(String title) {
        if (title.length() < 3 || title.length() > 128 || title.trim().length() == 0) {
            throw new PostServiceException(INVALID_TITLE);
        }
        return title.trim();
    }

    public static String validatePostContent(String content) {
        if (content.length() < 1 || content.length() > 512 || content.trim().length() == 0) {
            throw new PostServiceException(INVALID_CONTENT);
        }
        return content.trim();
    }

    public static String validateCommentContent(String content) {
        if (content.length() < 1 || content.length() > 512 || content.trim().length() == 0) {
            throw new PostServiceException(INVALID_CONTENT);
        }
        return content.trim();
    }

    public static String validateForumName(String name) {
        if (name.length() < 4 || name.length() > 32 || name.trim().length() == 0) {
            throw new PostServiceException(INVALID_FORUM_NAME);
        }
        return name.trim();
    }

    public static String validateForumDescription(String description) {
        if (description.length() < 4 || description.length() > 256 || description.trim().length() == 0) {
            throw new PostServiceException(INVALID_FORUM_DESCRIPTION);
        }
        return description.trim();
    }

    public static void validateUsername(String username) {
        if (username.length() < 3 || username.length() > 32) {
            throw new PostServiceException(INVALID_USERNAME);
        }
        if (username.length() != username.replaceAll("\\s*", "").length()) {
            throw new PostServiceException(INVALID_USERNAME);
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 5 || password.length() > 256) {
            throw new PostServiceException(INVALID_PASSWORD);
        }
        if (password.length() != password.replaceAll("\\s*", "").length()) {
            throw new PostServiceException(INVALID_PASSWORD);
        }
    }

}
