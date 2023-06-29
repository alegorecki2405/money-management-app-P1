package aleksander.gorecki.moneymanagementapp.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private BigDecimal amount;
    @NotEmpty
    private String type;
}
