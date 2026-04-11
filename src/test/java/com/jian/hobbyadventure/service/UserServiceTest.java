package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.User;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.repository.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
