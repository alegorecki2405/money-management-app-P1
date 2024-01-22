package aleksander.gorecki.moneymanagementapp.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getHighestRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_NONE");
    }
}
