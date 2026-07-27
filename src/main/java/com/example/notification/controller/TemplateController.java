package com.example.notification.controller;

import com.example.notification.common.BaseResponse;
import com.example.notification.common.enums.ChannelType;
import com.example.notification.dto.request.TemplateCreateRequest;
import com.example.notification.dto.request.TemplateUpdateRequest;
import com.example.notification.dto.response.TemplateResponse;
import com.example.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService ;

    @PostMapping
    public ResponseEntity<BaseResponse<TemplateResponse>> create(@RequestBody TemplateCreateRequest request) {
        TemplateResponse response = templateService.create(request) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(response,"Create template success")) ;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<TemplateResponse>>> getAll(@RequestParam(required = false) String keyword,
                                                                       @RequestParam(required = false)ChannelType channel,
                                                                       @RequestParam(required = false) String language) {
        List<TemplateResponse> responses = templateService.getAll(keyword,channel,language) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(responses,"Find template success")) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> update(@PathVariable String id,@RequestBody TemplateUpdateRequest request) {
        templateService.update(id,request) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess("Update template success")) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable String id) {
        templateService.delete(id) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess("Delete template success")) ;
    }
}
