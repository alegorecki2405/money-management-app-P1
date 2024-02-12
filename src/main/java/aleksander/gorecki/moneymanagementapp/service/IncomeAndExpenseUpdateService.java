package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.User;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Data
public class IncomeAndExpenseUpdateService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void doSomething() {
        updateExpenses(expenseService.findAllByBalanceUpdatedAndDateBefore(false, LocalDateTime.now()));
        updateIncomes(incomeService.findAllByBalanceUpdatedAndDateBefore(false, LocalDateTime.now()));
    }

    public void updateIncomes(List<Income> incomes) {
        for (Income income : incomes) {
            User user = income.getUser();
            incomeService.updateBalanceForIncome(user, income);
        }
    }

    public void updateExpenses(List<Expense> expenses) {
        for (Expense expense : expenses) {
            User user = expense.getUser();
            expenseService.updateBalanceForExpense(user, expense);
        }
    }
}
