package Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RestController
    public static class HelloController {

        @GetMapping("/api/getbooking")
        public String getBooking() {return "Hello World!";}
        @GetMapping("/api/hello")
        public String sayHello() {
            return "Hello from Spring Boot!";
        }
    }
}
