//package ru.vsu.tgbot.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.Credentials;
//import okhttp3.OkHttpClient;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
//import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
//import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
//import org.telegram.telegrambots.meta.generics.TelegramClient;
//
//import java.net.Authenticator;
//import java.net.InetSocketAddress;
//import java.net.PasswordAuthentication;
//import java.net.Proxy;
//
//@Configuration
//public class ProxyConfiguration {
//    @Bean(value = "okClient")
//    public OkHttpClient okClientHttp(
//            @Value("${telegram.proxy.hostname}") String hostname,
//            @Value("${telegram.proxy.port}") int port,
//            @Value("${username}") String username,
//            @Value("${password}") String password
//    ) {
//        return new TelegramOkHttpClientFactory.HttpProxyOkHttpClientCreator(
//                () -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port)),
//                () -> ((route, response) -> {
//                    String credential = Credentials.basic(username, password);
//                    return response
//                            .request()
//                            .newBuilder()
//                            .header("Proxy-Authorization", credential)
//                            .build();
//                })
//        ).get();
//    }
//
//    public OkHttpClient okClientSocks(
//            @Value("${hostname}") String hostname,
//            @Value("${port}") int port,
//            @Value("${username}") String username,
//            @Value("${password}") String password
//    ) {
//        Authenticator.setDefault(new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                if (getRequestingHost().equalsIgnoreCase(hostname)) {
//                    if (port == getRequestingPort()) {
//                        return new PasswordAuthentication(username, password.toCharArray());
//                    }
//                }
//                return null;
//            }
//        });
//
//        return new TelegramOkHttpClientFactory.SocksProxyOkHttpClientCreator(
//                () -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostname, port))
//        ).get();
//    }
//
//    @Bean
//    public TelegramClient telegramClient(
//            @Qualifier("okClient") OkHttpClient okClient,
//            @Value("${botToken}") String botToken
//    ) {
//        return new OkHttpTelegramClient(okClient, botToken);
//    }
//
//    @Bean(value = "telegramBotsApplication")
//    public TelegramBotsLongPollingApplication telegramBotsApplication(
//            @Qualifier("okClient") OkHttpClient okClient
//    ) {
//        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okClient);
//    }
//}
