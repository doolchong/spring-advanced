package org.example.expert.domain;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;

import java.time.LocalDateTime;

public class TestData {

    // Id
    public final static Long TEST_ID_1 = 1L;
    public final static Long TEST_ID_2 = 2L;

    // User
    public final static String TEST_EMAIL_1 = "test1@email.com";
    public final static String TEST_PASSWORD_1 = "test1234";
    public final static String TEST_PASSWORD_2 = "test5678";
    public final static String TEST_PASSWORD_3 = "test12345678";
    public final static User TEST_USER_1 = new User(TEST_EMAIL_1, TEST_PASSWORD_1, UserRole.USER);
    public final static UserResponse TEST_USER_RESPONSE_1 = new UserResponse(TEST_ID_1, TEST_EMAIL_1);

    public final static AuthUser TEST_AUTHUSER = new AuthUser(TEST_ID_1, TEST_EMAIL_1, UserRole.USER);

    // Todo
    public final static String TEST_TITLE_1 = "title1";
    public final static String TEST_CONTENTS_1 = "contents1";
    public final static String TEST_WEATHER_1 = "Sunny";
    public final static Todo TEST_TODO_1 = new Todo(TEST_TITLE_1, TEST_CONTENTS_1, TEST_WEATHER_1, TEST_USER_1);
    public final static TodoSaveResponse TEST_TODO_SAVE_RESPONSE_1 = new TodoSaveResponse(TEST_ID_1, TEST_TITLE_1, TEST_CONTENTS_1, TEST_WEATHER_1, TEST_USER_RESPONSE_1);
    public final static TodoResponse TEST_TODO_RESPONSE_1 = new TodoResponse(TEST_ID_1, TEST_TITLE_1, TEST_CONTENTS_1, TEST_WEATHER_1, TEST_USER_RESPONSE_1, LocalDateTime.now(), LocalDateTime.now());

    // Comment
    public final static Comment TEST_COMMENT_1 = new Comment(TEST_CONTENTS_1, TEST_USER_1, TEST_TODO_1);

    // Manager
    public final static Manager TEST_MANAGER_1 = new Manager(TEST_USER_1, TEST_TODO_1);
}
