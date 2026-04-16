## S/W 통계 API 구축

### 구조
- client (외부 API 호출)
    - `HolidayClient.java` — 공공 공휴일 API 호출

- config (설정)
    - `RestClientConfig.java` — RestClient Bean 설정

- controller (API 엔드포인트)
    - `StatisticController.java`

- dao (Service와 MyBatis를 연결하는 중개자)
    - `StatisticMapper.java`

- dto (데이터 전달 객체)
    - `DailyLoginDto.java`
    - `MonthlyLoginDto.java`
    - `AverageDailyLoginDto.java`
    - `DeptMonthlyLoginDto.java`
    - `HolidayItemDto.java` — 공휴일 개별 항목
    - `HolidayResponseDto.java` — 공공 API 응답 매핑 (중첩 클래스)
    - `YearCountDto.java`
    - `YearMonthCountDto.java`

- entity (MariaDB 테이블 매핑)
    - `LoginLog.java`
    - `Member.java`
    - `Dept.java`

- service (비즈니스 로직)
    - `StatisticService.java` — 통계 조회 + 휴일 필터링
    - `HolidayService.java` — 공휴일 목록 조회

- resources (SQL)
    - `StatisticMapper.xml`

### API 명세서

> Base URL: `/api/v1/stats`  
> Content-Type: `application/json`  
> 공통 파라미터: 모든 API는 `startDate`, `endDate`를 필수로 받아 기간 내 데이터를 조회합니다.

---

#### 1. 월별 접속자 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/monthly` |
| 설명 | 기간 내 월별 고유 접속자 수를 조회한다 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM` | 조회 시작 월 |
| endDate | String | O | `yyyy-MM` | 조회 종료 월 |

**Request 예시**
```
GET /api/v1/stats/monthly?startDate=2024-01&endDate=2024-06
```

**Response 예시**
```json
[
  { "month": "2024-01", "loginCount": 150 },
  { "month": "2024-02", "loginCount": 200 }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| month | String | 월 (yyyy-MM) |
| loginCount | long | 해당 월 고유 접속자 수 |

---

#### 2. 일자별 접속자 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/daily` |
| 설명 | 기간 내 일자별 고유 접속자 수를 조회한다 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM-dd` | 조회 시작일 |
| endDate | String | O | `yyyy-MM-dd` | 조회 종료일 |

**Request 예시**
```
GET /api/v1/stats/daily?startDate=2024-03-01&endDate=2024-03-31
```

**Response 예시**
```json
[
  { "date": "2024-03-01", "loginCount": 45 },
  { "date": "2024-03-02", "loginCount": 32 }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| date | String | 일자 (yyyy-MM-dd) |
| loginCount | long | 해당 일자 고유 접속자 수 |

---

#### 3. 평균 하루 로그인 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/daily-avg` |
| 설명 | 기간 내 하루 평균 로그인 수를 조회한다 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM-dd` | 조회 시작일 |
| endDate | String | O | `yyyy-MM-dd` | 조회 종료일 |

**Request 예시**
```
GET /api/v1/stats/daily-avg?startDate=2024-01-01&endDate=2024-06-30
```

**Response 예시**
```json
{
  "averageDailyLogin": 42.5,
  "totalDays": 182,
  "totalLogins": 7735
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| averageDailyLogin | double | 일 평균 로그인 수 |
| totalDays | int | 조회 기간 일수 |
| totalLogins | long | 조회 기간 총 로그인 수 |

---

#### 4. 휴일을 제외한 로그인 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/daily-no-holiday` |
| 설명 | 기간 내 휴일(주말/공휴일)을 제외한 일자별 접속자 수를 조회한다 |
| 데이터 소스 | DB(일자별 로그인) + 공공 공휴일 API(한국천문연구원) |
| 필터링 방식 | Service 레이어에서 공휴일 + 주말(토/일) 제외 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM-dd` | 조회 시작일 |
| endDate | String | O | `yyyy-MM-dd` | 조회 종료일 |

**Request 예시**
```
GET /api/v1/stats/daily-no-holiday?startDate=2024-03-01&endDate=2024-03-31
```

**Response 예시**
```json
[
  { "date": "2024-03-04", "loginCount": 55 },
  { "date": "2024-03-05", "loginCount": 48 }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| date | String | 일자 (yyyy-MM-dd, 평일만) |
| loginCount | long | 해당 일자 고유 접속자 수 |

**처리 흐름**
```
Controller.getDailyLoginsWithoutHoliday()
  → StatisticService.getDailyLoginsWithoutHoliday()
      ├── StatisticMapper.selectDailyLoginCount()  → 일자별 로그인 전체
      └── HolidayService.getHolidays()             → 공휴일 날짜 목록
           └── HolidayClient.fetchMonthHolidays()   → 공공 API 월별 호출
      → stream 필터링 (공휴일 + 주말 제외) 후 반환
```

**예외 처리**
- 공공 API 4xx/5xx 응답: `onStatus`로 로깅 후 빈 공휴일 목록 반환
- 네트워크 장애/타임아웃: `try-catch`로 로깅 후 빈 공휴일 목록 반환
- 공공 API 실패 시에도 로그인 통계 자체는 정상 반환 (휴일 필터링만 생략)

---

#### 5. 부서별 월별 로그인 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/monthly-dept` |
| 설명 | 기간 내 부서별 월별 고유 접속자 수를 조회한다 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM` | 조회 시작 월 |
| endDate | String | O | `yyyy-MM` | 조회 종료 월 |

**Request 예시**
```
GET /api/v1/stats/monthly-dept?startDate=2024-01&endDate=2024-06
```

**Response 예시**
```json
[
  { "deptName": "개발팀", "month": "2024-01", "loginCount": 80 },
  { "deptName": "마케팅팀", "month": "2024-01", "loginCount": 45 }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| deptName | String | 부서명 |
| month | String | 월 (yyyy-MM) |
| loginCount | long | 해당 부서의 월별 고유 접속자 수 |

---

## 테스트 전략

현재 테스트는 "DB와 외부 API를 같이 붙여보는 데모성 확인"보다, 각 계층의 책임이 실제로 맞게 동작하는지를 빠르고 안정적으로 검증하는 방향으로 구성했다.

### 왜 이렇게 나눴는가

- 컨트롤러 테스트는 HTTP 계약에 집중한다.
- 서비스 테스트는 비즈니스 규칙에 집중한다.
- 외부 공휴일 API는 단위 테스트에서 Mock 처리한다.

세부적으로는 아래를 확인한다.

- 요청 파라미터가 올바르게 전달되는지
- 응답 상태 코드와 JSON 필드 구조가 맞는지
- 공휴일과 주말 제외 규칙이 깨지지 않는지
- Mapper와 외부 서비스 호출 위임이 올바른지

### 테스트 구성

- `StatisticControllerTest`
  - `@WebMvcTest` 기반
  - `StatisticService`를 Mock 처리
  - 엔드포인트의 요청/응답 계약 검증
- `StatisticServiceTest`
  - Mockito 기반 단위 테스트
  - `StatisticMapper`, `HolidayService`를 Mock 처리
  - `daily-no-holiday`의 핵심 필터링 규칙 검증
- `HolidayServiceTest`
  - 월 단위 공휴일 수집과 날짜 포맷 변환 검증
  - 잘못된 `locdate` 데이터 무시 여부 검증

### 실행 방법

```bash
./mvnw test
```

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

### 이번에 의도적으로 제외한 것

- 실제 MariaDB를 붙인 통합 테스트
- 실제 공공 공휴일 API 호출 테스트

이 둘은 로컬에서는 재현성이 낮고 유지비가 크다. 실무에서는 이런 영역을 별도의 통합 테스트 환경이나 QA 시나리오로 분리하고, 기본 CI 테스트는 지금처럼 빠르고 결정적인 단위/슬라이스 테스트로 유지하는 편이 일반적이다.
