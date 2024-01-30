package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ExpenseService {
    List<ExpenseDto> findAllByUser(User user);

    List<ExpenseDto> findAllFutureExpensesByUser(User user);

    List<ExpenseDto> findAllPreviousExpensesByUser(User user);

    Expense create(User user, ExpenseDto expenseDto);

    void saveOrUpdate(Expense expense);

    Expense findById(Long expenseId);

    Model createModelForExpensesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod);
}

