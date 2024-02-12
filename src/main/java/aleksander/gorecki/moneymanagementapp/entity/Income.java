package aleksander.gorecki.moneymanagementapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "incomes")
public class Income extends FinanceElement {
    @Builder
    public Income(Long id, String name, BigDecimal amount, String type, Date date, User user, boolean balanceUpdated) {
        super(id, name, amount, type, date, user, balanceUpdated);
    }
}
