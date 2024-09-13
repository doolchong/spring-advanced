package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.expert.domain.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class getUser {

        @Test
        public void 유저_단건_조회() {
            // given
            User user = TEST_USER_1;
            ReflectionTestUtils.setField(user, "id", TEST_ID_1);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            UserResponse userResponse = userService.getUser(TEST_ID_1);

            // then
            assertThat(userResponse.getId()).isEqualTo(user.getId());
        }
    }

    @Nested
    class changePassword {

        @Test
        void 비밀번호_변경_중_기존_비밀번호와_같은_경우_에러발생() {
            // given
            long userId = TEST_ID_1;

            UserChangePasswordRequest request = new UserChangePasswordRequest(
                    TEST_PASSWORD_1, TEST_PASSWORD_1
            );

            String encodedOldPw = passwordEncoder.encode(TEST_PASSWORD_1);
            User user = new User(TEST_EMAIL_1, encodedOldPw, UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                    () -> userService.changePassword(userId, request));

            // then
            assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
        }

        @Test
        void 비밀번호_변경_중_기존_비밀번호와_다른_경우_에러발생() {
            // given
            long userId = TEST_ID_1;

            UserChangePasswordRequest request = new UserChangePasswordRequest(
                    TEST_PASSWORD_1, TEST_PASSWORD_2
            );

            String encodedOldPw = passwordEncoder.encode(TEST_PASSWORD_3);
            User user = new User(TEST_EMAIL_1, encodedOldPw, UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                    () -> userService.changePassword(userId, request));

            // then
            assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        }

        @Test
        void 비밀번호_변경_정상작동() {
            // given
            long userId = TEST_ID_1;

            UserChangePasswordRequest request = new UserChangePasswordRequest(
                    TEST_PASSWORD_1, TEST_PASSWORD_2
            );

            String encodedOldPw = passwordEncoder.encode(TEST_PASSWORD_1);
            User user = new User(TEST_EMAIL_1, encodedOldPw, UserRole.USER);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            userService.changePassword(userId, request);

            // then
            verify(userRepository, times(1)).findById(userId);
        }
    }
}