package com.example.notification.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@Getter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "meta",
        "data"
})
public class BaseResponse<T> {
    private T data;
    private Metadata meta = new Metadata();

    public static <T> BaseResponse<T> ofSuccess(T data,String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.data = data;
        response.meta.code = HttpStatus.OK.value();
        response.meta.message = message ;
        return response;
    }

    public static <T> BaseResponse<List<T>> ofSuccess(Page<T> page) {
        BaseResponse<List<T>> response = new BaseResponse<>();
        response.data = page.getContent();
        response.meta.page = page.getNumber() + 1;
        response.meta.size = page.getSize();
        response.meta.total = page.getTotalElements();
        response.meta.code = HttpStatus.OK.value();
        return response;
    }

    public static <T> BaseResponse<T> ofDeleteSuccess(String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.meta.code = HttpStatus.NO_CONTENT.value();
        response.meta.message = message ;
        return response;
    }

    public static <T> BaseResponse<T> ofSuccess(String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.meta.message = message;
        response.meta.code = HttpStatus.OK.value();
        return response;
    }

    public static BaseResponse<String> ofSuccessDataMessage(String message) {
        BaseResponse<String> response = new BaseResponse<>();
        response.data = message;
        response.meta.code = HttpStatus.OK.value();
        return response;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Metadata {
        private int code;
        private Integer page;
        private Integer size;
        private Long total;
        private List<FieldViolation> errors;
        private String message;
        private String requestId;
    }
}
