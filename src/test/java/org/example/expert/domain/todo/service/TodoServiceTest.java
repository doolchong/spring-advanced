package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.expert.domain.TestData.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Nested
    class saveTodoTest {

        @Test
        public void 일정_등록_정상동작() {
            // given
            long todoId = TEST_ID_1;
            long userId = TEST_ID_1;
            AuthUser authUser = TEST_AUTHUSER;
            User user = TEST_USER_1;
            ReflectionTestUtils.setField(user, "id", userId);
            Todo todo = TEST_TODO_1;
            ReflectionTestUtils.setField(todo, "id", todoId);
            TodoSaveRequest request = new TodoSaveRequest(TEST_TITLE_1, TEST_CONTENTS_1);

            given(weatherClient.getTodayWeather()).willReturn(TEST_WEATHER_1);
            given(todoRepository.save(any())).willReturn(todo);

            // when
            TodoSaveResponse result = todoService.saveTodo(authUser, request);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class GetTodos {

        @Test
        public void 일정_다건_조회_정상동작() {
            // given
            Pageable pageable = PageRequest.of(1, 10);
            List<Todo> todoList = new ArrayList<>();
            todoList.add(TEST_TODO_1);
            Page<Todo> todoPage = new PageImpl<>(todoList, pageable, 1);

            given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(todoPage);

            // when
            Page<TodoResponse> todoResponsePage = todoService.getTodos(1, 10);

            // then
            assertThat(todoResponsePage).isNotEmpty();
        }
    }

    @Nested
    class GetTodoTest {

        @Test
        public void 일정_단건_조회_일정_없음() {
            // given
            long todoId = TEST_ID_1;
            long userId = TEST_ID_1;
            User user = TEST_USER_1;
            ReflectionTestUtils.setField(user, "id", userId);
            Todo todo = TEST_TODO_1;
            ReflectionTestUtils.setField(todo, "id", todoId);

            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> todoService.getTodo(todoId));

            // then
            assertThat(exception.getMessage()).isEqualTo("Todo not found");
        }

        @Test
        public void 일정_단건_조회_정상동작() {
            // given
            long todoId = TEST_ID_1;
            long userId = TEST_ID_1;
            User user = TEST_USER_1;
            ReflectionTestUtils.setField(user, "id", userId);
            Todo todo = TEST_TODO_1;
            ReflectionTestUtils.setField(todo, "id", todoId);

            given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

            // when
            TodoResponse todoResponse = todoService.getTodo(todoId);

            // then
            assertNotNull(todoResponse);
            assertThat(todoResponse.getId()).isEqualTo(1);
        }
    }
}