# 비상교육 AIDT LMS API 프로젝트


***

## 시작하기

이 프로젝트는 GitLab을 사용하여 소스 관리를 하고 있습니다. GitLab 사용에 대한 기본적인 [가이드](https://about.gitlab.com/learn/)는 인터넷을 참고 부탁 드립니다.


## 프로젝트명
비상교육 AIDT LMS API 프로젝트

## 프로젝트 설명
이 프로젝트는 비상교육에서 진행하는 AI 교과서 프로젝트입니다. 교육부 주관 프로젝트 검인정 교과서 업체들에게 AIDT 라는 이름으로 전자 교과서를 만드는 프로젝트입니다. 이 프로젝트에 (주)코드비플랫이 맡은 업무는 백엔드 개발입니다. RESTFul API를 효율성있게 만드는 업무를 맡게 되었습니다.

이를 위해 Spring Data JPA와 Spring Data REST를 활용하여 간단한 형태의 API를 만들고, 복잡한 API는 QueryDSL을 활용하여 개발을 진행하고자 합니다.

[Spring Data REST](https://spring.io/projects/spring-data-rest)에 대한 소개는 링크를 참고 부탁 드립니다.

## 개발 환경 소개
- Build
    1. JDK 17
    2. Gradle 7.2
- Development Framework
    1. Spring Boot 2.7.17
    2. Spring Cloud 2021.0.8
    3. Spring Data JPA
    4. Spring Data Rest
    5. QueryDSL
    6. Lombok
    7. Log4j2
    8. Swagger(Spring Fox)
    9. Spring Hateoas
    10. Spring Security
    11. JUnit 5 & Karate & Microcks
- Tools
    1. GitLab
    2. Docker
    3. Jenkins
    4. Mariadb 11.1.2
    5. IntelliJ IDEA 2021.1.2 (Ultimate Edition)
    6. Postman
    7. Keycloak
- 이 외 필요할 경우, 기타 도구를 설치할 수 있습니다.

## 프로젝트 Clone 하기

- [코드비플랫 GitLab 접속하기](http://codebplat.co.kr:1980/)
- 발급받은 계정으로 로그인을 하면 AIDT 프로젝트가 보입니다.
- git clone http://codebplat.co.kr:1980/vs-aidt/visang-aidt-lms-api.git 명령어로 프로젝트를 내려 받아 주세요.
- main 브랜치를 먼저 받은 후에, develop 브랜치로 checkout 하세요.

```
git clone http://codebplat.co.kr:1980/vs-aidt/visang-aidt-lms-api.git
git checkout develop
```

## 기술지원 문의
GitLab 서버는 (주)코드비플랫이 관리하고 있으며, 기술지원 및 문의는 김효석이사(manager@codebplat.co.kr)에게 주세요.

