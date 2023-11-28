package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.ExpenseService;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    public ExpenseController(ExpenseService expenseService, UserService userService, AuthenticationFacade authenticationFacade) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping("/expense")
    public String createExpenseForm(Model model) {
        model.addAttribute("expense", new ExpenseDto());
        return "expense";
    }

    @PostMapping("/expense/save")
    public String createExpense(@Valid @ModelAttribute("expense") ExpenseDto expenseDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "expense";
        }

        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        expenseService.create(user, expenseDto);
        redirectAttributes.addFlashAttribute("successMessage", "Expense added successfully");
        return "redirect:/expense";
    }

    @GetMapping("/expenses")
    public String expenses(Model model) {
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        model.addAttribute("futureExpenses", expenseService.findAllFutureExpensesByUser(user));
        model.addAttribute("previousExpenses", expenseService.findAllPreviousExpensesByUser(user));
        return "expenses";
    }

    @PutMapping("/update-expense-date/{expenseId}")
    public ResponseEntity<String> updateExpenseDate(@PathVariable Long expenseId) {
        try {
            Expense expense = expenseService.findById(expenseId); // Implement this method in ExpenseService
            if (expense != null) {
                expense.setDate(new Date()); // Set the expense date to the current date
                expenseService.saveOrUpdate(expense); // Save the updated expense
                return ResponseEntity.ok("Expense date updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update expense date");
        }
    }
}
