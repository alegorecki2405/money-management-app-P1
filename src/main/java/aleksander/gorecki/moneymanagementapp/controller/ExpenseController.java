package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.ExpenseService;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ExpenseController {
    private ExpenseService expenseService;
    private UserService userService;
    private AuthenticationFacade authenticationFacade;

    public ExpenseController(ExpenseService expenseService, UserService userService, AuthenticationFacade authenticationFacade) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping("/expense")
    public String createExpenseForm(Model model) {
        ExpenseDto expenseDto = new ExpenseDto();
        model.addAttribute("expense", expenseDto);
        return "expense";
    }

    @PostMapping("/expense/save")
    public String createExpense(@Valid @ModelAttribute("expense") ExpenseDto expenseDto,
                                 BindingResult result,
                                 Model model) {
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        if (result.hasErrors()) {
            model.addAttribute("expense",expenseDto);
            return "/expense";
        }
        expenseService.create(user,expenseDto);
        return "redirect:/expense?success";
    }

    @GetMapping("/expenses")
    public String expenses(Model model){
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        model.addAttribute("futureExpenses", expenseService.findAllFutureExpensesByUser(user));
        model.addAttribute("previousExpenses", expenseService.findAllPreviousExpensesByUser(user));
        return "expenses";
    }
}
