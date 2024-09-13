package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.expert.domain.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private ManagerRepository managerRepository;
    @InjectMocks
    private CommentService commentService;

    @Nested
    class saveComment {

        @Test
        public void 댓글_등록_중_할일을_찾지_못해_에러발생() {
            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            AuthUser authUser = TEST_AUTHUSER;

            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                    () -> commentService.saveComment(authUser, todoId, request)
            );

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        public void 댓글_등록_중_담당자가_아닌_경우_에러발생() {
            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            AuthUser authUser = TEST_AUTHUSER;
            Todo todo = TEST_TODO_1;
            ReflectionTestUtils.setField(todo, "id", TEST_ID_1);

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(managerRepository.findByTodo_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                commentService.saveComment(authUser, todoId, request);
            });

            // then
            assertEquals("담당자가 아니면 댓글을 달 수 없습니다.", exception.getMessage());
        }

        @Test
        public void 댓글을_정상적으로_등록한다() {
            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            AuthUser authUser = TEST_AUTHUSER;
            User user = User.fromAuthUser(authUser);
            Todo todo = TEST_TODO_1;
            ReflectionTestUtils.setField(todo, "id", TEST_ID_1);
            Manager manager = new Manager(user, todo);
            Comment comment = new Comment(request.getContents(), user, todo);

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(managerRepository.findByTodo_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.of(manager));
            given(commentRepository.save(any())).willReturn(comment);

            // when
            CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

            // then
            assertNotNull(result);
        }
    }

    @Nested
    class getComments {

        @Test
        public void 댓글_다건_조회_정상작동() {
            // given
            List<Comment> commentList = new ArrayList<>();
            commentList.add(TEST_COMMENT_1);
            given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(commentList);

            // when
            List<CommentResponse> commentResponseList = commentService.getComments(TEST_ID_1);

            // then
            assertThat(commentResponseList).isNotEmpty();
        }
    }
}
