package aleksander.gorecki.moneymanagementapp.config;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Authentication getAuth();
}
