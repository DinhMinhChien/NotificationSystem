package com.example.notification.service.implement;

import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.request.GroupCreateRequest;
import com.example.notification.dto.request.InsertMemberRequest;
import com.example.notification.dto.response.GroupResponse;
import com.example.notification.entity.Group;
import com.example.notification.entity.GroupMember;
import com.example.notification.entity.User;
import com.example.notification.mapper.GroupMapper;
import com.example.notification.repository.GroupMemberRepository;
import com.example.notification.repository.GroupRepository;
import com.example.notification.repository.UserRepository;
import com.example.notification.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImplement implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public GroupResponse create(GroupCreateRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return groupMapper.toResponse(groupRepository.save(group));
    }

    @Override
    public List<GroupResponse> getAll() {
        return groupMapper.toResponse(groupRepository.findAll());
    }

    @Override
    public void insertMember(String id, InsertMemberRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Group not exist"));

        List<String> userIds = request.getUserIds();
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // ✅ 1 Query duy nhất bằng IN(...) để giải quyết lỗi N+1
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new BusinessException("One or more userIds do not exist");
        }

        // Lấy danh sách member hiện tại để tránh lưu trùng
        Set<String> existingUserIds = groupMemberRepository.findAllByGroup(group).stream()
                .map(gm -> gm.getUser().getId())
                .collect(Collectors.toSet());

        List<GroupMember> newMembers = users.stream()
                .filter(u -> !existingUserIds.contains(u.getId()))
                .map(user -> {
                    GroupMember member = new GroupMember();
                    member.setGroup(group);
                    member.setUser(user);
                    return member;
                })
                .toList();

        if (!newMembers.isEmpty()) {
            groupMemberRepository.saveAll(newMembers);
        }
    }

    @Override
    public List<User> getMember(String id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Not exist group"));

        return groupMemberRepository.findAllByGroup(group).stream()
                .map(GroupMember::getUser)
                .toList();
    }
}