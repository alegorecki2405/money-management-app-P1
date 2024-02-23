package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Data
public class AuthController {
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null) {
            result.rejectValue("email", null, "An account with this email already exists");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.saveUser(userDto);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful");
        return "redirect:/register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "login";
    }
}
