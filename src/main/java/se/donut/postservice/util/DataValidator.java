package se.donut.postservice.util;

import se.donut.postservice.exception.PostServiceException;

import static se.donut.postservice.exception.ExceptionType.*;

public class DataValidator {

    public static String validatePostTitle(String title) {
        return title.trim().replaceAll(" {2,}", " ");
    }

    public static String validatePostContent(String content) {
        return content.trim().replaceAll(" {2,}", " ");
    }

    public static String validateCommentContent(String content) {
        return content.trim().replaceAll(" {2,}", " ");
    }

    public static void validateForumName(String name) {
        if (!name.matches("\\A[A-Za-z0-9]+\\z")) {
            throw new PostServiceException(
                    INVALID_FORUM_NAME,
                    "Forum name may only contain alphanumeric characters."
            );
        }
    }

    public static String validateForumDescription(String description) {
        return description.trim().replaceAll(" {2,}", " ");
    }

    public static void validateUsername(String username) {
        if (!username.matches("\\A[A-Za-z0-9_\\-]+\\z")) {
            throw new PostServiceException(
                    INVALID_USERNAME,
                    "Username may only contain letters, numbers, hyphens and underscores");
        }
    }

    public static void validatePassword(String password) {
        if (password.matches(".*\\s+.*")) {
            throw new PostServiceException(
                    INVALID_PASSWORD,
                    "Password must not contain whitespace characters."
            );
        }
    }

}
