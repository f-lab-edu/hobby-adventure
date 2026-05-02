package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.User;
import com.jian.hobbyadventure.dto.request.LoginRequest;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.dto.response.DeleteAccountResponse;
import com.jian.hobbyadventure.dto.response.LoginResponse;
import com.jian.hobbyadventure.dto.response.UserProfileResponse;
import com.jian.hobbyadventure.repository.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserProfile_성공_시_userId_email_nickname을_반환한다() {
        User user = new User(1L, "test@example.com", "encodedPassword", "닉네임", LocalDateTime.now());
        when(userMapper.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponse response = userService.getUserProfile(1L);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("닉네임");
    }

    @Test
    void getUserProfile_존재하지_않는_userId_시_BusinessException을_던진다() {
        when(userMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void deleteAccount_성공_시_success가_true를_반환한다() {
        User user = new User(1L, "test@example.com", "encodedPassword", "닉네임", LocalDateTime.now());
        when(userMapper.findById(1L)).thenReturn(Optional.of(user));

        DeleteAccountResponse response = userService.deleteAccount(1L);

        assertThat(response.isSuccess()).isTrue();
        verify(userMapper).deleteById(1L);
    }

    @Test
    void deleteAccount_존재하지_않는_userId_시_BusinessException을_던진다() {
        when(userMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteAccount(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);

        verify(userMapper, never()).deleteById(any());
    }

    @Test
    void login_성공_시_userId와_nickname을_반환한다() {
        User user = new User(1L, "test@example.com", "encodedPassword", "닉네임", LocalDateTime.now());
        LoginRequest request = new LoginRequest("test@example.com", "rawPassword");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        LoginResponse response = userService.login(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getNickname()).isEqualTo("닉네임");
    }

    @Test
    void login_존재하지_않는_이메일_시_BusinessException을_던진다() {
        LoginRequest request = new LoginRequest("test@example.com", "rawPassword");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void login_비밀번호_불일치_시_BusinessException을_던진다() {
        User user = new User(1L, "test@example.com", "encodedPassword", "닉네임", LocalDateTime.now());
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void signup_호출_시_비밀번호가_암호화되어_insert된다() {
        SignupRequest request = new SignupRequest("test@example.com", "rawPassword", "닉네임");
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        userService.signup(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void signup_호출_시_userMapper_insert가_1회_호출된다() {
        SignupRequest request = new SignupRequest("test@example.com", "rawPassword", "닉네임");
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        userService.signup(request);

        verify(userMapper).insert(any(User.class));
    }

    @Test
    void signup_이메일_중복_시_BusinessException을_던진다() {
        SignupRequest request = new SignupRequest("test@example.com", "rawPassword", "닉네임");
        when(userMapper.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED);
    }

    @Test
    void signup_닉네임_중복_시_BusinessException을_던진다() {
        SignupRequest request = new SignupRequest("test@example.com", "rawPassword", "닉네임");
        when(userMapper.existsByNickname(request.getNickname())).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED);
    }

    @Test
    void signup_insert_중_DuplicateKeyException_발생_시_BusinessException을_던진다() {
        SignupRequest request = new SignupRequest("test@example.com", "rawPassword", "닉네임");
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        doThrow(new DuplicateKeyException("duplicate")).when(userMapper).insert(any(User.class));

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED);
    }
}
