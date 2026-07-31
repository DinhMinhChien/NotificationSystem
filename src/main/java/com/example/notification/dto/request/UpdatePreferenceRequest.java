package com.example.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferenceRequest {
    @NotEmpty(message = "List preferences not empty")
    private List<@NotBlank(message = "preference item not blank") PreferenceItem> preferences;
}
