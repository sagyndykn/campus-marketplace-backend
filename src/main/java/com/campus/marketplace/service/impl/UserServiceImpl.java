package com.campus.marketplace.service.impl;

import com.campus.marketplace.dto.request.UpdateProfileRequest;
import com.campus.marketplace.dto.response.UserResponse;
import com.campus.marketplace.model.User;
import com.campus.marketplace.repository.UserRepository;
import com.campus.marketplace.service.MinioService;
import com.campus.marketplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public UserResponse getMe(String email) {
        return toResponse(findByEmail(email));
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        user.setUpdatedAt(LocalDateTime.now());

        return toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> search(String email, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        User me = findByEmail(email);
        String escaped = query.replaceAll("[\\[\\](){}.*+?^$|\\\\]", "\\\\$0");

        return userRepository.searchUsers(escaped, PageRequest.of(0, 10))
                .stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return toResponse(user);
    }

    @Override
    public UserResponse uploadAvatar(String email, MultipartFile file) {
        User user = findByEmail(email);

        if (user.getAvatarUrl() != null) {
            minioService.delete(user.getAvatarUrl());
        }

        String url = minioService.upload(file, "avatars");
        user.setAvatarUrl(url);
        user.setUpdatedAt(LocalDateTime.now());

        return toResponse(userRepository.save(user));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isVerified(user.isVerified())
                .build();
    }
}
