package com.dot.Xpay.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "XPay Transaction API",
                version = "1.0",
                description = "APIs for transaction processing, reporting, and commissions",
                contact = @Contact(
                        name = "Efosa Blossom",
                        email = "efosablossom001@gmail.com"
                )
        )
)
public class OpenApiConfig {
}
