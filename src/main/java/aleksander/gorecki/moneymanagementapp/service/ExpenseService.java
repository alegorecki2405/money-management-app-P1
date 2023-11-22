package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;

import java.util.List;

public interface ExpenseService {
    List<ExpenseDto> findAllByUser(User user);

    List<ExpenseDto> findAllFutureExpensesByUser(User user);

    List<ExpenseDto> findAllPreviousExpensesByUser(User user);

    Expense create(User user, ExpenseDto expenseDto);
}
