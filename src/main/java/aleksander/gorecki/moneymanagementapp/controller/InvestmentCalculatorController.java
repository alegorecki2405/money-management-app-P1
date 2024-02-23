package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@AllArgsConstructor
public class InvestmentCalculatorController {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    @GetMapping("/investing-calculator")
    public String investingCalculator(Model model) {
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "investing-calculator";
    }
}
