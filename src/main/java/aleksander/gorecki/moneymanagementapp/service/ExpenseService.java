package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseService {
    List<ExpenseDto> findAllByUser(User user);

    List<ExpenseDto> findAllFutureExpensesByUser(User user);

    List<ExpenseDto> findAllPreviousExpensesByUser(User user);

    List<Expense> findAllByBalanceUpdatedAndDateBefore(boolean balanceUpdated, LocalDateTime date);

    Expense create(User user, ExpenseDto expenseDto);

    Expense updateBalanceForExpense(User user, Expense expense);

    void saveOrUpdate(Expense expense);

    Expense findById(Long expenseId);

    List<ExpenseType> findAllTypesByUser(User user);

    Model createModelForExpensesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, LocalDateTime startDate, LocalDateTime endDate, String timePeriod);
}

