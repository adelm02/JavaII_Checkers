package cz.vsb.checkers;

import £org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cz.vsb.checkers", "lab"})
public class CheckersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckersApiApplication.class, args);
    }
}
