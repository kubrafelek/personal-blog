package devrabbit.personalblog.security;

import devrabbit.personalblog.model.Role;
import devrabbit.personalblog.model.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class UserDetail implements UserDetails {

    private final String username;
    private final String password;
    private final Set<Role> roles;
    private final UserStatus userStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !(userStatus.equals(UserStatus.LOCKED));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userStatus.equals(UserStatus.ACTIVE);
    }
}