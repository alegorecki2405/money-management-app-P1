package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserPanelController {

    private UserService userService;

    public UserPanelController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/expenses")
//    public String profile(Model model, Principal principal){
//        UserDto userDto = userService.findUserByEmailToDto(principal.getName());
//        model.addAttribute("user", userDto);
//        return "expenses";
//    }
}
