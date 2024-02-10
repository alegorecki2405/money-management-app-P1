package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.IncomeDto;
import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.IncomeType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IncomeService {
    List<IncomeDto> findAllByUser(User user);

    List<IncomeDto> findAllFutureIncomesByUser(User user);

    List<IncomeDto> findAllPreviousIncomesByUser(User user);

    Income create(User user, IncomeDto incomeDto);

    void saveOrUpdate(Income income);

    Income findById(Long incomeId);

    List<IncomeType> findAllTypesByUser(User user);

    Model createModelForIncomesTemplate(Model model, User user, String typeFilter, BigDecimal maxAmount, BigDecimal minAmount, Date startDate, Date endDate, String timePeriod);
}