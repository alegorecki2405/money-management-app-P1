package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
public class InvestmentCalculatorController {

    private final AuthenticationFacade authenticationFacade;

    public InvestmentCalculatorController(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping("/investing-calculator")
    public String investingCalculator(Model model) {
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        return "investing-calculator";
    }
}
