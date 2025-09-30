package com.visang.aidt.lms.api.configuration;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile({"!local"})
public class JasyptConfig {

    @Value("${key.namespace}")
    private String namespace;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() throws URISyntaxException {

        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";
        config.setAlgorithm(algorithm);
        config.setPassword(getKey());
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(1);
        config.setProvider(new BouncyCastleProvider());
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);

        return encryptor;
    }

    private String getKey() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String encryptKey = restTemplate.getForObject(new URI("http://visang-aidt-key-server."+namespace+".svc.cluster.local/vlmsapi"), String.class);
        return StringUtils.trim(encryptKey);
    }

}
