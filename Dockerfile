FROM ncp-vsaidt-registry.ncr.gov-ntruss.com/openjdk:17.0.1-jdk-slim AS builder

COPY . /tmp
WORKDIR /tmp

RUN sed -i 's/\r$//' ./gradlew

RUN chmod +x ./gradlew
RUN ./gradlew clean
RUN ./gradlew bootjar

FROM ncp-vsaidt-registry.ncr.gov-ntruss.com/openjdk:17.0.1-jdk-slim
COPY --from=builder /tmp/build/libs/api-1.0.0-SNAPSHOT.jar ./
COPY --from=builder /tmp/opentelemetry-javaagent.jar ./
RUN mkdir -p assets
COPY --from=builder /tmp/assets ./assets
# CSAP 조치
#RUN useradd -m -s /bin/bash visang
#USER visang

CMD ["java", "-jar", "api-1.0.0-SNAPSHOT.jar"]