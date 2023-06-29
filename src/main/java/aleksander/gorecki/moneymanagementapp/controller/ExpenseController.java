package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.ExpenseService;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ExpenseController {
    private ExpenseService expenseService;
    private UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @PostMapping("/expenses/create")
    public Expense createExpense(@Valid @ModelAttribute("expense") ExpenseDto expenseDto, Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        return expenseService.create(user,expenseDto);
    }

    @GetMapping("/expenses")
    public String expenses(Model model, Principal principal){
        UserDto userDto = userService.findUserByEmailToDto(principal.getName());
        model.addAttribute("user", userDto);
        return "expenses";
    }

}
