## Dataflow
```shell
java -jar spring-cloud-dataflow-server-2.11.5.jar \
    --spring.datasource.url=jdbc:postgresql://localhost:5432/postgres \
    --spring.datasource.username=postgres \
    --spring.datasource.password=postgres \
    --spring.datasource.driver-class-name=org.postgresql.Driver
```

- 기본 Port: 9393
- 대시보드 UI: http://localhost:9393/dashboard

## Skipper
```shell
java -jar spring-cloud-skipper-server-2.11.5.jar \
--spring.datasource.url=jdbc:postgresql://localhost:5432/postgres \
--spring.datasource.username=postgres \
--spring.datasource.password=postgres \
--spring.datasource.driver-class-name=org.postgresql.Driver \
--spring.datasource.hikari.maximum-pool-size=20
```

- 기본 Port: 7577

## Shell
```shell
java -jar spring-cloud-dataflow-shell-2.11.5.jar
```

## 실행 로그 WARN 분석

> 2025-01-18 06:46:16.864  WARN 18686 --- [           main] ubernetesProfileEnvironmentPostProcessor : Not running inside kubernetes. Skipping 'kubernetes' profile activation.

kubermates 구동 여부를 확인하는 로그로 보인다. 로컬 환경으로 실행했기에 무시하고 넘어간다.

> 2025-01-18 06:46:17.251  WARN 18686 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Could not locate PropertySource: I/O error on GET request for "http://localhost:8888/application/local": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)

기본적으로 서버 구동시 `localhost:8888`서버로 접근을 시도하려고 한다. 로깅 클래스 위치를 확인해보니 `Spring Cloud Config Server`와 연동되는 것으로 보인다.

