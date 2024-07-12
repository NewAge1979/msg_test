package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chatId;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User senderId;
    @ManyToMany
    @ToString.Exclude
    private List<User> userDeleteMessage = new ArrayList<>();
    private boolean isDelete = false;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified;
    @OneToMany(mappedBy = "message")
    @ToString.Exclude
    private List<Attachments> attachments = new ArrayList<>();

    public void addUserDeleters(User user) {
        userDeleteMessage.add(user);
    }

    public void addAttachments(Attachments att) {
        attachments.add(att);
        att.setMessage(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Messages messages = (Messages) o;
        return getId() != null && Objects.equals(getId(), messages.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
