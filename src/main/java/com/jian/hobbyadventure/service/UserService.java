package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.domain.User;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), encodedPassword, request.getNickname());
        userMapper.insert(user);
    }
}
