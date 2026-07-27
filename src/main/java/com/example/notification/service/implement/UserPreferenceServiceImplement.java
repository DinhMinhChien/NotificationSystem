package com.example.notification.service.implement;

import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.request.PreferenceItem;
import com.example.notification.dto.request.UpdatePreferenceRequest;
import com.example.notification.dto.response.UserPreferenceResponse;
import com.example.notification.entity.User;
import com.example.notification.entity.UserPreference;
import com.example.notification.mapper.UserPreferenceMapper;
import com.example.notification.repository.UserPreferenceRepository;
import com.example.notification.repository.UserRepository;
import com.example.notification.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferenceServiceImplement implements UserPreferenceService {

    private final UserRepository userRepository ;
    private final UserPreferenceRepository userPreferenceRepository ;
    private final UserPreferenceMapper userPreferenceMapper ;

    @Override
    public List<UserPreferenceResponse> getPreferences(String userId) {
        Optional<User> userOptional = userRepository.findById(userId) ;
        if (userOptional.isEmpty()) {
            throw new BusinessException("User not exist") ;
        }

        List<UserPreference> userPreferences = userPreferenceRepository.findAllByUser(userOptional.get()) ;
        return userPreferenceMapper.toResponse(userPreferences) ;

    }

    @Override
    public void update(String userId, UpdatePreferenceRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException("User not exist") ;
        }

        List<UserPreference> existingPreferences = userPreferenceRepository.findAllByUser(userOptional.get());

        Map<String, UserPreference> preferenceMap = existingPreferences.stream()
                .collect(Collectors.toMap(
                        p -> p.getNotificationType().name() + "_" + p.getChannel().name(),
                        Function.identity()
                ));

        List<UserPreference> preferencesToSave = new ArrayList<>();

        for (PreferenceItem item : request.getPreferences()) {
            String key = item.getNotiType().name() + "_" + item.getChannel().name();
            UserPreference preference = preferenceMap.get(key);

            if (preference == null) {
                preference = new UserPreference();
                preference.setUser(userOptional.get());
                preference.setNotificationType(item.getNotiType());
                preference.setChannel(item.getChannel());
            }

            preference.setIsEnabled(item.getEnabled());
            preferencesToSave.add(preference);
        }
        userPreferenceRepository.saveAll(preferencesToSave);
    }
}
