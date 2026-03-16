# 백엔드개발 Coding Test

## Overview

- 이 프로젝트는 Product 와 Order 가 있는 간단한 Spring Boot 응용 프로그램입니다. 
- 과제는 기능 구현, 리팩토링, 코드 리뷰로 구성되어 있으며 가능한 한 많이 완료해 주세요.

## 과제

코드베이스에는 수정 및 리뷰해야 하는 `TODO`가 있습니다. 각 TODO는 코드의 주석으로 표시되어 있습니다.

### TODO List

1. `ProductService#findProductsByCategory`에서 카테고리별 제품 조회 메소드 구현
2. `OrderController`에 주문 생성 API 구현
3. `OrderService#placeOrder`에 주문 생성 로직 구현 --> 테스트 클래스의 `@InjectMock`에 대해 알지 못했던 것, JPA 연관관계 데이터를 저장해야 하는 것을 까먹었던 것
4. 리팩토링: `OrderService#checkoutOrder`에 몰린 도메인 로직을 도메인 객체로 이동
5. 코드 리뷰: `OrderService#bulkShipOrdersParent`의 구현코드 리뷰
6. 리팩토링(가격/기준정보): `ProductService#applyBulkPriceChange` 개선
7. 최적화: `PermissionChecker#hasPermission` 개선

---

## 풀이 및 이슈 기타 등

> 3. `OrderService#placeOrder`에 주문 생성 로직 구현에서 `OrderItemRepository`가 NPE로 인해 테스트 실패했던 이슈

Test 클래스 내 선언돼 있는 `OrderService`가 `@InjectMocks`어노테이션을 달고 선언돼 있었음.

```java
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;
```

여기서 `@InjectMocks` 은 테스트하려는 진짜 클래스 객체를 생성한다. `@Mock`이 붙은 필드들을 모아서 이 객체의 생성자나 필드에 주입하는 방식이다.

`OrderItemRepository`을 Mock으로 지정하지 않았기 때문에 NPE가 발생했던 이슈였다. `OrderItemRepository` `@Mock` 어노테이션을 달아서 NPE는 해결됐다.

> 3. `OrderService#placeOrder` 테스트에서 값이 제대로 들어가지 않았던 이유

`Order`와 `OrderItem` 클래스는 일대다 관계를 가지고 있는데, 각자의 객체를 생성해서 저장은 다 해놨지만 연관관계 데이터를 저장하지 않아서, `0(데이터가 저장되지 않음)`값으로 나왔었다. 연관관계 데이터(`Order.items`)를 저장하고나니 테스트를 통과했다.

> 6. 리팩토링(가격/기준정보): `ProductService#applyBulkPriceChange` 개선

double 사용, 루프 내 개별 조회 및 저장, 하드코딩된 세금/반올림 규칙

1. double 사용 - `BigDecimal`이라는 클래스에 대해 알아봐야함
  - `Number` 클래스와 다르게, 계산 연산을 하기 위해서는 메서드들을 활용해서 처리해야함.
2. 루프 내 개별 조회 및 저장 - 루프마다 save 대신 saveAll로 처리해야함
  - List 컬렉션(findAllById)를 통해 향상된 for문으로 넣어서 `Product` 객체 내 계산 처리
3. 하드코딩된 세금/반올림 규칙 - 지역/카테고리에 맞게 변경(카테고리 별 분기 기능 별도의 클래스 생성 필요해 보임)
  - `Product` 객체 내 계산 처리와 카테고리에 맞게 switch문을 활용한 부가세 처리(그런데 이 부분에서는 위처럼 카테고리가 많아질 수록 switch 쓰기엔 버거워보임. 클래스가 더 좋을지도?)
