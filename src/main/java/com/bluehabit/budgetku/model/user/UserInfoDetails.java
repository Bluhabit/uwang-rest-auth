package com.bluehabit.budgetku.model.user;

import com.bluehabit.budgetku.entity.Permission;
import com.bluehabit.budgetku.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoDetails implements UserDetails {
    private final String name;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;

    public UserInfoDetails(UserCredential user) {
        this.name = user.getUserEmail();
        this.password = user.getUserPassword();
        this.authorities = user
                .getUserPermission()
                .stream()
                .map(Permission::getPermissionType)
                .map(SimpleGrantedAuthority::new)
                .toList();

    }

    public List<? extends  GrantedAuthority> getAuth(){
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
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
        return true;
    }
}
