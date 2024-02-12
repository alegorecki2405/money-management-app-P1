package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseRepository;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final UserService userService;

    @Override
    public List<ExpenseDto> findAllByUser(User user) {
        List<Expense> expenses = expenseRepository.findAllByUser(user);
        return mapToExpenseDtoList(expenses);
    }

    @Override
    public List<ExpenseDto> findAllFutureExpensesByUser(User user) {
        return filterExpensesByDate(findAllByUser(user), LocalDate.now(), true);
    }

    @Override
    public List<ExpenseDto> findAllPreviousExpensesByUser(User user) {
        return filterExpensesByDate(findAllByUser(user), LocalDate.now(), false);
    }

    @Override
    public List<Expense> findAllByBalanceUpdatedAndDateBefore(boolean balanceUpdated, LocalDate date) {
        return expenseRepository.findAllByBalanceUpdatedAndDateBefore(false, LocalDate.now());
    }

    @Override
    @Transactional
    public Expense create(User user, ExpenseDto expenseDto) {
        LocalDate date = expenseDto.getDate() != null ? expenseDto.getDate() : LocalDate.now();
        String type = getExpenseType(user, expenseDto.getType());
        Expense expense = new Expense(
                expenseDto.getId(),
                expenseDto.getName(),
                expenseDto.getAmount(),
                type,
                date,
                user,
                false
        );
        return updateBalanceForExpense(user, expense);
    }

    @Override
    public Expense updateBalanceForExpense(User user, Expense expense) {
        if (expense.getDate().isBefore(LocalDate.now())) {
            userService.updateUsersBalance(user, expense.getAmount().negate());
            expense.setBalanceUpdated(true);
        }
        return expenseRepository.save(expense);
    }


    private String getExpenseType(User user, String typeName) {
        ExpenseType expenseType = expenseTypeRepository.findByNameAndUser(typeName, user);
        if (expenseType == null) {
            expenseType = createExpenseType(user, typeName);
        }
        return expenseType.getName();
    }

    private ExpenseType createExpenseType(User user, String typeName) {
        ExpenseType expenseType = new ExpenseType();
        expenseType.setName(typeName.toUpperCase());
        expenseType.setUser(user);
        return expenseTypeRepository.save(expenseType);
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
    public List<ExpenseType> findAllTypesByUser(User user) {
        return expenseTypeRepository.findAllByUser(user);
    }

    @Override
    public Model createModelForExpensesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, LocalDate startDate, LocalDate endDate, String timePeriod) {
        model.addAttribute("futureExpenses", filterExpenses(findAllFutureExpensesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("previousExpenses", filterExpenses(findAllPreviousExpensesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("minAmount", minAmount);
        if (startDate != null) {
            model.addAttribute("startDate", startDate);
        }
        if (endDate != null) {
            model.addAttribute("endDate", endDate);
        }
        model.addAttribute("timePeriod", timePeriod);
        model.addAttribute("expenseTypes", findAllTypesByUser(user));
        return model;
    }

    private List<ExpenseDto> filterExpenses(List<ExpenseDto> expenseDtos, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, LocalDate startDate, LocalDate endDate, String timePeriod) {
        return expenseDtos.stream()
                .filter(expenseDto -> isTypeMatch(expenseDto, typeFilter))
                .filter(expenseDto -> isAmountInRange(expenseDto, maxAmount, minAmount))
                .filter(expenseDto -> {
                    try {
                        return isDateInRange(expenseDto, startDate, endDate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(expenseDto -> {
                    try {
                        return isFromTimePeriod(expenseDto, timePeriod);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean isFromTimePeriod(ExpenseDto expenseDto, String timePeriod) throws ParseException {
        LocalDate now = LocalDate.now();
        if (timePeriod != null && !timePeriod.isEmpty()) {
            switch (timePeriod) {
                case "lastYear":
                    return isDateInRange(expenseDto, LocalDate.now().minusYears(1), now);
                case "lastMonth":
                    return isDateInRange(expenseDto, LocalDate.now().minusMonths(1), now);
                case "lastWeek":
                    return isDateInRange(expenseDto, LocalDate.now().minusDays(7), now);
                case "nextWeek":
                    return isDateInRange(expenseDto, LocalDate.now().plusDays(7), now);
                case "nextMonth":
                    return isDateInRange(expenseDto, LocalDate.now().plusMonths(1), now);
                case "nextYear":
                    return isDateInRange(expenseDto, LocalDate.now().plusYears(7), now);
            }
        }
        return true;
    }

    private boolean isTypeMatch(ExpenseDto expenseDto, String typeFilter) {
        return typeFilter == null || typeFilter.isEmpty() || expenseDto.getType().equals(typeFilter);
    }

    private boolean isAmountInRange(ExpenseDto expenseDto, BigDecimal maxAmount, BigDecimal minAmount) {
        return (maxAmount == null || expenseDto.getAmount().compareTo(maxAmount) <= 0) &&
                (minAmount == null || expenseDto.getAmount().compareTo(minAmount) >= 0);
    }

    private boolean isDateInRange(ExpenseDto expenseDto, LocalDate startDate, LocalDate endDate) throws ParseException {
        LocalDate expenseDate = expenseDto.getDate();
        if (startDate != null && endDate != null) {
            return (expenseDate.isAfter(startDate) || expenseDate.isEqual(startDate)) && (expenseDate.isBefore(endDate) || expenseDate.isEqual(endDate));
        }
        if (startDate != null) {
            return expenseDate.isAfter(startDate) || expenseDate.isEqual(startDate);
        }
        if (endDate != null) {
            return expenseDate.isBefore(endDate) || expenseDate.isEqual(endDate);
        }
        return true;
    }

    private List<ExpenseDto> mapToExpenseDtoList(List<Expense> expenses) {
        return expenses.stream()
                .map(this::mapToExpenseDto)
                .collect(Collectors.toList());
    }

    private List<ExpenseDto> filterExpensesByDate(List<ExpenseDto> expenses, LocalDate compareDate, boolean future) {
        return expenses.stream()
                .filter(expense -> future ? expense.getDate().isAfter(compareDate) : expense.getDate().isBefore(compareDate) || expense.getDate().isEqual(compareDate))
                .collect(Collectors.toList());
    }

    private ExpenseDto mapToExpenseDto(Expense expense) {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(expense.getId());
        expenseDto.setName(expense.getName());
        expenseDto.setAmount(expense.getAmount());
        expenseDto.setDate(expense.getDate());
        expenseDto.setType(expense.getType());
        return expenseDto;
    }
}

