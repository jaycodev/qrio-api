package com.qrio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Qrio API",
        version = "1.0.0",
        description = "Backend API supporting Qrio's restaurant management panel, enabling efficient administrative workflows.",
        contact = @Contact(
            name = "Qrio Support",
            email = "qrio.restaurant@gmail.com"
        ),
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
    )
)
public class QrioApplication {
    public static void main(String[] args) {
        SpringApplication.run(QrioApplication.class, args);
    }
}
