package aleksander.gorecki.moneymanagementapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyManagementAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyManagementAppApplication.class, args);
    }

}
