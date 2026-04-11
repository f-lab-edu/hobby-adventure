package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.User;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATED);
        }
        if (userMapper.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.create(request.getEmail(), encodedPassword, request.getNickname());

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.DUPLICATED);
        }
    }
}
