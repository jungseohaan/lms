package com.visang.aidt.lms.api.configuration;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class NatsConfig {

    @Value(value = "${spring.nats.server}")
    private String natsSever;

    private Connection natsConnection;

    @PostConstruct
    public void init() {
        Options options = new Options.Builder()
                .server(natsSever)
                .connectionListener(new ConnectionListener() {
                    @Override
                    public void connectionEvent(Connection conn, Events type) {
                        log.info("Status change {}", type);
                    }
                })
                .pingInterval(Duration.ofSeconds(30))
                .reconnectWait(Duration.ofSeconds(60))
                .maxReconnects(10)
                .build();
        try {
            natsConnection = Nats.connect(options);
        } catch (IOException | InterruptedException e) {
            log.error("Unable to connect to NATS: {}", e.getMessage());
        }
    }

    public Connection natsConnection() {
        if (natsConnection == null) {
            log.error("NATS connection is not established.");
            throw new IllegalStateException("NATS connection not established.");
        }
        return natsConnection;
    }
}
