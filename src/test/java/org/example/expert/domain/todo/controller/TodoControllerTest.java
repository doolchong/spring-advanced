package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.GlobalExceptionHandler;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.example.expert.domain.TestData.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @MockBean
    private TodoService todoService;

    @Mock
    private AuthUserArgumentResolver argumentResolver;

    @Autowired
    private TodoController todoController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(todoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(argumentResolver)
                .build();
    }

    @Nested
    class saveTodo {

        @Test
        public void 일정_등록_정상동작() throws Exception {
            // given
            ObjectMapper objectMapper = new ObjectMapper();
            TodoSaveRequest request = new TodoSaveRequest(TEST_TITLE_1, TEST_CONTENTS_1);
            TodoSaveResponse response = new TodoSaveResponse(
                    TEST_ID_1,
                    TEST_TITLE_1,
                    TEST_CONTENTS_1,
                    TEST_WEATHER_1,
                    new UserResponse(TEST_ID_1, TEST_EMAIL_1));

            given(argumentResolver.supportsParameter(any())).willReturn(true);
            given(argumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(TEST_AUTHUSER);
            given(todoService.saveTodo(any(), any())).willReturn(response);

            // when
            ResultActions result = mockMvc.perform(post("/todos")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getTodos {

        @Test
        public void 일정_다건_조회_정상동작() throws Exception {
            // given
            Pageable pageable = PageRequest.of(1, 10);
            given(todoService.getTodos(anyInt(), anyInt())).willReturn(Page.empty(pageable));

            // when
            ResultActions result = mockMvc.perform(get("/todos")
                    .param("page", "1")
                    .param("size", "10"));

            // then
            result.andExpect(status().isOk());
        }

    }

    @Nested
    class getTodo {

        @Test
        public void 일정_단건_조회_정상동작() throws Exception {
            // given
            long todoId = TEST_ID_1;
            TodoResponse todoResponse = TEST_TODO_RESPONSE_1;

            given(todoService.getTodo(anyLong())).willReturn(todoResponse);

            // when
            ResultActions result = mockMvc.perform(get("/todos/{todoId}", todoId));

            // then
            result.andExpect(status().isOk());
        }
    }
}