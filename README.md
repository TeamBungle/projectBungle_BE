
![배너용](https://user-images.githubusercontent.com/107230384/182052615-f4743530-6596-4b4f-9b5e-6100f021eebb.jpg)

<div align="center"><h3>싱글벙글, 사용자 현재 위치 기반으로 빠르게 주변 모임을 확인할 수 있습니다.</h3></div>

## 🤩 벙글 [서비스 링크 바로가기](https://bungle.life)
## 😖 벙글 [발표 영상 바로가기](https://youtu.be/AoN3nuWR9Hg)
## 🤗 벙글 [시연 영상 바로가기](https://youtu.be/aJnM2TuXXWg)

## 😆 프로젝트 Git address

- Back-end Github    https://github.com/TeamBungle/projectBungle_BE
- Front-end Github   https://github.com/TeamBungle/projectBungle_FE

## 😶 벙글 팀원 소개( L : 팀장, LV : 부팀장 )
<!-- 표 시작 -->
<div align="center">
<table>
      <thead>
        <tr>
          <th>역할</th><th>이름</th><th>개인 Git 주소</th><th>개인 메일 주소</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>강현구님</td><td>https://github.com/kootner</td><td>refromto@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>( LV ) 김민수님</td><td>https://github.com/minssu86</td><td>manager.kim86@gmail.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>김정훈님</td><td>https://github.com/junghoon-kim96</td><td>0527wj@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>정현욱님</td><td>https://github.com/Jeonghyeonuk</td><td>junghunwook456@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>최서우님</td><td>https://github.com/zerovodka</td><td>264826@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>한결님</td><td>https://github.com/GHan19</td><td>gksrufdla@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>( L ) 한지용님</td><td>https://github.com/jigomgom</td><td>hjy583@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white"></td><td>양승연님</td><td></td><td>didtmddus123@gmail.com</td>
        </tr>
      </tbody>
    </table>
</div>
<!--표 끝-->
<p align="center"><img src="https://user-images.githubusercontent.com/107230384/182984029-9de38ffc-186e-415c-a372-76b2bf59a6dd.png"></p>    

## 🤔 프로젝트 시장 조사 및 Feasibility Test

- <p><a href="https://www.notion.so/Feasibility-test-b8f7d2dccd354a0db0577e245a12f4a4">팀 Feasibility Test 페이지 </a>로 바로가기</p>

## 😏 QA 프로세스
- <p><a href="https://www.notion.so/QA-c3a48710c4f241298990b8413bff3e0d">팀 QA 프로세스 페이지</a>로 바로가기</p>

## 😤 BE 기술 Stack

## 프레임워크

### Spring boot

- 국내에서는 예전부터 현재까지 Back-end 언어로 JAVA가 많이 사용되고 있는 것으로 알고 있으며, Spring boot는 이전 버전들에 비해 구조가 간단하여 접근성이 좋다.

## 스택

### JWT

- Session을 이용한 방식과 JWT 인증방식 두가지를 고민하였으며, 그 결과 Session방식에 비해 별도의 저장소가 필요없는 JWT방식이 서버자원 절약에 유리하다고 판단하여 적용하였음

### OAuth2

- 사용자 입장에서는 여러 서비스들을 하나의 계정으로 관리할 수 있게되어 편해지고 개발자 입장에서는 민감한 사용자 정보를 다루지 않아 위험부담이 줄고 서비스 제공자로부터 사용자 정보를 활용할 수 있다

### redis

- Message broker 역할로 사용
- 잦은 조회가 예상되는 자료를 저장하는 in memory cache로 사용

### mysql

- RDB 특성상 정해진 스키마에 따라 데이터를 명확하게 구분해서 저장해야 되기 때문에 데이터 구조 설계시 불필요한 데이터 중복과 잘못된 데이터 저장 작업을 줄일 수 있어 사용
- 현재 진행중인 프로젝트 규모가 크지 않아 여러 RDB중 mysql로도 충분한 커버가 가능할 것으로 판단

### S3

- 모든 미디어 파일을 한번에 관리할수 있다
- 인증시스템을 설정하여 보안이 좋다

### JPA

- 초기 개발 과정에서 비지니스로직 구성에 집중하기 위해 팀원들의 숙련도가 비교적 높은 JPA 사용
- N+1문제 해결과 조회 성능 향상을 위해 nativequery를 함께 적용

### WebSocket

- Http 통신은 클라이언트의 요청이 있을때만 서버가 응답하는 단방향 통신이여서 실시간 채팅 서비스 구현에 적절하지 못하기 때문에 양방향 통신을 지원하는 Socket 통신 방식을 적용하기 위해 WebSocket 프로토콜 사용

### Stomp

- Spring Security를 적용해 메세지 보호 가능
- WebSocket만 사용해서 구현하면 해당 메시지가 어떤 요청인지, 어떤 포맷으로 오는지 그리고 메시지 통신 과정을 어떻게 처리해야하는지 정해져 있지 않아 일일이 구현해야 한다. 이를 보완하기 위해 Stomp를 사용하여 메시지의 형식, 유형, 내용등을 정의할 수 있으며, 단순한 Binary, Text가 아닌 규격을 갖춘 메시지를 보낼수 있어 사용
- 메세지 브로커로 In Memory Broker를 사용하면 세션 수용 크기가 제한 되는등 단점이 있어, RabbitMQ, ActiveMQ등 전용 외부 브로커 사용이 가능하여 확장성이 좋은 STOMP를 사용

### SockJS

- websocket프로토콜을 지원하지 않는 브라우저에서 Http Streaming, Long-Polling 같은 Http 기반의 다른 기술로 전환해  연결하기 위해 사용

### EC2

- 이번 프로젝트는 수익성을 목표로한 프로젝트가 아니다보니 초기 투자비용이 발생가능한 부분을 배제하는 과정에서 프리티어를 제공하는 AWS의 EC2가 최적이라 판단되었고,  RDS, S3등 AWS 제공하는 서비스를 사용하기에 함께 관리하기에도 효율적이라 판단하여 사용

### NginX

- 웹소켓 이용한 채팅기능이 포함 되있기 때문에 동시 접속자 수의 증가에 대해 대응하기 적합
- 채팅 및 알람기능의 사용성 및 편의성 증대를 위한 무중단 배포가 가능
- SSL 인증서를 통한 HTTPS 환경 설정

### CI/CD (GitHub Actions, S3, Code Deploy)

- 빌드/ 테스트/배포를 자동화 시켜서 개발 속도 향상
- Jenkins와 비교하여 Github Actions는 별도의 server가 필요없고 설정이 간편하여 적용하기에 용이하다고 판단


## 🙂 아키텍쳐

![최종 아키텍처](https://user-images.githubusercontent.com/107230384/182052947-7c29f084-224a-492b-9a71-0c0f09c65a9e.jpg)

## 😮 Data base 설계
![image](https://user-images.githubusercontent.com/87007109/182985563-f223fd87-d7ac-4688-8102-cf0a5d8bafac.png)

## 🤩 벙글 주요 기능

1. GPS를 사용한 현재 위치 확인 ( geolocation )
   - 사용자 위치 기준, 400km 반경의 실시간 벙글 위치와 마감 임박순 벙글 을 나타냄
   - 서비스 론칭 기간이 짧기 때문에 400km로 결정, 추후 데이터가 많이 쌓이면 50km로 변경 예정

<p align="center"><img src="https://user-images.githubusercontent.com/107230384/182052513-562cce1e-09d0-4496-aceb-e17440cf3b22.png" width="920px" height="400px"></p>    

2. 실시간 벙글 생성 및 문자 채팅 ( Redis, StompJS, SockJS )
    - 벙글 생성을 통해 주변 사람들과 모임을 가질 수 있고 문자 채팅에서 상세 결정을 내릴 수 있음
    - 사용자 피드백에 따라 화상 채팅 추가 예정
<p align="center"><img src="https://user-images.githubusercontent.com/107230384/182052538-9e3d28f1-0f7f-4604-a944-35c920fa2aca.png"></p>    

3. 지도를 통해 실시간 진행 중인 벙글 확인 ( Redis, Kakao map API )
<p align="center"><img src="https://user-images.githubusercontent.com/107230384/182052556-d5fb2af0-7617-403a-9e39-a3becd215dd3.png"></p>


## 🧐 Trouble Shooting

- BE Trouble Shooting

    <details>
    <summary>사용자 위치 기반 정보 검색 및 데이터 정렬</summary>
    <pre>
    1. 문제 인지
      - 게시글 조회시 유저의 위치로부터 일정 거리내에 있는 게시물 만을 DB로부터 불러와 거리순에 맞춰 응답하는 로직 필요
      - 초기 기능 구현시 기본적인 Spring Data JPA만을 사용한 결과 N+1 문제 및 DB로부터 불러온 data를 JAVA 코드로 재 정렬해야하는 문제 발생
    2. 선택지
      - 1. MBRContaions 사용
      - 2. ST_DISTANCE_SPHERE 사용
    3. 해결 
      [ 2번 선택 ]
       - MBRContains 적용시 DB에서 해당 데이터 추출후 거리순 정렬 작업이 추가로 필요하여, Query조회시 거리순 정렬 및 추출을 한번에 할 수 있는 ST_DISTANCE_SPHERE를 사용하는 것이 더 좋다고 판단하였지만, 부하 테스트 결과 MBRContains 사용시 성능이 더 좋은 결과가 나왔습니다. 향후 테스트를 추가 진행하여 같은 결과 도출 시 현재 적용중인 ST_DISTANCE_SPHERE를 MBRContains로 변경 예정
    </pre>
    <h5>ST_DISTANCE_SPHERE 적용 코드</h5>	https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/PostService.java#L118-L141
    </details>
    

    <details>
    <summary>회원 가입시 사용자 인증</summary>
    <pre>
    1. 문제 인지
      회원 가입시 email 인증 메일 발송 로직에서 2~3초간의 대기시간이 걸려 가입 버튼을 클릭한 사용자가 대기해야하는 문제 발생 </div>
    2. 선택지
      1. 비동기 처리
    3. 해결 방법
       [ 1 선택 ]
        - email 전송 method에 @Async Annotation을 이용해 비동기 처리 하여 유저의 대기 시간을 줄였음
    </pre>
    <h5>@Async Annotation 적용 코드</h5> https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/EmailConfirmTokenService.java#L24-L51
    </details>

    <details>
    <summary>서비스 이용시 탈취 될 수 있는 유저 정보 보안</summary>
    <pre>
    1. 문제 인지
      유저인증 방식으로 JWT를 이용한 Access Token 발행 방식을 사용하였으며, 이때 Token이 타인에게 탈취 되었을때를 대비가 필요 하였음
    2. 선택지
      1. Access Token 만 사용
      2. Access , Refresh Token 함께 사용
    3. 해결 방법
      [ 2 번 선택 ]
       - Access Token의 만료 시간을 짧게 두어 탈취 되었을 경우 악용가능한 시간을 줄였으며, Access Token발행 시 만료 기간이 긴 Refresh Token을 함께 발행 하여 Access Token 만료시 재로그인으로 Access Token을 갱신하는 것이 아닌 Refresh Token 인증을 통해 Access Token을 갱신하였음. 이때, Refresh Token은 in memory cache인 redis에 저장하여 잦은 조회로 인해 발생가능한 DB부담을 줄였음
    </pre>
    <h5>Refresh Token 적용 코드</h5> https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/UserService.java#L188-L222
    </details>

    <details>
    <summary>Websocket을 사용하여 실시간 알림</summary>
    <pre>
    1. 문제 인지
      기존 기능 구현시 유저가 채팅방에 입장할때마다 Websocket을 Connect하고 나갈때마다 Disconnect 하였으나, 
      처음 로그인했을때 Connect 후, 로그아웃 하거나 웹페이지를 빠져나갈때 Disconnect가 되어야 안읽은 메세지에 대한 실시간 알림이 구현 된다고 판단하여 로직을 바꾸는 시도를 하였음
    2. 선택지
      1. 채팅방에 입장할때 Connect 후 그방에대한 Subscribe 진행
      2. 채팅방에서 나갈때 Disconnect
     2-1. 선택지 시도 순서
       1. 로그인할때 WebSocket Connect
       2. 채팅방에 입장할때 그방에대한 Subscribe 진행
       3. 채팅방에서 나갈때 그방에대한 Unsubscribe 진행
       4. 로그아웃할때 Websocket Disconnect
    3. 해결 방법
      Websocket을 하나 열고 그안에서 여러개의 sub,unsub을 진행하려고 하는 과정에서 에러가 많이 발생했고, 
      시간관계상 프로젝트 마무리까지 얼마 남지않아 방식을 바꾸기로 결정하고 구글링 및 멘토님께 자문을 구한 결과 http 프로토콜을 사용한 비동기 통신을 이용해서 구현 하기로 결정
     3-1. 실제 반영
       front에서 5초마다 알림을 조회하는 요청을 보내고 그에대한 응답으로 사용자가 채팅방에서 나간 시간을 저장하여, 
       그시간 이후로 그방에서 보내진 메세지들을 응답으로 보내주는 방식으로 구현
    </pre>
    </details>
        
    <details>
    <summary>Spring Security를 적용시 발생한 Websocket 연결 문제</summary>
    <pre>
    1. 문제 인지
      Spring Security를 적용하지 않은 상태에서 클라이언트와 서버간의 연결에 문제가 없이 정상적으로 작동 하였으나, Security를 적용하고 연결을 시도하니 401 에러가 발생
    2. 선택지
    3. 해결 방법
     3-1. 문제 해결 과정
      a) WebSocket은 Custom Header 적용이 안되는 것으로 확인
       - 관련자료 : https://velog.io/@tlatldms/Socket-%EC%9D%B8%EC%A6%9D-with-API-Gateway-Refresh-JWT
      b) Hand Shake하는 과정을 Security에서 Pass를 걸어 시도
       - 실패, 실패 원인 분석 필요
      c) Stomp Handler를 만들어서 intersepter를 적용하여서 Token검사를 시도
       - 실패, Token 자체를 받아올 수 가 없었음
      <p align="center"><img src="https://velog.velcdn.com/images/junghunuk456/post/bd2cb4f4-c822-4f9f-9c9f-82855d298b85/image.png"></p>
      d) 첫 HandShake 과정부터 차례대로 log를 찍어서 확인 해 본 결과 Sockjs를 사용시 우리가 정해놓은 EndPoint 뒤에 여러 path을 붙여서 접속을 시도하는 것을 확인
       - 정해놓은 EndPoint가 (“ws/chat”)이었는데, “ws/chat/934/czvkhxvy/websocket << 이런 식으로 뒤에 path 를 붙여서 요청이 들어옴
      <p align="center"><img src="https://velog.velcdn.com/images/junghunuk456/post/5439c533-9e22-40c9-b89c-4886f2972395/image.png"></p>
      e) 적용했던 Security API path 변경 시도
       - 실패
       [ 기존 API path ]
       <p align="center"><img src="https://velog.velcdn.com/images/junghunuk456/post/424d3e82-4cf1-40c5-aef5-72872b21c3de/image.png"></p>
       하지만 이 상태에서는 ws/chat/** 이런식으로 뒤에 와일드카드를 붙여서 전부다 API path를 허용하는것이 불가능 하였기 때문에 ws/chat을 path 시켜도,
       뒤에 붙는 path들이 전부 다르기 때문에 적용이 안되었음
      f) Security 구조 변경
      - 성공
      <p align="center"><img src="https://velog.velcdn.com/images/junghunuk456/post/57fb71d1-a381-49b5-8770-b9a4bddbf40f/image.png"></p>
      위와 같이, 구조를 변경하고 와일드카드를 사용하여 path시키니 정상적으로 작동
    </pre>
    </details>
    <details>
    <summary>Refresh token 이슈로 인한 채팅 send 문제</summary>
    <pre>
    1. 문제 인지
     refresh Token 적용 후 , Access Token 의 만료시간이 지나 refresh Token을 사용하여 AccessToken을 갱신 하는 과정에서 갱신을 시도할때 
     보내는 첫번째 메세지가 채팅창에 입력이되지 않는 현상이 발생
    2. 선택지
     1. message body에 access_token을 넣어 유저 유효성을 검사
     2. connect 할 때만 유저의 유효성 검사
    3. 해결 방법
     [ 2번 선택 ]
      Token을 사용하여 유저의 유효성을 검증하는것은 처음 WebSocket에 Connect 할때에만  하는것으로도 충분하다고 생각하여 
      Connect 할때(채팅방에 입장할때)에만 Token을 받아서  유효성 검사를 진행하고, 메세지를 주고 받을때는 기존에 사용하던 Token이 아닌, 
      User의 PK값을 받아서 user정보를 찾아 return시켜 주는 방법으로 변경
    </pre>
    </details>
    <details>
    <summary>Redis Message Serialize 문제</summary>
    <pre>
    1. 문제 인지
      Reids 에 Message들을 저장할때 Serialize 하는 부분에서 에러발생
    2. 선택지
      구글링을 통한 문제 해결
    3. 해결 방법
      - 메세지를 저장할때 메세지를 보낸 시간을 저장하기위해서 LocalDateTime을 사용하였는데, 자료를 조사해본결과 Java8 버전에서는 LocalDateTime을 직렬화,역직렬화 하지 못함
      - redis 에 저장하기 전, LocaldateTime 을 String으로 변환하여 저장
    </pre>
    </details>
    <details>
    <summary>채팅방 진입 문제</summary>
    <pre>
    1. 문제 인지
      유저가 채팅 방에 정상 진입 되지 않는 문제가 발생
    2. 선택지
    3. 해결 방법
     3-1. 문제 해결 과정
      a) 기존에 서버쪽에서 postId(Pk)를 roomId로 사용하였는데 TopicChannel Class에서 param을 String으로 받기 때문에 
      Long type 인 postId를 String으로 형 변환 하여 사용하는 중이었음, 이때 채팅방에 유저가 진입을 시도할경우 방입장이 정상적으로 진행되지 않음
      b) 확인 결과 Client에서 보내주는 값이 Long Type으로 와서 채팅방입장이 정상적으로 진행되지 않음
      c) Client에서 값을 String으로 변환하여 보내줘서 문제 해결 
    </pre>
    </details>
- <p><a href="https://github.com/TeamBungle/projectBungle_FE"> FE Trouble Shooting </a> 로 바로가기</p>

## 😇 성능 테스트
<details>
<summary>채팅 Message 조회 부하 문제</summary>
<pre>
문제
 - 채팅방에 입장 할 때 이전 메세지를 불러오는 방식에대해서 처음에는 DB에서 바로 조회해오는 방식으로 구상하였으나, 
   해당 방법은 트래픽에 부담이 크지 않을까? 라는 의문과 함께 조회방식에 대한 최적화를 고민
해결방법
 - memory cache에 저장 후 조회하는 방식이 RDB로부터 불러오는 방식보다 성능이 더 좋다는 것을 조사를 통해 알게 됨
 - 메세지를 저장할 때는 Redis와 DB에 같이 저장하고, 메세지를 조회할 경우에는 Redis Cache를 사용하는 로직 적용 후 테스트 결과
   RDB에서 조회 하는 것 대비 18% 성능 향상을 확인할 수 있었음
   <p align="center"><img src="https://user-images.githubusercontent.com/107230384/183238734-c914cb06-6fca-4364-8406-0b6e751aa08c.png"></p>
</pre>
<details>

