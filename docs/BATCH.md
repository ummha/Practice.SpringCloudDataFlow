# Spring Batch 5

## Schemas

- batch_job_execution
- batch_job_execution_context
- batch_job_execution_params
- batch_job_instance
- batch_step_execution
- batch_step_execution_context

## Sequences

- batch_step_execution_seq
- batch_job_execution_seq
- batch_job_seq

## ItemProcessor

1. BeanValidatingItemProcessor
   - 설명: Java Bean Validation (JSR-303) 기반으로 데이터를 검증하는 ItemProcessor입니다. 입력 데이터를 검증하고, 유효하지 않으면 예외를 던집니다.
   - 사용 시점:
   - 데이터가 특정 제약 조건(예: @NotNull, @Size 등)을 만족하는지 확인해야 할 때.
   - 입력 데이터가 유효성 검사를 통과하지 못하면 처리에서 제외하고 싶을 때.

2. ClassifierCompositeItemProcessor
   - 설명: Classifier를 사용하여 여러 ItemProcessor 중 하나를 선택해서 실행합니다. Classifier는 입력 데이터에 따라 어떤 프로세서를 사용할지 결정합니다.
   - 사용 시점:
   - 데이터의 조건에 따라 서로 다른 ItemProcessor를 실행해야 할 때.
   - 예: 데이터의 타입이나 속성에 따라 다른 변환 로직을 적용해야 할 때.

3. CompositeItemProcessor
   - 설명: 여러 개의 ItemProcessor를 체인처럼 연결하여 순차적으로 실행합니다. 한 ItemProcessor의 출력이 다음 ItemProcessor의 입력으로 전달됩니다.
   - 사용 시점:
   - 여러 단계로 데이터를 변환하거나 처리해야 할 때.
   - 예: 데이터 정규화 → 검증 → 변환의 순차적 처리.

4. FunctionItemProcessor
   - 설명: 자바의 Function<T, R> 인터페이스를 활용하여 데이터를 처리하는 ItemProcessor입니다. 간단한 람다 표현식을 사용할 수 있습니다.
   - 사용 시점:
   - 단순한 데이터 변환이 필요한 경우.
   - 예: 특정 필드 값을 대문자로 변환하거나, 값을 매핑하는 간단한 작업.

5. ItemProcessorAdapter
   - 설명: 기존의 메서드 호출을 래핑하여 ItemProcessor로 사용할 수 있게 합니다. 일반적인 Java 객체의 메서드를 호출할 수 있도록 어댑터 역할을 합니다.
   - 사용 시점:
   - 기존 서비스나 유틸리티 메서드를 호출하여 데이터를 처리하려는 경우.
   - 예: 외부 API 호출이나 데이터 가공 로직이 이미 구현된 경우.

6. PassThroughItemProcessor
   - 설명: 입력 데이터를 그대로 출력으로 전달합니다. 변환이나 처리 로직이 필요 없을 때 사용됩니다.
   - 사용 시점:
   - 데이터를 변환하지 않고 그대로 전달해야 할 때.
   - 주로 테스트 용도 또는 프로세싱을 비활성화해야 할 때.

7. PredicateFilteringItemProcessor
   - 설명: 주어진 Predicate 조건에 따라 데이터를 필터링합니다. 조건을 만족하지 않는 데이터는 무시됩니다.
   - 사용 시점:
   - 특정 조건을 만족하는 데이터만 다음 단계로 전달하고 싶을 때.
   - 예: null 값 또는 특정 속성을 가진 데이터를 제거하고 싶을 때.

8. ScriptItemProcessor
   - 설명: 스크립트를 실행하여 데이터를 처리합니다. Groovy, JavaScript 등 여러 스크립트 언어를 지원합니다.
   - 사용 시점:
   - 데이터 변환 로직이 동적으로 변경되거나, 스크립트 기반으로 처리해야 할 때.
   - 예: 사용자 정의 스크립트를 실행하여 데이터를 변환.

9. ValidatingItemProcessor
   - 설명: Validator 인터페이스를 사용하여 데이터를 검증합니다. 유효하지 않은 경우 ValidationException을 던집니다.
   - 사용 시점:
   - Bean Validation과는 별도로, 커스텀 검증 로직이 필요한 경우.
-   예: 복잡한 도메인 검증 로직을 구현해야 할 때.

| 구현체                              | 주요 기능             | 사용 예시                          |
|----------------------------------|-------------------|--------------------------------|
| BeanValidatingItemProcessor      | JSR-303 기반 데이터 검증 | 엔티티 검증 (예: @NotNull, @Size 등)  |
| ClassifierCompositeItemProcessor | 조건에 따라 프로세서를 선택   | 타입에 따라 다른 변환 로직 실행             |
| CompositeItemProcessor           | 여러 프로세서를 순차적으로 실행 | 데이터 변환 → 검증 → 포맷팅              |
| FunctionItemProcessor            | 람다로 간단한 데이터 처리    | 필드 값 변환, 값 매핑                  |
| ItemProcessorAdapter             | 기존 메서드를 래핑하여 호출   | 외부 API 호출, 기존 서비스 로직 사용        |
| PassThroughItemProcessor         | 입력값을 그대로 출력       | 처리 없이 데이터 전달                   |
| PredicateFilteringItemProcessor  | 조건에 따른 데이터 필터링    | null 값 제거, 특정 조건을 만족하는 데이터 필터링 |
| ScriptItemProcessor              | 스크립트를 이용한 데이터 처리  | 동적 로직 실행                       |
| ValidatingItemProcessor          | 커스텀 검증 로직 구현      | 도메인 규칙 검증                      |