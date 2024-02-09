package aleksander.gorecki.moneymanagementapp.repository;

import aleksander.gorecki.moneymanagementapp.entity.IncomeType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeTypeRepository extends JpaRepository<IncomeType, Long> {

    List<IncomeType> findAllByUser(User user);

    IncomeType findByName(String name);
}
