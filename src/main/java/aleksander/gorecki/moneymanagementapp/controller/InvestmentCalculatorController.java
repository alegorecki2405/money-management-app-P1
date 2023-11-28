package aleksander.gorecki.moneymanagementapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InvestmentCalculatorController {

    @GetMapping("/investing-calculator")
    public String investingCalculator() {
        return "investing-calculator";
    }
}
