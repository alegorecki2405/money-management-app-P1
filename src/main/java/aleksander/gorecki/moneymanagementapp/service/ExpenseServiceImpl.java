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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ExpenseTypeRepository expenseTypeRepository;

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
        return expenseRepository.save(expense);
    }

    private String getExpenseType(User user, String typeName) {
        ExpenseType expenseType = expenseTypeRepository.findByName(typeName);
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
    public Model createModelForExpensesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod) {
        model.addAttribute("futureExpenses", filterExpenses(findAllFutureExpensesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("previousExpenses", filterExpenses(findAllPreviousExpensesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("minAmount", minAmount);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            model.addAttribute("startDate", dateFormat.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("endDate", dateFormat.format(endDate));
        }
        model.addAttribute("timePeriod", timePeriod);
        model.addAttribute("expenseTypes", findAllTypesByUser(user));
        return model;
    }

    private List<ExpenseDto> filterExpenses(List<ExpenseDto> expenseDtos, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod) {
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
        Calendar cal = Calendar.getInstance();
        if (timePeriod != null && !timePeriod.isEmpty()) {
            switch (timePeriod) {
                case "lastYear":
                    cal.add(Calendar.YEAR, -1);
                    return isDateInRange(expenseDto, cal.getTime(), new Date());
                case "lastMonth":
                    cal.add(Calendar.MONTH, -1);
                    return isDateInRange(expenseDto, cal.getTime(), new Date());
                case "lastWeek":
                    cal.add(Calendar.DAY_OF_WEEK, -7);
                    return isDateInRange(expenseDto, cal.getTime(), new Date());
                case "nextWeek":
                    cal.add(Calendar.DAY_OF_WEEK, 7);
                    return isDateInRange(expenseDto, new Date(), cal.getTime());
                case "nextMonth":
                    cal.add(Calendar.MONTH, 1);
                    return isDateInRange(expenseDto, new Date(), cal.getTime());
                case "nextYear":
                    cal.add(Calendar.YEAR, 1);
                    return isDateInRange(expenseDto, new Date(), cal.getTime());
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

    private boolean isDateInRange(ExpenseDto expenseDto, Date startDate, Date endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expenseDate = sdf.parse(sdf.format(expenseDto.getDate()));
        if (startDate != null && endDate != null) {
            Date start = sdf.parse(sdf.format(startDate));
            Date end = sdf.parse(sdf.format(endDate));
            return (expenseDate.after(start) || expenseDate.equals(start)) && (expenseDate.before(end) || expenseDate.equals(end));
        }
        if (startDate != null) {
            Date start = sdf.parse(sdf.format(startDate));
            return expenseDate.after(start) || expenseDate.equals(start);
        }
        if (endDate != null) {
            Date end = sdf.parse(sdf.format(endDate));
            return expenseDate.before(end) || expenseDate.equals(end);
        }
        return true;
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
        expenseDto.setType(expense.getType());
        return expenseDto;
    }
}

