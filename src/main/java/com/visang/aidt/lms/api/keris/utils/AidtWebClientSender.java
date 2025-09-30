package com.visang.aidt.lms.api.keris.utils;

import com.visang.aidt.lms.api.keris.utils.response.AbstractResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class AidtWebClientSender {

    private WebClient client;

    @PostConstruct
    public void init() throws Exception {
        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(20)
                .maxIdleTime(Duration.ofSeconds(58))
                .maxLifeTime(Duration.ofSeconds(58))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .pendingAcquireMaxCount(-1)
                .evictInBackground(Duration.ofSeconds(30))
                .lifo()
                .metrics(true)
                .build();

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        client = WebClient.builder().clientConnector(
                new ReactorClientHttpConnector(
                        HttpClient.create(provider)
                                .secure(t -> t.sslContext(sslContext))
                                .tcpConfiguration(
                                        client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000) //miliseconds
                                                .doOnConnected(
                                                        conn -> conn.addHandlerLast(new ReadTimeoutHandler(30))  //sec
                                                                .addHandlerLast(new WriteTimeoutHandler(60)) //sec
                                                )
                                ).responseTimeout(Duration.ofSeconds(60))
                )
        ).build();
    }

    public <T extends AbstractResponse> ResponseEntity<T> sendWithBlock(ParamOption option, ParameterizedTypeReference<T> typeReference) {
        return this.send(option, typeReference).block();
    }

    public <T extends AbstractResponse> List<ResponseEntity<T>> fetchSendWithBlock(List<ParamOption> option, ParameterizedTypeReference<T> typeReference) {
        return this.fetchSend(option, typeReference).collectList().block();
    }

    public <T extends AbstractResponse> Flux<ResponseEntity<T>> fetchSend(List<ParamOption> options, ParameterizedTypeReference<T> typeReference) {
        return Flux.fromIterable(options)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(option ->
                        send(option, typeReference)
                )
                .ordered((u1, u2) -> 1-2);
    }

    public <T extends AbstractResponse> Mono<ResponseEntity<T>> send(ParamOption option, ParameterizedTypeReference<T> typeReference) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(option.getUrl());
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return client.mutate().uriBuilderFactory(factory).build().method(option.getMethod())
                .uri(option.getUrl())
                .headers(httpHeaders -> {
                    httpHeaders.set("Content-Type", option.getContentType());
                    if (StringUtils.isNotEmpty(option.getPartnerId())) {
                        httpHeaders.set("Partner-ID", option.getPartnerId());
                    }
                    if (StringUtils.isNotEmpty(option.getApiVersion())) {
                        httpHeaders.set("API-version", option.getApiVersion());
                    }
                })
                .accept(option.getMediaType())
                .bodyValue(option.getRequest().toString())
                .retrieve()
                .toEntity(typeReference)
                .flatMap(clientResponse  -> {
                    clientResponse.getBody().setData(option.getData());
                    return Mono.just(clientResponse);
                })
                .onErrorResume(error -> Mono.just(handleError(error, option, typeReference)));
    }

    public <T extends AbstractResponse> ResponseEntity<T> handleError(Throwable error, ParamOption option, ParameterizedTypeReference<T> typeReference) {
        // null 체크 로직 추가
        if (error == null) {
            throw new IllegalArgumentException("error는 null일 수 없습니다.");
        }

        if (typeReference == null) {
            throw new IllegalArgumentException("typeReference는 null일 수 없습니다.");
        }

        ResponseEntity<T> response = null;
        try {
            Class<T> paramCls = (Class<T>) AidtUtils.getClass(typeReference.getType());

            // paramCls null 체크 추가
            if (paramCls == null) {
                throw new IllegalStateException("클래스 타입을 가져올 수 없습니다.");
            }

            if (error instanceof WebClientResponseException) {
                //http 에러
                WebClientResponseException err = (WebClientResponseException) error;
                response = new ResponseEntity<>(paramCls.newInstance(), err.getStatusCode());
            } else {
                //기타에러
                response = new ResponseEntity<>(paramCls.newInstance(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("err: {}", e);
            // 예외 발생 시 기본 응답 생성
            try {
                Class<T> paramCls = (Class<T>) AidtUtils.getClass(typeReference.getType());
                if (paramCls != null) {
                    response = new ResponseEntity<>(paramCls.newInstance(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception ex) {
                log.error("기본 응답 생성 실패: {}", ex);
            }
        }

        // response null 체크 로직 추가
        if (response == null) {
            throw new IllegalStateException("Response를 생성할 수 없습니다.");
        }

        if (response.getBody() == null) {
            throw new IllegalStateException("Response Body가 null입니다.");
        }

        response.getBody().setData(option != null ? option.getData() : null);
        response.getBody().setThrowable(error);
        return response;
    }

}
