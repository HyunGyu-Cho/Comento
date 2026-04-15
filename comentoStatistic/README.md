## S/W 통계 API 구축

### 구조
- dao(service 와 MyBatis를 연결해주는 중개자 역할)
    - `StatisticMapper.java`

- dto
    - `DailyLoginDto.java`
    - `MonthlyLoginDto.java`
    - `YearCountDto.java`
    - `YearMonthCountDto.java`

- entity(단지 mariadb 테이블 기록용)
    - `LoginLog.java`
    - `Member.java`

- service
    - `StatisticService.java`

- resources(실제 SQL문)
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

#### 4. 휴일을 포함한 로그인 수

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/api/v1/stats/daily-with-holiday` |
| 설명 | 기간 내 휴일(주말/공휴일)을 포함한 일자별 접속자 수를 조회한다 |

**Request**

| 파라미터 | 타입 | 필수 | 형식 | 설명 |
|----------|------|------|------|------|
| startDate | String | O | `yyyy-MM-dd` | 조회 시작일 |
| endDate | String | O | `yyyy-MM-dd` | 조회 종료일 |

**Request 예시**
```
GET /api/v1/stats/daily-with-holiday?startDate=2024-03-01&endDate=2024-03-31
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
| date | String | 일자 (yyyy-MM-dd, 휴일 포함) |
| loginCount | long | 해당 일자 고유 접속자 수 |

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

