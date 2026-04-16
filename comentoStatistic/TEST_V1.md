# API 테스트 가이드

## 사전 준비

### 1. 환경변수 설정
```
HOLIDAY_API_KEY=발급받은_공공_API_키
```

### 2. 서버 실행
```bash
mvn spring-boot:run
```
- Base URL: `http://localhost:8031`

### 3. DB 데이터 확인
- `login_log` 테이블에 테스트용 데이터 존재 여부 확인
- `dept` 테이블에 부서 + 멤버 매핑 데이터 존재 여부 확인

---

## 1. 월별 접속자 수

### Postman
| 항목 | 값 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8031/api/v1/stats/monthly` |
| Params | `startDate=2024-01` / `endDate=2024-06` |

### curl
```bash
curl -X GET "http://localhost:8031/api/v1/stats/monthly?startDate=2024-01&endDate=2024-06"
```

### Spring Boot Test
```java
@SpringBootTest
@AutoConfigureMockMvc
class MonthlyLoginTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 월별_접속자_수_조회() throws Exception {
        mockMvc.perform(get("/api/v1/stats/monthly")
                .param("startDate", "2024-01")
                .param("endDate", "2024-06"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").exists())
                .andExpect(jsonPath("$[0].loginCount").isNumber());
    }
}
```

### 검증 포인트
- [ ] 응답이 `[ { "month": "yyyy-MM", "loginCount": n } ]` 형태인지
- [ ] startDate~endDate 범위 내 월만 반환되는지
- [ ] loginCount가 고유 접속자 수(distinct)인지

---

## 2. 일자별 접속자 수

### Postman
| 항목 | 값 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8031/api/v1/stats/daily` |
| Params | `startDate=2024-03-01` / `endDate=2024-03-31` |

### curl
```bash
curl -X GET "http://localhost:8031/api/v1/stats/daily?startDate=2024-03-01&endDate=2024-03-31"
```

### Spring Boot Test
```java
@Test
void 일자별_접속자_수_조회() throws Exception {
    mockMvc.perform(get("/api/v1/stats/daily")
            .param("startDate", "2024-03-01")
            .param("endDate", "2024-03-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].date").exists())
            .andExpect(jsonPath("$[0].loginCount").isNumber());
}
```

### 검증 포인트
- [ ] 응답이 `[ { "date": "yyyy-MM-dd", "loginCount": n } ]` 형태인지
- [ ] 날짜가 오름차순 정렬인지
- [ ] startDate~endDate 범위 내 일자만 반환되는지

---

## 3. 평균 하루 로그인 수

### Postman
| 항목 | 값 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8031/api/v1/stats/daily-avg` |
| Params | `startDate=2024-01-01` / `endDate=2024-06-30` |

### curl
```bash
curl -X GET "http://localhost:8031/api/v1/stats/daily-avg?startDate=2024-01-01&endDate=2024-06-30"
```

### Spring Boot Test
```java
@Test
void 평균_하루_로그인_수_조회() throws Exception {
    mockMvc.perform(get("/api/v1/stats/daily-avg")
            .param("startDate", "2024-01-01")
            .param("endDate", "2024-06-30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.averageDailyLogin").isNumber())
            .andExpect(jsonPath("$.totalDays").isNumber())
            .andExpect(jsonPath("$.totalLogins").isNumber());
}
```

### 검증 포인트
- [ ] 응답이 단일 객체 `{ "averageDailyLogin": n, "totalDays": n, "totalLogins": n }` 인지
- [ ] averageDailyLogin = totalLogins / totalDays 와 근사한지
- [ ] 배열(`[]`)이 아닌 단일 객체로 반환되는지

---

## 4. 휴일을 제외한 로그인 수

### Postman
| 항목 | 값 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8031/api/v1/stats/daily-no-holiday` |
| Params | `startDate=2024-03-01` / `endDate=2024-03-31` |

### curl
```bash
curl -X GET "http://localhost:8031/api/v1/stats/daily-no-holiday?startDate=2024-03-01&endDate=2024-03-31"
```

### Spring Boot Test
```java
@Test
void 휴일_제외_로그인_수_조회() throws Exception {
    mockMvc.perform(get("/api/v1/stats/daily-no-holiday")
            .param("startDate", "2024-03-01")
            .param("endDate", "2024-03-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].date").exists())
            .andExpect(jsonPath("$[0].loginCount").isNumber());
}
```

### 검증 포인트
- [ ] 토요일/일요일 날짜가 결과에 포함되지 않는지
- [ ] 공휴일(예: 3/1 삼일절)이 결과에 포함되지 않는지
- [ ] `/daily` API 결과보다 행 수가 적은지 (같은 기간 비교)
- [ ] HOLIDAY_API_KEY가 미설정일 때 에러 없이 응답되는지 (필터링만 생략)

---

## 5. 부서별 월별 로그인 수

### Postman
| 항목 | 값 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8031/api/v1/stats/monthly-dept` |
| Params | `startDate=2024-01` / `endDate=2024-06` |

### curl
```bash
curl -X GET "http://localhost:8031/api/v1/stats/monthly-dept?startDate=2024-01&endDate=2024-06"
```

### Spring Boot Test
```java
@Test
void 부서별_월별_로그인_수_조회() throws Exception {
    mockMvc.perform(get("/api/v1/stats/monthly-dept")
            .param("startDate", "2024-01")
            .param("endDate", "2024-06"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].deptName").exists())
            .andExpect(jsonPath("$[0].month").exists())
            .andExpect(jsonPath("$[0].loginCount").isNumber());
}
```

### 검증 포인트
- [ ] 응답이 `[ { "deptName": "", "month": "yyyy-MM", "loginCount": n } ]` 형태인지
- [ ] 부서가 없는 멤버의 로그인은 제외되는지 (INNER JOIN)
- [ ] 같은 월에 여러 부서가 각각 나오는지

---

## 에러 케이스 테스트

| 테스트 | curl | 예상 결과 |
|--------|------|-----------|
| 파라미터 누락 | `curl "http://localhost:8031/api/v1/stats/monthly"` | 400 Bad Request |
| 잘못된 날짜 형식 | `curl "...?startDate=abc&endDate=def"` | 에러 응답 |
| 데이터 없는 기간 | `curl "...?startDate=1999-01&endDate=1999-12"` | 빈 배열 `[]` |
| 존재하지 않는 URL | `curl "http://localhost:8031/api/v1/stats/unknown"` | 404 Not Found |
