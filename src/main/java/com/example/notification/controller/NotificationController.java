package com.example.notification.controller;

import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService ;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUser(@PathVariable String userId) {
        List<NotificationResponse> responses = notificationService.getByUser(userId) ;
        return ResponseEntity.ok(responses) ;
    }
}
