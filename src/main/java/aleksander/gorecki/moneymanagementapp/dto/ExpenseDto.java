package aleksander.gorecki.moneymanagementapp.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpenseDto implements FinanceInterface {
    private Long id;
    @NotEmpty(message = "Name should not be empty")
    private String name;
    @NotNull(message = "Amount cannot be null")
    @Min(0)
    private BigDecimal amount;
    @NotEmpty(message = "Type should not be empty")
    private String type;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
