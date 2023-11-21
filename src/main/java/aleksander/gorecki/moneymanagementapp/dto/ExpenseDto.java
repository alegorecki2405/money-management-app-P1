package aleksander.gorecki.moneymanagementapp.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
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
    @Min(0)
    private BigDecimal amount;
    @NotNull(message = "Type should not be null")
    @Enumerated(EnumType.STRING)
    private ExpenseType type;
}
