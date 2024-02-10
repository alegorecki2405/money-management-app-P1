package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.IncomeDto;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.IncomeType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.IncomeRepository;
import aleksander.gorecki.moneymanagementapp.repository.IncomeTypeRepository;
import aleksander.gorecki.moneymanagementapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final AuthenticationFacade authenticationFacade;
    private final IncomeTypeRepository incomeTypeRepository;
    private final UserRepository userRepository;

    @Override
    public List<IncomeDto> findAllByUser(User user) {
        List<Income> incomes = incomeRepository.findAllByUser(user);
        return mapToIncomeDtoList(incomes);
    }

    @Override
    public List<IncomeDto> findAllFutureIncomesByUser(User user) {
        return filterIncomesByDate(findAllByUser(user), new Date(), true);
    }

    @Override
    public List<IncomeDto> findAllPreviousIncomesByUser(User user) {
        return filterIncomesByDate(findAllByUser(user), new Date(), false);
    }

    @Override
    @Transactional
    public Income create(User user, IncomeDto incomeDto) {
        Date date = incomeDto.getDate() != null ? incomeDto.getDate() : new Date();
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
        return incomeRepository.save(income);
    }

    private String getIncomeType(User user, String typeName) {
        IncomeType incomeType = incomeTypeRepository.findByName(typeName);
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
    public Model createModelForIncomesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod) {
        model.addAttribute("futureIncomes", filterIncomes(findAllFutureIncomesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("previousIncomes", filterIncomes(findAllPreviousIncomesByUser(user), typeFilter, maxAmount, minAmount, startDate, endDate, timePeriod));
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("timePeriod", timePeriod);
        model.addAttribute("incomeTypes", findAllTypesByUser(user));
        return model;
    }

    private List<IncomeDto> filterIncomes(List<IncomeDto> incomeDtos, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod) {
        return incomeDtos.stream()
                .filter(incomeDto -> isTypeMatch(incomeDto, typeFilter))
                .filter(incomeDto -> isAmountInRange(incomeDto, maxAmount, minAmount))
                .collect(Collectors.toList());
    }

    private boolean isTypeMatch(IncomeDto incomeDto, String typeFilter) {
        return typeFilter == null || typeFilter.isEmpty() || incomeDto.getType().equals(typeFilter);
    }

    private boolean isAmountInRange(IncomeDto incomeDto, BigDecimal maxAmount, BigDecimal minAmount) {
        return (maxAmount == null || incomeDto.getAmount().compareTo(maxAmount) <= 0) &&
                (minAmount == null || incomeDto.getAmount().compareTo(minAmount) >= 0);
    }

    private List<IncomeDto> mapToIncomeDtoList(List<Income> incomes) {
        return incomes.stream()
                .map(this::mapToIncomeDto)
                .collect(Collectors.toList());
    }

    private List<IncomeDto> filterIncomesByDate(List<IncomeDto> incomes, Date compareDate, boolean future) {
        return incomes.stream()
                .filter(income -> future ? income.getDate().after(compareDate) : income.getDate().before(compareDate))
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
