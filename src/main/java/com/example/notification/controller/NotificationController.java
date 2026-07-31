package com.example.notification.controller;

import com.example.notification.common.BaseResponse;
import com.example.notification.dto.response.NotificationDashboard;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService ;

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getByUser(@PathVariable String userId,
                                                                             @RequestParam(required = false) int pageNumber,
                                                                             @RequestParam(required = false) int pageSize) {
        Page<NotificationResponse> responses = notificationService.getByUser(userId,pageNumber,pageSize) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(responses)) ;
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponse<NotificationDashboard>> summary() {
        NotificationDashboard response = notificationService.summary() ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(response,"Load summary notification success"));
    }
}
