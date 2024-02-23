package aleksander.gorecki.moneymanagementapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Income> incomes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ExpenseType> expenseTypes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<IncomeType> incomeTypes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<BalanceHistory> balanceHistory = new ArrayList<>();

    public void updateBalance(BigDecimal amount, LocalDate date, String name) {
        // Update the balance field
        this.balance = this.balance.add(amount);

        // Create a new BalanceHistory entry
        BalanceHistory balanceChange = new BalanceHistory();
        balanceChange.setUser(this);
        balanceChange.setDateTime(date);
        balanceChange.setBalanceChange(amount);
        balanceChange.setName(name);

//        if (this.balanceHistory == null) {
//            this.balanceHistory = new ArrayList<>();
//        }

        this.balanceHistory.add(balanceChange);
    }
}
