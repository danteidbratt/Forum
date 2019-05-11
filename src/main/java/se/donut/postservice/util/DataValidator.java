package se.donut.postservice.util;

import se.donut.postservice.exception.PostServiceException;

import static se.donut.postservice.exception.ExceptionType.*;

public class DataValidator {

    public static String validatePostTitle(String title) {
        title = title.trim().replaceAll("\\s+", " ");
        if (title.length() < 1 || title.length() > 128) {
            throw new PostServiceException(
                    INVALID_TITLE,
                    "Post title length must be between 3 and 128 characters."
            );
        }
        return title;
    }

    public static String validatePostContent(String content) {
        content = content.trim().replaceAll(" {2,}", " ");
        if (content.length() > 512) {
            throw new PostServiceException(
                    INVALID_CONTENT,
                    "Post content length must be between 1 and 512 characters."
            );
        }
        return content;
    }

    public static String validateCommentContent(String content) {
        content = content.trim().replaceAll(" {2,}", " ");
        if (content.length() < 1 || content.length() > 512) {
            throw new PostServiceException(
                    INVALID_CONTENT,
                    "Comment length must be between 1 and 512 characters."
            );
        }
        return content;
    }

    public static void validateForumName(String name) {
        if (!name.matches("\\A[A-Za-z]+\\z")) {
            throw new PostServiceException(
                    INVALID_FORUM_NAME,
                    "Forum name may only contain alphabetic letters."
            );
        }

        if (name.length() < 3 || name.length() > 32) {
            throw new PostServiceException(
                    INVALID_FORUM_NAME,
                    "Forum name length must be between 3 and 32 characters."
            );
        }
    }

    public static String validateForumDescription(String description) {
        description = description.trim().replaceAll(" {2,}", " ");
        if (description.length() > 256) {
            throw new PostServiceException(
                    INVALID_FORUM_DESCRIPTION,
                    "Forum description must not be longer that 256 characters."
            );
        }
        return description;
    }

    public static void validateUsername(String username) {
        if (username.length() < 3 || username.length() > 32) {
            throw new PostServiceException(
                    INVALID_USERNAME,
                    "Username length must be between 3 and 32 characters."
            );
        }

        if (username.length() != username.replaceAll("\\s+", "").length()) {
            throw new PostServiceException(
                    INVALID_USERNAME,
                    "Username must not contain whitespace characters.");
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 5 || password.length() > 256) {
            throw new PostServiceException(
                    INVALID_PASSWORD,
                    "Password length must be between 5 and 256 characters.");
        }
        if (password.length() != password.replaceAll("\\s+", "").length()) {
            throw new PostServiceException(
                    INVALID_PASSWORD,
                    "Password must not contain whitespace characters."
            );
        }
    }

}
