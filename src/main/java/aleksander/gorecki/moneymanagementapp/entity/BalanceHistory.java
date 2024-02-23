package aleksander.gorecki.moneymanagementapp.entity;

import aleksander.gorecki.moneymanagementapp.dto.BalanceHistoryDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDate dateTime;

    private BigDecimal balanceChange;

    private String name;

    public BalanceHistoryDto mapToDto() {
        BalanceHistoryDto balanceHistoryDto = new BalanceHistoryDto();
        balanceHistoryDto.setName(this.name);
        balanceHistoryDto.setDateTime(this.dateTime);
        balanceHistoryDto.setBalanceChange(this.balanceChange);
        return balanceHistoryDto;
    }
}
