package com.campus.marketplace.service;

import com.campus.marketplace.dto.request.UpdateProfileRequest;
import com.campus.marketplace.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse getMe(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    List<UserResponse> search(String email, String query);

    UserResponse getById(String id);

    UserResponse uploadAvatar(String email, MultipartFile file);
}
