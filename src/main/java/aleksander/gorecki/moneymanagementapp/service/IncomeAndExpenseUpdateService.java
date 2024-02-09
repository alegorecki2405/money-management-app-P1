package aleksander.gorecki.moneymanagementapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IncomeAndExpenseUpdateService {
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void doSomething() {
        //TODO: check if there are any expenses/incomes with balance updated on false and past date
    }
}
