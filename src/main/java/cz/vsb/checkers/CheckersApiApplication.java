package cz.vsb.checkers;

import lab.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"cz.vsb.checkers", "lab"})
@EntityScan(basePackages = {"lab", "cz.vsb.checkers"})
@EnableJpaRepositories(basePackages = {"cz.vsb.checkers.api.repository"})
public class CheckersApiApplication {

    public static void main(String[] args) {
        Main.main(args);
    }
}