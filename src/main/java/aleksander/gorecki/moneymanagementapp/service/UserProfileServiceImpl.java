package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.entity.BalanceHistory;
import aleksander.gorecki.moneymanagementapp.entity.User;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
            model.addAttribute("firstDiagram", lastYearByMonthLostGain(user));
        }
    }

    public HashMap<String, BigDecimal> lastYearByMonthLostGain(User user) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        LocalDate d = LocalDate.now();
        HashMap<String, BigDecimal> balancesPerMonth = new LinkedHashMap<>();
        for (int i = 12; i >= 0; i--) {
            LocalDate firstDayOfEachMonth = d.minusMonths(i).withDayOfMonth(1);
            LocalDate firstDayOfNextMonth = d.minusMonths(i - 1).withDayOfMonth(1);
            String key = firstDayOfEachMonth.getMonth().name().concat("-").concat(String.valueOf(firstDayOfEachMonth.getYear()));
            BigDecimal monthValue = balanceHistories
                    .stream()
                    .filter(balanceHistory -> isDateInRangeForBalanceHistory(balanceHistory, firstDayOfEachMonth, firstDayOfNextMonth))
                    .map(BalanceHistory::getBalanceChange)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            balancesPerMonth.put(key, monthValue);
        }
        return balancesPerMonth;
    }

    private boolean isDateInRangeForBalanceHistory(BalanceHistory balanceHistory, LocalDate startDate, LocalDate endDate) {
        LocalDate ldt = balanceHistory.getDateTime();
        return (ldt.isEqual(startDate) || ldt.isAfter(startDate)) && (ldt.isEqual(endDate) || ldt.isBefore(endDate));
    }
}
