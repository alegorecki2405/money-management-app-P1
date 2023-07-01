package aleksander.gorecki.moneymanagementapp.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty(message = "Name should not be empty")
    private String name;
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
    @NotEmpty(message = "Type should not be empty")
    private String type;
}
