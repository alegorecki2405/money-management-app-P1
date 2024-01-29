package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuthenticationFacade authenticationFacade;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, AuthenticationFacade authenticationFacade) {
        this.expenseRepository = expenseRepository;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public List<ExpenseDto> findAllByUser(User user) {
        List<Expense> expenses = expenseRepository.findAllByUser(user);
        return mapToExpenseDtoList(expenses);
    }

    @Override
    public List<ExpenseDto> findAllFutureExpensesByUser(User user) {
        return filterExpensesByDate(findAllByUser(user), new Date(), true);
    }

    @Override
    public List<ExpenseDto> findAllPreviousExpensesByUser(User user) {
        return filterExpensesByDate(findAllByUser(user), new Date(), false);
    }

    @Override
    @Transactional
    public Expense create(User user, ExpenseDto expenseDto) {
        Date date = expenseDto.getDate() != null ? expenseDto.getDate() : new Date();
        Expense expense = new Expense(
                expenseDto.getId(),
                expenseDto.getName(),
                expenseDto.getAmount(),
                expenseDto.getType().name(),
                date,
                user
        );
        return expenseRepository.save(expense);
    }

    @Override
    public void saveOrUpdate(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public Expense findById(Long expenseId) {
        return expenseRepository.findById(expenseId).orElse(null);
    }

    @Override
    public Model createModelForExpensesTemplate(Model model, User user, String typeFilter, Double maxAmount, Double minAmount, String startDate, String endDate, String timePeriod) {
        if (typeFilter != null && !typeFilter.isEmpty()) {
            model.addAttribute("futureExpenses", findAllFutureExpensesByUser(user));
            model.addAttribute("previousExpenses", findAllPreviousExpensesByUser(user));
        } else {
            model.addAttribute("futureExpenses", Collections.EMPTY_LIST);
            model.addAttribute("previousExpenses", Collections.EMPTY_LIST);
        }

        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        return model;
    }

    private List<ExpenseDto> mapToExpenseDtoList(List<Expense> expenses) {
        return expenses.stream()
                .map(this::mapToExpenseDto)
                .collect(Collectors.toList());
    }

    private List<ExpenseDto> filterExpensesByDate(List<ExpenseDto> expenses, Date compareDate, boolean future) {
        return expenses.stream()
                .filter(expense -> future ? expense.getDate().after(compareDate) : expense.getDate().before(compareDate))
                .collect(Collectors.toList());
    }

    private ExpenseDto mapToExpenseDto(Expense expense) {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(expense.getId());
        expenseDto.setName(expense.getName());
        expenseDto.setAmount(expense.getAmount());
        expenseDto.setDate(expense.getDate());
        expenseDto.setType(ExpenseType.valueOf(expense.getType()));
        return expenseDto;
    }
}

