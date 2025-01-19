package api.fitnessbuddyback.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends User {

    private final String provider;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String provider) {
        super(username, password != null ? password : "", authorities);
        this.provider = provider;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !"LOCAL".equals(provider) || super.isCredentialsNonExpired();
    }
}