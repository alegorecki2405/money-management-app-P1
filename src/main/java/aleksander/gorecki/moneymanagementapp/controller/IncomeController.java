package aleksander.gorecki.moneymanagementapp.controller;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.IncomeDto;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.service.IncomeService;
import aleksander.gorecki.moneymanagementapp.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
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
import java.time.LocalDate;

@Data
@Controller
public class IncomeController {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final IncomeService incomeService;

    @GetMapping("/income")
    public String createIncomeForm(Model model) {
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        model.addAttribute("income", new IncomeDto());
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        model.addAttribute("incomeTypes", incomeService.findAllTypesByUser(user));
        return "income";
    }

    @PostMapping("/income/save")
    public String createIncome(@Valid @ModelAttribute("income") IncomeDto incomeDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userRole", authenticationFacade.getHighestRole());
            return "income";
        }

        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        incomeService.create(user, incomeDto);
        redirectAttributes.addFlashAttribute("successMessage", "Income added successfully");
        return "redirect:/income";
    }

    @GetMapping("/incomes")
    public String incomes(Model model,
                          @RequestParam(required = false) String typeFilter,
                          @RequestParam(required = false) BigDecimal maxAmount,
                          @RequestParam(required = false) BigDecimal minAmount,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
                          @RequestParam(required = false) String timePeriod) {
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        incomeService.createModelForIncomesTemplate(model, user, typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod);
        return "/incomes";
    }

    @PostMapping("/incomes/applyFilters")
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

        return "redirect:/incomes";
    }

    @PutMapping("/update-income-date/{incomeId}")
    public ResponseEntity<String> updateIncomeDate(@PathVariable Long incomeId) {
        try {
            Income income = incomeService.findById(incomeId);
            if (income != null) {
                income.setDate(LocalDate.now());
                incomeService.saveOrUpdate(income);
                return ResponseEntity.ok("Income date updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update income date");
        }
    }
}
