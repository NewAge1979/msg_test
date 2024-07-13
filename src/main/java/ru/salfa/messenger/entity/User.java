package ru.salfa.messenger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_phone", columnNames = {"phone"}),
        @UniqueConstraint(name = "uq_nickname", columnNames = {"nickname"})
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 16)
    @NotNull
    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    @Size(max = 512)
    @Column(name = "password_hash", length = 512)
    private String passwordHash;

    @Size(max = 64)
    @Column(name = "last_name", length = 64)
    private String lastName;

    @Size(max = 64)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Size(max = 32)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 32)
    private String nickname;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @NotNull
    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "modified")
    private OffsetDateTime modified;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "phone_is_verified")
    private Boolean phoneIsVerified;

    @Column(name = "email_is_confirmed")
    private Boolean emailIsConfirmed;

    @Column(name = "personal_information", length = Integer.MAX_VALUE)
    private String personalInformation;

    @ManyToMany
    private List<User> blockedContacts = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }

    public void  blockContact(User user) {
        blockedContacts.add(user);
    }

    public boolean isBlockedContact(User user) {
        return blockedContacts.contains(user);
    }

    public void unblockContact(User user) {
        blockedContacts.remove(user);
    }
}