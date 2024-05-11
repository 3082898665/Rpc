package tq.cn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tq.cn.annotation.EnableProviderRpc;

@SpringBootApplication
@EnableProviderRpc
public class Provider1Application {
    public static void main(String[] args) {
        SpringApplication.run(Provider1Application.class, args);
    }
}
