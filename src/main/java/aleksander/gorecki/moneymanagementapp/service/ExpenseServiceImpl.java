package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService{

    private ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public List<ExpenseDto> findAllByUser(User user) {
        List<Expense> expenses = expenseRepository.findAllByUser(user);
        return expenses.stream()
                .map(this::mapToExpenseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> findAllFutureExpensesByUser(User user) {
        return findAllByUser(user)
                .stream()
                .filter(expenseDto -> expenseDto.getDate().after(new Date()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> findAllPreviousExpensesByUser(User user) {
        return findAllByUser(user)
                .stream()
                .filter(expenseDto -> expenseDto.getDate().before(new Date()))
                .collect(Collectors.toList());
    }

    @Override
    public Expense create(User user, ExpenseDto expenseDto) {
        Date date = new Date();
        if(expenseDto.getDate() != null) {
            date = expenseDto.getDate();
        }
        Expense expense = new Expense(expenseDto.getId(),
                expenseDto.getName(),
                expenseDto.getAmount(),
                expenseDto.getType().name(),
                date,
                user);
        return expenseRepository.save(expense);
    }

    private ExpenseDto mapToExpenseDto(Expense expense){
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(expense.getId());
        expenseDto.setName(expense.getName());
        expenseDto.setAmount(expense.getAmount());
        expenseDto.setDate(expense.getDate());
        expenseDto.setType(ExpenseType.valueOf(expense.getType()));
        return expenseDto;
    }
}
