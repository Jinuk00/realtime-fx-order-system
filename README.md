# 실시간 환율 기반 외환 주문 시스템

Spring Boot 3.x + Java 17 + H2 기반의 과제 구현입니다.

## 외부 API 키 발급

- 환율 API 키는 공공데이터포털 한국수출입은행 환율 정보에서 발급받습니다.
- 발급 안내 페이지: [https://www.koreaexim.go.kr/ir/HPHKIR020M01?apino=2&viewtype=C&searchselect=&searchword=](https://www.koreaexim.go.kr/ir/HPHKIR020M01?apino=2&viewtype=C&searchselect=&searchword=)
- 발급받은 키를 `.env`의 `EXIM_AUTH_KEY` 값으로 넣어 실행하면 됩니다.

## 주요 엔드포인트

- `GET /exchange-rate/latest`
- `GET /exchange-rate/latest/{currency}`
- `POST /order`
- `GET /order/list`

## 예시 요청/응답

```bash
curl http://localhost:8080/exchange-rate/latest
```

```json
{
  "code": "OK",
  "message": "정상적으로 처리되었습니다.",
  "returnObject": {
    "exchangeRateList": [
      {
        "currency": "USD",
        "buyRate": 1480.43,
        "tradeStanRate": 1477.45,
        "sellRate": 1474.47,
        "dateTime": "2026-04-22T10:01:00"
      },
      {
        "currency": "JPY",
        "buyRate": 915.20,
        "tradeStanRate": 910.50,
        "sellRate": 905.80,
        "dateTime": "2026-04-22T10:01:00"
      }
    ]
  }
}
```

## 구현 포인트

- 환율 수집 스케줄러: 1분 주기
- 외부 API: 한국수출입은행 환율 API 연동(`deal_bas_r`, `ttb`, `tts`)
- 외부 API 장애 시 이전 시세 기반 mock 변동값 저장 fallback
- 환율 반올림(소수 둘째 자리), JPY 100엔 단위, KRW floor 처리

## 설정 주의

- `.env` 파일에 `EXIM_AUTH_KEY`를 설정해야 외부 API 호출이 가능합니다.
