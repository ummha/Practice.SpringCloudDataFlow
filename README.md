# Spring Cloud Dataflow

- 공식 문서: https://dataflow.spring.io/docs
- 공식 문서: https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/

## 1. 설치

- 로컬 서버 다운로드

1. Spring cloud dataflow 서버
    ```shell
    wget https://repo.maven.apache.org/maven2/org/springframework/cloud/spring-cloud-dataflow-server/2.11.5/spring-cloud-dataflow-server-2.11.5.jar
    ```

2. Spring cloud dataflow shell
    ```shell
    wget https://repo.maven.apache.org/maven2/org/springframework/cloud/spring-cloud-dataflow-shell/2.11.5/spring-cloud-dataflow-shell-2.11.5.jar
    ```

3. Spring cloud skipper 서버
    ```shell
    wget https://repo.maven.apache.org/maven2/org/springframework/cloud/spring-cloud-skipper-server/2.11.5/spring-cloud-skipper-server-2.11.5.jar
   ```

## 2. Spring cloud Skipper

Spring Cloud Data Flow와 함께 사용되는 배포 관리 도구로, 애플리케이션 배포의 관리 및 업그레이드를 간편하게 수행할 수 있도록 도와주며, Skipper는 Spring Cloud Data Flow에서 스트림 및 작업(Tasks)을 구성하고 실행하는데 있어 버전 관리, 롤백, 및 점진적 업그레이드와 같은 복잡한 배포 시나리오를 지원하는 역할을 한다.

### 2-1. 주요 역할 및 기능 버전 관리:

> Skipper는 애플리케이션의 각 배포에 대해 버전을 관리하며 새로운 버전의 애플리케이션을 배포하거나, 문제가 발생했을 때 이전 버전으로 롤백을 할 수 있다.

#### 점진적 업그레이드:

> Skipper는 애플리케이션의 새로운 버전을 배포할 때, 한 번에 전체 시스템을 업그레이드하는 대신, 부분적으로 점진적으로 업그레이드할 수 있도록 지원한다.
>
> 예를 들어, 스트림의 일부 인스턴스만 먼저 업그레이드한 후, 문제가 없으면 나머지 인스턴스도 업그레이드하는 방식으로 안정성을 높일 수 있다.

#### 롤백 기능:

> 새로운 버전 배포 후 문제가 발생할 경우, 이전 안정된 버전으로 빠르게 롤백할 수 있다. Skipper는 이 과정을 단순화하여 다운타임을 최소화.

#### 상태 관리:

> Skipper는 각 배포의 상태를 관리한다. 예를 들어, 현재 배포된 애플리케이션의 상태가 정상인지, 배포 과정에서 실패한 것은 없는지 등을 추적 가능.

#### 추상화된 배포 환경:

> Skipper는 Kubernetes, Cloud Foundry 등 다양한 환경에서 애플리케이션을 배포할 수 있도록 추상화된 인터페이스를 제공. 이를 통해 다양한 클라우드 및 온프레미스 환경에서 일관된 방식으로 애플리케이션 배포가 가능.

## 3. Spring Cloud Dataflow Server

데이터 스트림 처리 및 배치 작업을 관리하고 오케스트레이션하는 플랫폼. 이 서버는 Spring Cloud Data Flow 생태계의 핵심 컴포넌트로서, 데이터 파이프라인을 설계, 배포, 모니터링 및 관리.

### 3.1 주요 기능

#### 데이터 스트림 처리:

> Spring Cloud Data Flow Server는 스트림을 구성하고 실행. 스트림은 데이터가 소스에서 시작해 프로세서로 전달되고, 마지막으로 싱크로 전송되는 데이터 파이프라인을 나타냄.
>
> 예를 들어, Kafka에서 데이터를 읽어와 이를 변환하고, 다시 데이터베이스에 저장하는 스트림을 구성할 수 있다.

#### 배치 작업 관리:

> 배치 작업은 일괄 처리가 필요한 데이터 작업을 정의.
>
> Spring Cloud Data Flow Server는 Spring Batch와 통합되어, 대량의 데이터를 일괄 처리하는 작업을 정의하고, 스케줄링하며, 관리할 수 있다.

#### 구성 및 배포:

> Spring Cloud Data Flow Server는 데이터 스트림과 배치 작업을 손쉽게 구성하고 배포할 수 있는 UI와 REST API를 제공.
>
> 이를 통해 다양한 애플리케이션을 간편하게 연결하고, Kubernetes, Cloud Foundry, 또는 로컬 환경에 배포 가능.

#### 모니터링 및 관리:

> 서버는 실행 중인 스트림과 배치 작업의 상태를 모니터링하고, 로그를 분석하며, 메트릭스를 수집하여 실시간으로 관리할 수 있는 기능을 제공.
>
> 이를 통해 애플리케이션의 성능과 건강 상태 추적 가능.

#### 확장 가능성:

> Spring Cloud Data Flow는 확장 가능한 아키텍처를 제공하여, 클라우드 환경에서도 대규모 데이터 처리 작업을 효율적으로 수행할 수 있으며 필요에 따라 노드를 추가하거나, 배포된 애플리케이션을 확장하여 더 많은 데이터를 처리할 수 있다.

### 3.2 구성 요소

#### Spring Cloud Stream:

> 데이터 스트림을 구성하고, 메시지 기반의 통신을 관리하는 라이브러리.
>
> Kafka, RabbitMQ와 같은 메시징 시스템을 사용하여 스트림 내의 데이터를 전달.

### Spring Batch:

> 대규모 데이터 처리를 위해 배치 작업을 정의하고 실행할 수 있는 프레임워크.
>
> 데이터 변환, 로드, 저장 작업을 자동화하고, 이를 Spring Cloud Data Flow와 연동하여 관리.

### Skipper:

> 애플리케이션 배포의 버전 관리, 점진적 업그레이드, 롤백 등을 수행하는 도구로, Spring Cloud Data Flow Server와 통합 사용.

## 실행

## Skipper & Dataflow

```shell
java -jar spring-cloud-dataflow-server-2.11.5.jar \
--spring.datasource.url=`DB 주소`\
--spring.datasource.username=`DB 계정 ID`\
--spring.datasource.password=`DB 계정 비밀번호`\
--spring.datasource.driver-class-name=`DB Driver Class`\
--spring.flyway.enabled=false # DDL 자동 생성 기능 방지
```

Dataflow 서버와 Skipper 서버가 같은 Host 에서 실행되지 않으면 Dataflow 실행시 Skipper 서버 주소를 설정한다.
```shell
java -jar spring-cloud-dataflow-server-2.11.5.jar \
--spring.cloud.skipper.client.serverUri=https://192.51.100.1:7577/api
```
