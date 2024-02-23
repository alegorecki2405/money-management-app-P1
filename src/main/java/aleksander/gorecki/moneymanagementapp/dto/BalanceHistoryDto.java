package aleksander.gorecki.moneymanagementapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BalanceHistoryDto {

    private LocalDate dateTime;

    private BigDecimal balanceChange;

    private String name;
}
