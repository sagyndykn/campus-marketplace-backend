package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.UpdateProfileRequest;
import com.campus.marketplace.dto.response.UserResponse;
import com.campus.marketplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getMe(email));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> search(
            @AuthenticationPrincipal String email,
            @RequestParam String q) {
        return ResponseEntity.ok(userService.search(email, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<UserResponse> uploadAvatar(
            @AuthenticationPrincipal String email,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(email, file));
    }
}
