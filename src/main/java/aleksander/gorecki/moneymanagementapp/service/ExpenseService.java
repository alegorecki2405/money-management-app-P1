package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;

import java.util.List;

public interface ExpenseService {
    List<ExpenseDto> findAllByUser(User user);

    Expense create(User user, ExpenseDto expenseDto);
}
