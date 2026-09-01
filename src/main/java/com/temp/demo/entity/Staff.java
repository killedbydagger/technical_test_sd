package com.temp.demo.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@Table(name = "`staff`")
public class Staff implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String picture;
    private boolean active;
    private boolean enabled;
    private String lastUpdate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return getPermission()
//                .stream()
//                .map(authority ->
//                        new SimpleGrantedAuthority(authority.getName())
//                ).collect(Collectors.toList());
        return new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.FALSE;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
