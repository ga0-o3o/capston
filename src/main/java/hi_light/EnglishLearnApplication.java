package hi_light;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan(basePackages = {"hi_light.bean", "hi_light"})
public class EnglishLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglishLearnApplication.class, args);
    }
}
