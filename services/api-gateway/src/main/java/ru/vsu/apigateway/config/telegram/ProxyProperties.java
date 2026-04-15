package ru.vsu.apigateway.config.telegram;

import java.net.Proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "telegram.proxy")
public class ProxyProperties {
    private boolean enabled;
    private Proxy.Type type;
    private String host;
    private int port;
    private String username;
    private String password;
}
