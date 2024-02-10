package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Data
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    @Override
    public void createModelForUserProfile(Model model) {
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
//        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
//        model.addAttribute("balance", user.getBalance());
    }
}
