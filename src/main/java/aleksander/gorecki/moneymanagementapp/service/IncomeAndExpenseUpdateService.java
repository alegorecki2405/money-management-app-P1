package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.User;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Data
public class IncomeAndExpenseUpdateService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;


    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void doSomething() {
        updateExpenses(expenseService.findAllByBalanceUpdatedAndDateBefore(false, LocalDate.now()));
        updateIncomes(incomeService.findAllByBalanceUpdatedAndDateBefore(false, LocalDate.now()));
    }

    public void updateIncomes(List<Income> incomes) {
        for (Income income : incomes) {
            User user = userService.findUserByEmail(income.getUser().getEmail());
            incomeService.updateBalanceForIncome(user, income);
        }
    }

    public void updateExpenses(List<Expense> expenses) {
        for (Expense expense : expenses) {
            User user = userService.findUserByEmail(expense.getUser().getEmail());
            expenseService.updateBalanceForExpense(user, expense);
        }
    }
}
