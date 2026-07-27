package com.example.notification.repository;

import com.example.notification.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,String> {
}
