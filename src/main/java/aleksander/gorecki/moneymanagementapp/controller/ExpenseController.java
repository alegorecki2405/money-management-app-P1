package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.ExpenseService;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        model.addAttribute("expense", new ExpenseDto());
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        model.addAttribute("expenseTypes", expenseService.findAllTypesByUser(user));
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
    public String expenses(Model model,
                           @RequestParam(required = false) String typeFilter,
                           @RequestParam(required = false) BigDecimal maxAmount,
                           @RequestParam(required = false) BigDecimal minAmount,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                           @RequestParam(required = false) String timePeriod) {
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        expenseService.createModelForExpensesTemplate(model, user, typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod);
        return "/expenses";
    }

    @PostMapping("/applyFilters")
    public String applyFiltersAndRedirect(@RequestParam(required = false) String typeFilter,
                                          @RequestParam(required = false) BigDecimal maxAmount,
                                          @RequestParam(required = false) BigDecimal minAmount,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          @RequestParam(required = false) String timePeriod,
                                          RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("typeFilter", typeFilter);
        redirectAttributes.addAttribute("maxAmount", maxAmount);
        redirectAttributes.addAttribute("minAmount", minAmount);
        redirectAttributes.addAttribute("startDate", startDate);
        redirectAttributes.addAttribute("endDate", endDate);
        redirectAttributes.addAttribute("timePeriod", timePeriod);

        return "redirect:/expenses";
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
