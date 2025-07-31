# spring-gift-order

## STEP1 코드 리팩토링

## STEP2 - 주문하기

- [ ] **주문 도메인 구현:** `Order` 엔티티를 JPA 엔티티로 생성하고, 관련 DTO, Repository, Service를 구현한다.
- [ ] **카카오 메시지 클라이언트 구현:** 카카오 메시지 API와 통신을 전담하는 `KakaoMessageClient`를 구현한다.
- [ ] **주문 서비스 로직 구현:**
    - [ ] 주문 시 상품 옵션의 재고를 차감한다.
    - [ ] 주문한 상품이 위시 리스트에 있으면 삭제한다.
    - [ ] 주문 내역을 생성하고 저장한다.
    - [ ] `KakaoMessageClient`를 호출하여 주문 내역을 카카오톡으로 전송한다.
- [ ] **주문 API 구현:** `POST /api/orders` 엔드포인트를 구현한다.