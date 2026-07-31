package com.example.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest implements Serializable {
    @NotBlank(message = "Name's group not null or empty")
    private String name ;

    private String description ;
}
