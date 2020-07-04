package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author luoliudan
 */
@SpringBootApplication(scanBasePackages = "com.ml")
@EnableSwagger2
public class KubernetesClientServer {
    public static void main(String[] args) {
        SpringApplication.run(KubernetesClientServer.class, args);
    }
}
