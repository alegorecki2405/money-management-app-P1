package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.IncomeDto;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.IncomeType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.IncomeRepository;
import aleksander.gorecki.moneymanagementapp.repository.IncomeTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final AuthenticationFacade authenticationFacade;
    private final IncomeTypeRepository incomeTypeRepository;
    private final UserService userService;

    @Override
    public List<IncomeDto> findAllByUser(User user) {
        List<Income> incomes = incomeRepository.findAllByUser(user);
        return mapToIncomeDtoList(incomes);
    }

    @Override
    public List<IncomeDto> findAllFutureIncomesByUser(User user) {
        return filterIncomesByDate(findAllByUser(user), LocalDateTime.now(), true);
    }

    @Override
    public List<IncomeDto> findAllPreviousIncomesByUser(User user) {
        return filterIncomesByDate(findAllByUser(user), LocalDateTime.now(), false);
    }

    @Override
    public List<Income> findAllByBalanceUpdatedAndDateBefore(boolean balanceUpdated, LocalDateTime date) {
        return incomeRepository.findAllByBalanceUpdatedAndDateBefore(false, LocalDateTime.now());
    }

    @Override
    @Transactional
    public Income create(User user, IncomeDto incomeDto) {
        LocalDateTime date = incomeDto.getDate() != null ? incomeDto.getDate() : LocalDateTime.now();
        String type = getIncomeType(user, incomeDto.getType());
        Income income = new Income(
                incomeDto.getId(),
                incomeDto.getName(),
                incomeDto.getAmount(),
                type,
                date,
                user,
                false
        );
        return updateBalanceForIncome(user, income);
    }

    @Override
    public Income updateBalanceForIncome(User user, Income income) {
        if (income.getDate().isBefore(LocalDateTime.now())) {
            userService.updateUsersBalance(user, income.getAmount());
            income.setBalanceUpdated(true);
        }
        return incomeRepository.save(income);
    }

    private String getIncomeType(User user, String typeName) {
        IncomeType incomeType = incomeTypeRepository.findByNameAndUser(typeName, user);
        if (incomeType == null) {
            incomeType = createIncomeType(user, typeName);
        }
        return incomeType.getName();
    }

    private IncomeType createIncomeType(User user, String typeName) {
        IncomeType incomeType = new IncomeType();
        incomeType.setName(typeName.toUpperCase());
        incomeType.setUser(user);
        return incomeTypeRepository.save(incomeType);
    }

    @Override
    public void saveOrUpdate(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public Income findById(Long incomeId) {
        return incomeRepository.findById(incomeId).orElse(null);
    }

    @Override
    public List<IncomeType> findAllTypesByUser(User user) {
        return incomeTypeRepository.findAllByUser(user);
    }

    @Override
    public Model createModelForIncomesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, LocalDateTime startDate, LocalDateTime endDate, String timePeriod) {
        model.addAttribute("futureIncomes", filterIncomes(findAllFutureIncomesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("previousIncomes", filterIncomes(findAllPreviousIncomesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
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
        model.addAttribute("incomeTypes", findAllTypesByUser(user));
        return model;
    }

    private List<IncomeDto> filterIncomes(List<IncomeDto> incomeDtos, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, LocalDateTime startDate, LocalDateTime endDate, String timePeriod) {
        return incomeDtos.stream()
                .filter(incomeDto -> isTypeMatch(incomeDto, typeFilter))
                .filter(incomeDto -> isAmountInRange(incomeDto, maxAmount, minAmount))
                .filter(incomeDto -> {
                    try {
                        return isDateInRange(incomeDto, startDate, endDate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(incomeDto -> {
                    try {
                        return isFromTimePeriod(incomeDto, timePeriod);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean isFromTimePeriod(IncomeDto incomeDto, String timePeriod) throws ParseException {
        LocalDateTime now = LocalDateTime.now();
        if (timePeriod != null && !timePeriod.isEmpty()) {
            switch (timePeriod) {
                case "lastYear":
                    return isDateInRange(incomeDto, LocalDateTime.now().minusYears(1), now);
                case "lastMonth":
                    return isDateInRange(incomeDto, LocalDateTime.now().minusMonths(1), now);
                case "lastWeek":
                    return isDateInRange(incomeDto, LocalDateTime.now().minusDays(7), now);
                case "nextWeek":
                    return isDateInRange(incomeDto, LocalDateTime.now().plusDays(7), now);
                case "nextMonth":
                    return isDateInRange(incomeDto, LocalDateTime.now().plusMonths(1), now);
                case "nextYear":
                    return isDateInRange(incomeDto, LocalDateTime.now().plusYears(7), now);
            }
        }
        return true;
    }

    private boolean isTypeMatch(IncomeDto incomeDto, String typeFilter) {
        return typeFilter == null || typeFilter.isEmpty() || incomeDto.getType().equals(typeFilter);
    }

    private boolean isAmountInRange(IncomeDto incomeDto, BigDecimal maxAmount, BigDecimal minAmount) {
        return (maxAmount == null || incomeDto.getAmount().compareTo(maxAmount) <= 0) &&
                (minAmount == null || incomeDto.getAmount().compareTo(minAmount) >= 0);
    }

    private boolean isDateInRange(IncomeDto incomeDto, LocalDateTime startDate, LocalDateTime endDate) throws ParseException {
        LocalDateTime expenseDate = incomeDto.getDate();
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

    private List<IncomeDto> mapToIncomeDtoList(List<Income> incomes) {
        return incomes.stream()
                .map(this::mapToIncomeDto)
                .collect(Collectors.toList());
    }

    private List<IncomeDto> filterIncomesByDate(List<IncomeDto> incomes, LocalDateTime compareDate, boolean future) {
        return incomes.stream()
                .filter(income -> future ? income.getDate().isAfter(compareDate) : income.getDate().isBefore(compareDate))
                .collect(Collectors.toList());
    }

    private IncomeDto mapToIncomeDto(Income income) {
        IncomeDto incomeDto = new IncomeDto();
        incomeDto.setId(income.getId());
        incomeDto.setName(income.getName());
        incomeDto.setAmount(income.getAmount());
        incomeDto.setDate(income.getDate());
        incomeDto.setType(income.getType());
        return incomeDto;
    }
}
