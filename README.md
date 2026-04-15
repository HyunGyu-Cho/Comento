## 코멘토 5주 인턴 과제

### 1주차 - Spring 개발환경 구축

- Spring 및 MyBatis 개발환경 구축
- SpringBoot와 비교하여 편의성 차이 학습

### 2주차 - API 인터페이스 가이드 문서 작성

- 공공 데이터 API 문서를 참고하여 SW활용률 통계 API 가이드 작성
- 요청 파라미터 설계 및 응답 데이터 포맷 정의
- 상세 내용: [API_GUIDE.docs](API_GUIDE.docs)

### 3~5주차 - API 구현

- 2주차에 작성한 API 명세서를 기반으로 실제 통계 API 구축
- Spring Boot + MyBatis (MariaDB)
- 상세 내용: [comentoStatistic/README.md](comentoStatistic/README.md)

### 학습 정리

REST API / HTTP 통신

#### HTTP 통신

- Client가 Server에 Request를 보내고 Server가 Response를 반환하는 구조
- **Request**: 요청라인 + 헤더(Host, Authorization, Content-Type) + 바디(JSON)
- **Response**: 상태줄 + 응답헤더 + 응답바디

#### HTTP 메서드


| 메서드    | 용도        |
| ------ | --------- |
| GET    | 데이터 조회    |
| POST   | 데이터 생성    |
| PUT    | 데이터 전체 수정 |
| PATCH  | 데이터 일부 수정 |
| DELETE | 데이터 삭제    |


#### 상태 코드


| 코드                        | 의미       |
| ------------------------- | -------- |
| 200 OK                    | 성공       |
| 201 Created               | 생성 성공    |
| 400 Bad Request           | 요청 형식 오류 |
| 401 Unauthorized          | 인증 필요    |
| 403 Forbidden             | 권한 없음    |
| 404 Not Found             | 리소스 없음   |
| 500 Internal Server Error | 서버 오류    |


#### 무상태성과 인증

- HTTP는 각 요청을 독립적으로 처리 (상태 저장 X)
- 인증 방식: 쿠키 / 세션 / JWT 토큰

#### 브라우저 요청-응답 흐름

URL 입력 → DNS 조회 → TCP 연결 → HTTP 요청 전송 → 서버 처리 → 응답 반환 → 브라우저 렌더링

