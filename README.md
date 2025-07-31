# spring-gift-order

## STEP2 코드 리팩토링

## STEP3 - 배포하기

- [ ] **CORS 설정:** 다른 출처(Origin)의 클라이언트가 API를 호출할 수 있도록 `WebConfig`에 CORS 전역 설정을 추가한다.
- [ ] **배포 스크립트 작성:** 애플리케이션 빌드, 기존 프로세스 종료, 새로운 버전 실행을 자동화하는 `deploy.sh` 셸 스크립트를 작성한다.
- [ ] **(선택) CORS 인수 테스트 작성:** `@SpringBootTest`와 `MockMvc`를 사용하여 CORS 설정이 올바르게 동작하는지 검증하는 인수 테스트를 작성한다.
