package com.example.notification.entity;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Template extends BaseEntity{
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id ;

    @Column(name = "code")
    private String code ;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private ChannelType channel ;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "language")
    private String language ;

    @Column(name = "subject")
    private String subject ;

    @Column(name = "content")
    private String content ;

    @Column(name = "is_active")
    private Boolean isActive ;
}
