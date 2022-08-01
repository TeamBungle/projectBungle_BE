
![배너용](https://user-images.githubusercontent.com/107230384/182052615-f4743530-6596-4b4f-9b5e-6100f021eebb.jpg)


**싱글벙글, 사용자 현재 위치 기반으로 빠르게 주변 모임을 확인할 수 있습니다.**

### 🤔 프로젝트 시장 조사 및 Feasibility Test

[Feasibility test](https://www.notion.so/Feasibility-test-b8f7d2dccd354a0db0577e245a12f4a4) 

### 🙂 아키텍쳐

![최종 아키텍처](https://user-images.githubusercontent.com/107230384/182052947-7c29f084-224a-492b-9a71-0c0f09c65a9e.jpg)



### 🤩 벙글 주요 기능

1. GPS를 사용한 현재 위치 확인 ( geolocation )
    - 사용자 위치 기준, 400km 반경의 실시간 벙글 위치와 마감 임박순 벙글 을 나타냄
    - 서비스 론칭 기간이 짧기 때문에 400km로 결정, 추후 데이터가 많이 쌓이면 50km로 변경 예정
    
![Untitled (1)](https://user-images.githubusercontent.com/107230384/182052513-562cce1e-09d0-4496-aceb-e17440cf3b22.png)
    

2. 실시간 벙글 생성 및 문자 채팅 ( Redis, StompJS, SockJS )
    - 벙글 생성을 통해 주변 사람들과 모임을 가질 수 있고 문자 채팅에서 상세 결정을 내릴 수 있음
    - 사용자 피드백에 따라 화상 채팅 추가 예정

![Untitled (2)](https://user-images.githubusercontent.com/107230384/182052538-9e3d28f1-0f7f-4604-a944-35c920fa2aca.png)


3. 지도를 통해 실시간 진행 중인 벙글 확인 ( Redis, Kakao map API )

![Untitled (3)](https://user-images.githubusercontent.com/107230384/182052556-d5fb2af0-7617-403a-9e39-a3becd215dd3.png)


### 🧐 Trouble Shooting

- BE Trouble Shooting
    <details>
        <summary>사용자 위치 기반 정보 검색 및 데이터 정렬</summary>
        <div style="">문제 인지<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>1. JPA Data 사용<br> 2. Native Query 사용</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>[2번 선택]<br>- Kakao Geocoding을 통해 DB에 저장된 위도, 경도 정보를 조회시 Native Query를 사용하여 Query 조회 성능 향상</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    <details>
        <summary>모임전 필요한 정보 공유를 위한 채팅 서비스를 제공</summary>
        <div style="">문제 인지<div>
            <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>1. 실시간 채팅 라이브러리( ex> PeerJS )<br>2. Stomp, SockJS, Redis pub/sub</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>[2번 선택]<br>- Websocket에 대한 전반적인 이해도가 부족한 상태에서, 라이브러리를 통해 구현<br>하려고 하다보니 개발이 잘 진행 되지 않음- 채팅 서버가 여러개로 나뉠경우, Spring 에서 제공하는 내장 broker로는 서로 다른 서버에 요청을 보낸 사용자끼리 채팅이 불가 하여 Reids pub/sub 방식을 사용</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
     <details>
        <summary>코드 병합할때 시큐리티 적용시 Websocket 이 정상연결 되지 않는 문제</summary>
        <div style=""> 1. 시큐리티 적용후 프론트에서 jwt token을 헤더에 담아서 보내서 채팅 연결 시도<div>
            <div> -> 401 에러 (이때부터 Reids pub/sub 적용함)
            <br>  -> websocket은 custom header를 적용 시킬 수 없는 부분 확인 (→ 관련 자료 https://velog.io/@tlatldms/Socket-인증-with-API-Gateway-Refresh-JWT)</div>
        <div style=""> 2. 시큐리티를 패스 시켜서 하려고 시도하였으나 실패<div>
        <div style=""> 3. stomp handler를 만들어서 websocket config에 intercepter 적용하였으나 실패 <div>
            <div> -> security 로직 문제 인것으로 판단
            <br>  -> token을 header에 넣어서 보냈으나, 받아지질 않으니 유효성 검사가 계속 실패하여 임시로 token을 만드는 로직을 구현해서 test를 진행
            <br>  -> 연결, 구독, 메세지 보내는 순서대로 테스트 - > 성공</div> 
        <div style="">정리) 시큐리티에서 토큰을 받아오지 못하는가<div>
            <div> 1. websocket 은 custom header가 안됨
            <br>  2. 따라서 security config에서 endpoint를 pass 시켜줘야만 한다.(permitAll)
            <br>  3. 그래서 pass를 시켜 줬지만 현재 사용하고 있는 security 로직에서는 와일드 카드가 적용이 안되서 security를 교체 함
            <br>   sockjs에서 요청을 서버쪽으로 보낼때, 현재 정해놓은 endpoint (”ws/chat”)뒤에다가 여러가지 url을 붙여서 요청을 보내오는데, 우리가 만든 security에서는 (”ws/chat/**)이런식으로 와일드카드가 적용이 안되어서 에러가 발생했었다. </div>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
    <details>
       <summary>Websocket 통신시 직렬화/역직렬화 문제</summary>
       <div style=""> 1. 웹소켓은 객체를 Serialization 해서 보내야 한다 ?<div>
           <div> -> spring에서 http 통신을 할때는 자동으로 jacson2HttpMessageConverter 내부에서 objectmapper를 이용해 역 직렬화 하여,
           <br>객체 형태로 만들어 줬으나 Websocket은 따로 역직렬화 해야하는점 확인 </div>
       <div style=""> 2. connect/sub 시에 sessionId를 받아오지 못하는 현상<div>
       <div>세션 아이디를 if문 밖에서 초기화 시켜야 했는데, Stirng session Id = “”; 형식으로 설정하여 진행되지 않음<div>
       <div style=""> 3. redis 캐시 사용시 Localdatetime , date serialize 문제 <div>
           <div> 메세지를 reids에 저장하고 return 시킬때 메세지가 작성된 시간을 저장하려고 LocaldateTime 을 사용하였는데, serialize 오류 발생
           <br>  -> 확인해 보니 redis 캐시에서는 localdatetime과 date << 이 두개의 type을 지원하지 않아, string으로 serialize해서 보내야 했음
           <br>  -> db에 redis에 있는 데이터가 몇개이상 쌓일때마다 저장을 하려고하니 오류 
           <br>  -> date type을 string으로 변환하여 해결 </div> 
       <div style="">해결방법<div>
           <div> Object mapper = new ObjectMapper();
           <br>List<타입> list = mapper.convertValue(returnlist, new TypeReference<List<타입>>(){});</div>
       <div markedown="1">
           https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
       </div>
   </details>
    
   <details>
       <summary>방장은 채팅방에 바로 입장 했지만 유저는 채팅 방에 정상 진입 하지 않는 문제 </summary>
       <div style=""> 기존에 게시물 Id 랑 room Id 를 같은 값을 사용하지만 TopicChannel 에 Class에서 param을 String으로 받기 때문에 
           <br> 게시물 Id 는 Long 으로 사용하지만 roomId 는 String 으로 형변환 하여 사용했었음 <div>
       <div style=""> 채팅 방에 진입할때 게시물 내에서 게시물 Id를 사용하여 입장을 시도 하였으나 방입장이 정상적으로 진행되지 않음 <div>
       <div>> 확인 결과 Client에서 보내주는 값이 Long 값으로 와서 채팅방입장이 정상적으로 진행되지 않음 
           <br> -> room Id를 형변환 하지 않고 Long 형태로 사용 할 수 있는지 방법 확인 
           <br> -> 확인결과 TopicChannel을 구현된 그대로 사용하는 이상 불가능한 점 확인 
           <br> -> Client에서 값을 String으로 변환하여 보내줘서 문제 해결 <div>    
       <div markedown="1">
           https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
       </div>
   </details>    
    
    <details>
        <summary>채팅 메세지 저장 및 채팅방 입장시 이전 메세지 출력 방식</summary>
        <div style="">문제 인지<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>- 1. Mysql 사용<br>2. Redis Cache사용</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    <details>
        <summary>읽지 않은 메세지에 대한 알림기능 구현</summary>
        <div style="">문제 인지<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>- 1. Websocket을 사용하여 실시간 알림<br>2. SSE를 사용하여 실시간 알림<br>3. http를 사용하여 알림</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>- [3번 선택]<br>- 프로젝트 마무리 시간을 고려하여, 시간이 충분히 여유롭지 않아 제일 익숙한 방식<br>인 http를 이용하여 알림을 구현하기로 함-  front에서 5초마다 알림을 조회하는 요청을 보내고 그에대한 응답으로 사용자가 채팅방에서 나간 시간을 저장하여, 그시간 이후로 그방에서 보내진 메세지들을 return시켜줌.</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    <details>
        <summary>회원 가입시 사용자 인증</summary>
        <div style="">문제 인지<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>- 1. Email 인증<br>2. OAuth 사용<br>3. only Id/Password</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>- [1, 2번 선택]- 일반 회원 가입의 경우 가입에 사용한 email로 인증 토큰을 전달후 유<br>저가 해당 토큰을 다시 서버로 전달 하면 서비스를 사용할 수 있도록 권한 변경- Oauth를 통해 유저들에게 친숙한 3개의 대형회사에 접근 권한 인증을 위임하여 신규 서비스의 단점인 신뢰성을 보강생</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    <details>
        <summary>서비스 이용시 탈취 될 수 있는 유저 정보 보안</summary>
        <div style="">문제 인지<div>
            <div>- 로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
        <div style="">선택지<div>
            <div>- 1. Access Token 만 사용<br>2. Access , Refresh Token 함께 사용</div> 
        <div style="">핵심 기술을 선택한 이유 및 근거<div>
            <div>- [2 번 선택]- Client에서 Server로 요청시 사용자 인증을 위해 전달하는 Access Token<br>이 중간에 탈취 되면, 탈취 한사람이 원래의 유저의 권한을 획득하여 서비스를 악용할 우려가 있어 이를 방지 하기 위해 Access Token의 만료 시간 짧게 두어 탈취 되었을 경우 악용가능한 시간을 줄였으며, 만료된 토큰을 갱신 하여 새로 발급하기 위해 Refresh Token을 함께 사용</div> 
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>

- FE Trouble Shooting
    https://github.com/TeamBungle/projectBungle_FE/blob/c59ca843603028c114949199179fcc88dac413db/src/App.js#L42-L74

### 😍 벙글 [서비스 링크 바로가기](https://bungle.life)

### 😆 프로젝트 Git address

- Back-end Github    https://github.com/TeamBungle/projectBungle_BE
- Front-end Github   https://github.com/TeamBungle/projectBungle_FE

### 😶 벙글 팀원 소개( L : 팀장, LV : 부팀장 )

| 역할 | 이름 | Git 주소 |
| --- | --- | --- |
| BE | 강현구님 | https://github.com/kootner |
| BE( LV ) | 김민수님 | https://github.com/minssu86 |
| BE | 김정훈님 | https://github.com/junghoon-kim96 |
| BE | 정현욱님 |  |
| FE | 최서우님 | https://github.com/zerovodka |
| FE | 한결님 | https://github.com/GHan19 |
| FE( L ) | 한지용님 | https://github.com/jigomgom |
| Designer | 양승연님 |  |
