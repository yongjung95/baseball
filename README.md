# Baseball DashBoard (최신화 2024/10/01)

### 주소
- [야꾸야꾸](https://yaggu.n-e.kr)

### 기술 스택
- Java 17, 네이버 검색 API, Spring Boot 3.3.3
- Redis, RabbitMQ, MariaDB, AWS EC2, Docker, Github Actions

### 기술 내용
- `RabbitMQ`를 사용하여 실시간 채팅과 실시간 댓글 알림 기능을 구현.
    - 서버가 여러 대인 경우에도 `RabbitMQ`의 브로커 역할을 통해 서버 간의 메시지 전달이 가능.
      - `STOMP 프로토콜`과 `WebSocket`을 결합하여 실시간 메시징을 지원하며, `RabbitMQ`의 `Topic Exchange`를 사용하여 사용자들에게 메시지를 전송.
      - 이를 통해 서버 A에 접속한 사용자가 서버 B에 접속한 사용자와도 실시간으로 채팅을 지원.
- `Redis`를 사용하여 데이터 조회 성능을 최적화.
  - 빈번하게 조회되는 데이터를 캐시로 저장함으로써 데이터베이스에 대한 부하를 줄이고 응답 속도를 향상.
  - `TTL` 기능을 사용해 캐시의 유효 기간을 10분으로 설정하여 오래된 데이터가 남지 않도록 관리.