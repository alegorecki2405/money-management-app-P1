package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService{

    private ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseDto> findAllByUser(User user) {
        List<Expense> expenses = expenseRepository.findAllByUser(user);
        return expenses.stream()
                .map(this::mapToExpenseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Expense create(User user, ExpenseDto expenseDto) {
        Expense expense = new Expense(expenseDto.getId(),
                expenseDto.getName(),
                expenseDto.getAmount(),
                expenseDto.getType().name(),
                user);
        return expenseRepository.save(expense);
    }

    private ExpenseDto mapToExpenseDto(Expense expense){
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(expense.getId());
        expenseDto.setName(expense.getName());
        expenseDto.setAmount(expense.getAmount());
        expenseDto.setType(ExpenseType.valueOf(expense.getType()));
        return expenseDto;
    }
}
