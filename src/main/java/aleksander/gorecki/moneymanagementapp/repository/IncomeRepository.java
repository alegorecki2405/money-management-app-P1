package aleksander.gorecki.moneymanagementapp.repository;

import aleksander.gorecki.moneymanagementapp.entity.Income;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByUser(User user);

    List<Income> findAllByBalanceUpdatedAndDateBefore(boolean balanceUpdated, LocalDate date);
}
