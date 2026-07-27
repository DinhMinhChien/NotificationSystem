package com.example.notification.repository;

import com.example.notification.entity.Group;
import com.example.notification.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember,String> {
    List<GroupMember> findAllByGroup(Group group);
}
