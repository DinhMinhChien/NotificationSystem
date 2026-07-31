package com.example.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertMemberRequest {
    @NotEmpty(message = "User IDs list cannot be empty")
    @Size(min = 1, max = 1000, message = "Must provide between 1 and 1000 users")
    private List<@NotBlank(message = "User ID cannot be blank") String> userIds;
}
