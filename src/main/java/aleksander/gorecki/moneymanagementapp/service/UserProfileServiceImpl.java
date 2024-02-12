package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.config.AuthenticationFacade;
import aleksander.gorecki.moneymanagementapp.dto.ExpenseDto;
import aleksander.gorecki.moneymanagementapp.dto.IncomeDto;
import aleksander.gorecki.moneymanagementapp.entity.BalanceHistory;
import aleksander.gorecki.moneymanagementapp.entity.User;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
//            lastYearByMonthLostGain(user);
            lastYearByMonthLostGainButBetterXD(user);
        }
    }


    public void lastYearByMonthLostGain(User user) {
        //wyciagniecie wszystkich przeszlych wydatkow
        List<ExpenseDto> expenses = expenseService.findAllPreviousExpensesByUser(user);
        List<IncomeDto> incomes = incomeService.findAllPreviousIncomesByUser(user);

        //wydatki i dochody per miesiac
        LocalDate d = LocalDate.now();
        HashMap<String, List<ExpenseDto>> expensesPerMonth = new HashMap<>();
        HashMap<String, List<IncomeDto>> incomesPerMonth = new HashMap<>();
        for (int i = 12; i >= 0; i--) {
//            LocalDate firstDayOfEachMonth = d.minusMonths(i).withDayOfMonth(1).withHour(0);
//            LocalDate firstDayOfNextMonth = d.minusMonths(i - 1).withDayOfMonth(1).withHour(0);
//            expensesPerMonth.put(d.getMonth().name(),
//                    expenses.stream().filter(expenseDto ->
//                                    isDateInRangeForLocalDT(expenseDto, firstDayOfEachMonth, firstDayOfNextMonth))
//                            .collect(Collectors.toList()));
//
//            incomesPerMonth.put(d.getMonth().name(),
//                    incomes.stream().filter(incomeDto ->
//                                    isDateInRangeForLocalDT(incomeDto, firstDayOfEachMonth, firstDayOfNextMonth))
//                            .collect(Collectors.toList()));
        }
    }

    public void lastYearByMonthLostGainButBetterXD(User user) {
        List<BalanceHistory> balanceHistories = user.getBalanceHistory();
        LocalDate d = LocalDate.now();
        HashMap<String, List<BalanceHistory>> balanceHistoryPerMonth = new HashMap<>();
        for (int i = 12; i >= 0; i--) {
            LocalDate firstDayOfEachMonth = d.minusMonths(i).withDayOfMonth(1);
            LocalDate firstDayOfNextMonth = d.minusMonths(i - 1).withDayOfMonth(1);
            balanceHistoryPerMonth.put(d.getMonth().name(),
                    balanceHistories.stream().filter(balanceHistory ->
                                    isDateInRangeForBalanceHistory(balanceHistory, firstDayOfEachMonth, firstDayOfNextMonth))
                            .collect(Collectors.toList())
            );
        }
        System.out.println(balanceHistoryPerMonth);
    }

    private boolean isDateInRangeForBalanceHistory(BalanceHistory balanceHistory, LocalDate startDate, LocalDate endDate) {
        LocalDate ldt = balanceHistory.getDateTime();
        return (ldt.isEqual(startDate) || ldt.isAfter(startDate)) && (ldt.isEqual(endDate) || ldt.isBefore(endDate));
    }

//    private boolean isDateInRangeForLocalDT(FinanceInterface element, LocalDate startDate, LocalDate endDate) {
//        LocalDate start = Date.from(startDate.toInstant(ZoneId.systemDefault().getRules().getOffset(startDate)));
//        LocalDate end = Date.from(endDate.toInstant(ZoneId.systemDefault().getRules().getOffset(endDate)));
//        LocalDate expenseDate = element.getDate();
//        return (expenseDate.after(start) || expenseDate.equals(start)) && (expenseDate.before(end) || expenseDate.equals(end));
//    }
}
