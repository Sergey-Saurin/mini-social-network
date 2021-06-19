package network;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@TestConfiguration
@ComponentScan("skillbox.javapro11.service")
public class ServiceTestConfiguration {
}
