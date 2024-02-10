package aleksander.gorecki.moneymanagementapp.repository;

import aleksander.gorecki.moneymanagementapp.entity.Expense;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUser(User user);

    List<Expense> findAllByBalanceUpdatedAndDateBefore(boolean balanceUpdated, Date date);
}
