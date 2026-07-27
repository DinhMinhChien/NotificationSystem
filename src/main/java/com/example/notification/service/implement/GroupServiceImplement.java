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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImplement implements GroupService {

    private final GroupRepository groupRepository ;
    private final GroupMapper groupMapper ;
    private final UserRepository userRepository ;
    private final GroupMemberRepository groupMemberRepository ;

    @Override
    public GroupResponse create(GroupCreateRequest request) {
        Group group = new Group() ;

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        Group groupSave = groupRepository.save(group) ;
        return groupMapper.toResponse(groupSave) ;
    }

    @Override
    public List<GroupResponse> getAll() {
        List<Group> groups = groupRepository.findAll() ;
        return groupMapper.toResponse(groups) ;
    }

    @Override
    public void insertMember(String id, InsertMemberRequest request) {
        Optional<Group> existGroup = groupRepository.findById(id) ;

        if (existGroup.isEmpty()) {
            throw new BusinessException("Group not exist") ;
        }

        List<String> userIds = request.getUserIds() ;
        List<GroupMember> groupMembers = new ArrayList<>() ;
        for (String userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId) ;
            if (userOptional.isEmpty()) {
                throw new BusinessException("Not exist user") ;
            }
            GroupMember groupMember = new GroupMember() ;
            groupMember.setGroup(existGroup.get());
            groupMember.setUser(userOptional.get());
            groupMembers.add(groupMember) ;
        }

        groupMemberRepository.saveAll(groupMembers) ;
    }

    @Override
    public List<User> getMember(String id) {
        Optional<Group> groupOptional = groupRepository.findById(id) ;
        if (groupOptional.isEmpty()) {
            throw new BusinessException("Not exist group");
        }

        Group group = groupOptional.get() ;
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group) ;
        List<User> users = groupMembers.stream().map(GroupMember::getUser).toList() ;

        return users ;
    }
}
