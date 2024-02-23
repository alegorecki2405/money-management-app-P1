package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.BalanceHistoryDto;
import aleksander.gorecki.moneymanagementapp.entity.BalanceHistory;
import aleksander.gorecki.moneymanagementapp.entity.User;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @Override
    public void createModelForUserProfile(Model model) {
        model.addAttribute("userRole", authenticationFacade.getHighestRole());
        User user = userService.findUserByEmail(authenticationFacade.getAuth().getName());
        if (user != null) {
            HashMap<String, BigDecimal> balancesPerMonth = lastYearByMonthLostGain(user);
            model.addAttribute("firstDiagram", balancesPerMonth);
            BigDecimal balance = user.getBalance();
            model.addAttribute("currentBalance", balance.toString());
            model.addAttribute("percentageChange", getPercentageDifference(balance, getChangeInThisMonth(user)).stripTrailingZeros());
            getAmountsAddedAndSubtractedPerPeriod(model, user);
            getLastOperations(user, model);
        }
    }

    public HashMap<String, BigDecimal> lastYearByMonthLostGain(User user) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        LocalDate d = LocalDate.now();
        HashMap<String, BigDecimal> balancesPerMonth = new LinkedHashMap<>();
        for (int i = 12; i >= 0; i--) {
            YearMonth month = YearMonth.from(d.minusMonths(i));
            LocalDate firstDayOfMonth = month.atDay(1);
            LocalDate lastDayOfMonth = month.atEndOfMonth();
            String key = firstDayOfMonth.getMonth().name().concat("-").concat(String.valueOf(firstDayOfMonth.getYear()));
            balancesPerMonth.put(key, getValueFromPeriod(balanceHistories, firstDayOfMonth, lastDayOfMonth));
        }
        return balancesPerMonth;
    }

    private BigDecimal getValueFromPeriod(List<BalanceHistory> balanceHistories, LocalDate start, LocalDate end) {
        return balanceHistories
                .stream()
                .filter(balanceHistory -> isDateInRangeForBalanceHistory(balanceHistory, start, end))
                .map(BalanceHistory::getBalanceChange)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isDateInRangeForBalanceHistory(BalanceHistory balanceHistory, LocalDate startDate, LocalDate endDate) {
        LocalDate ldt = balanceHistory.getDateTime();
        return (ldt.isEqual(startDate) || ldt.isAfter(startDate)) && (ldt.isEqual(endDate) || ldt.isBefore(endDate));
    }

    private BigDecimal getChangeInThisMonth(User user) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        YearMonth month = YearMonth.now();
        return getValueFromPeriod(balanceHistories, month.atDay(1), month.atEndOfMonth());
    }

    private BigDecimal getPercentageDifference(BigDecimal current, BigDecimal change) {
        BigDecimal previous = current.subtract(change);
        return current.subtract(previous).divide(previous, 4, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));
    }

    private void getAmountsAddedAndSubtractedPerPeriod(Model model, User user) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        List<BalanceHistory> expenses = balanceHistories.stream().filter(balanceHistory -> balanceHistory.getBalanceChange().compareTo(BigDecimal.ZERO) < 0).toList();
        List<BalanceHistory> incomes = balanceHistories.stream().filter(balanceHistory -> balanceHistory.getBalanceChange().compareTo(BigDecimal.ZERO) > 0).toList();

        model.addAttribute("incomeLastWeek", getValueFromPeriod(incomes, LocalDate.now().minusDays(7), LocalDate.now()));
        model.addAttribute("incomeLastMonth", getValueFromPeriod(incomes, LocalDate.now().minusMonths(1), LocalDate.now()));
        model.addAttribute("incomeLastYear", getValueFromPeriod(incomes, LocalDate.now().minusYears(1), LocalDate.now()));
        model.addAttribute("expenseLastWeek", getValueFromPeriod(expenses, LocalDate.now().minusDays(7), LocalDate.now()));
        model.addAttribute("expenseLastMonth", getValueFromPeriod(expenses, LocalDate.now().minusMonths(1), LocalDate.now()));
        model.addAttribute("expenseLastYear", getValueFromPeriod(expenses, LocalDate.now().minusYears(1), LocalDate.now()));
    }

    private void getLastOperations(User user, Model model) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        if (balanceHistories.size() - 8 >= 0) {
            balanceHistories = balanceHistories.subList(balanceHistories.size() - 8, balanceHistories.size());
        }
        List<BalanceHistoryDto> reversed = new ArrayList<>(balanceHistories.stream().map(BalanceHistory::mapToDto).toList());
        Collections.reverse(reversed);
        model.addAttribute("lastOperations", reversed);
    }
}
