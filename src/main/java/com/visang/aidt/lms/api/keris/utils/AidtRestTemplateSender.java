package com.visang.aidt.lms.api.keris.utils;

import com.visang.aidt.lms.api.keris.utils.response.AbstractResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;

@Component
@Slf4j
public class AidtRestTemplateSender {

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(3000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(10)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        factory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(factory);
    }

    public <T extends AbstractResponse> ResponseEntity<T> send(ParamOption option, ParameterizedTypeReference<T> typeReference) throws Exception {
        URI uri = new URI(option.getUrl());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(option.getMediaType()));
        headers.setContentType(MediaType.parseMediaType(option.getContentType()));
        if (StringUtils.isNotEmpty(option.getPartnerId())) {
            headers.set("Partner-ID", option.getPartnerId());
        }
        HttpEntity<String> request = new HttpEntity<>(option.getRequest().toString(), headers);
        ResponseEntity<T> response = restTemplate.exchange(
                uri,
                option.getMethod(),
                request,
                typeReference
        );
        return response;
    }

}
