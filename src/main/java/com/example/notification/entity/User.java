package com.example.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id ;

    @Column(name = "full_name")
    private String fullName ;

    @Column(name = "email")
    private String email ;

    @Column(name = "phone")
    private String phone ;

    @Column(name = "device_token")
    private String deviceToken ;

    @Column(name = "is_active")
    private Boolean isActive ;


}
