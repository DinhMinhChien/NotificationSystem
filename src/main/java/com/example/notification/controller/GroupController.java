package com.example.notification.controller;

import com.example.notification.common.BaseResponse;
import com.example.notification.dto.request.GroupCreateRequest;
import com.example.notification.dto.request.InsertMemberRequest;
import com.example.notification.dto.response.GroupResponse;
import com.example.notification.entity.User;
import com.example.notification.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService ;

    @PostMapping
    public ResponseEntity<BaseResponse<GroupResponse>> create(@RequestBody GroupCreateRequest request) {
        GroupResponse response = groupService.create(request) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(response,"Create group success")) ;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<GroupResponse>>> getAll() {
        List<GroupResponse> responses = groupService.getAll() ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(responses,"Get all groups success"));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<BaseResponse<String>> insertMember(@RequestBody InsertMemberRequest request, @PathVariable String id) {
        groupService.insertMember(id,request) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess("Insert member in group success")) ;
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<BaseResponse<List<User>>> getMemberInGroup(@PathVariable String id) {
        List<User> users = groupService.getMember(id) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(users,"Get users in group success")) ;
    }

}
