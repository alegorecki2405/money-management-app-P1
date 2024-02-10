package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.service.UserProfileService;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@Controller
@RequestMapping("/")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/index")
    public String showIndexPage(Model model) {
        userProfileService.createModelForUserProfile(model);
        return "index";
    }
}
