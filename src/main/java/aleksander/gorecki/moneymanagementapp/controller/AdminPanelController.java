package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;


import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    public AdminPanelController(AuthenticationFacade authenticationFacade, UserService userService) {
        this.authenticationFacade = authenticationFacade;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        return "users";
    }

    @DeleteMapping("/delete/{userEmail}")
    public ResponseEntity<String> deleteUser(@PathVariable String userEmail){
        try {
            User user = userService.findUserByEmail(userEmail);
            if (user != null) {
                userService.delete(user);
                return ResponseEntity.ok("User successfully deleted");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update expense date");
        }
    }
}
