
![배너용](https://user-images.githubusercontent.com/107230384/182052615-f4743530-6596-4b4f-9b5e-6100f021eebb.jpg)


**싱글벙글, 사용자 현재 위치 기반으로 빠르게 주변 모임을 확인할 수 있습니다.**

### 🤔 프로젝트 시장 조사 및 Feasibility Test

[Feasibility test](https://www.notion.so/Feasibility-test-b8f7d2dccd354a0db0577e245a12f4a4) 

### 🙂 아키텍쳐

![최종 아키텍처](https://user-images.githubusercontent.com/107230384/182052947-7c29f084-224a-492b-9a71-0c0f09c65a9e.jpg)

### 🤩 Data base 설계
![image](https://user-images.githubusercontent.com/87007109/182985563-f223fd87-d7ac-4688-8102-cf0a5d8bafac.png)

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
        <ul>
            <li>문제 인지
                <div>게시글 조회시 유저의 위치로부터 일정 거리내에 있는 게시물 만을 DB로부터 불러와 거리순에 맞춰 응답하는 로직 필요.<br>
		초기 기능 구현시 기본적인 Spring Data JPA만을 사용한 결과 N+1 문제 및 DB로부터 불러온 data를 JAVA 코드로 재 정렬해야하는 문제 발생.<br></div>
            </li>
            <li>선택지
                <div>1. MBRContains 사용<br> 
		2. ST_DISTANCE_SPHERE 사용</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [2번 선택]<br>
                    - MBRContains 적용시 DB에서 해당 데이터 추출후 거리순 정렬 작업이 추가로 필요하여, Query조회시 거리순 정렬 및 추출을 한번에 할 수 있는 ST_DISTANCE_SPHERE를 사용하는 것이 더 좋다고 판단하였지만, 부하 테스트 결과 MBRContains 사용시 성능이 더 좋은 결과가 나왔습니다. 향후 테스트를 추가 진행하여 같은 결과 도출 시 현재 적용중인 ST_DISTANCE_SPHERE를 MBRContains로 변경 예정입니다.
                </div> 
            </li>
        </ul>
        <div markedown="1">    https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/PostService.java#L118-L141
        </div>
    </details>
    
    <details>
        <summary>회원 가입시 사용자 인증</summary>
        <ul>
            <li>문제 인지
                <div> 회원 가입시 email 인증 메일 발송 로직에서 2~3초간의 대기시간이 걸려 가입 버튼을 클릭한 사용자가 대기해야하는 문제 발생 </div>
            </li>
            <li>선택지
                <div>1. 비동기 처리<br>
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                [1선택]<br>
                - email 전송 method에 @Async Annotation을 이용해 비동기 처리 하여 유저의 대기 시간을 줄였음
                </div> 
            </li>
        </ul>
        <div markedown="1"> https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/EmailConfirmTokenService.java#L24-L51
        </div>
    </details>
    
    <details>
        <summary>서비스 이용시 탈취 될 수 있는 유저 정보 보안</summary>
        <ul>
            <li>문제 인지
                <div>유저인증 방식으로 JWT를 이용한 Access Token 발행 방식을 사용하였으며, 이때 Token이 타인에게 탈취 되었을때를 대비가 필요 하였음</div>
            </li>
            <li>선택지
                <div>1. Access Token 만 사용<br>
		2. Access , Refresh Token 함께 사용</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                [2 번 선택]<br>
                - Access Token의 만료 시간을 짧게 두어 탈취 되었을 경우 악용가능한 시간을 줄였으며, Access Token발행 시 만료 기간이 긴 Refresh Token을 함께 발행 하여 Access Token 만료시
	      재로그인으로 Access Token을 갱신하는 것이 아닌 Refresh Token 인증을 통해 Access Token을 갱신하였음. 이때, Refresh Token은 in memory cache인 redis에 저장하여 잦은 조회로 인해 발생가능한 DB부담을 줄였음
                </div> 
            </li>
        </ul>
        <div markedown="1"> https://github.com/TeamBungle/projectBungle_BE/blob/ba1372e9c4d25307f66320c42b1f60a41544d8bd/src/main/java/com/sparta/meeting_platform/service/UserService.java#L188-L222
        </div>
    </details>
    
    <details>
        <summary>Websocket을 사용하여 실시간 알림</summary>
        <ul>
            <li>문제 인지
                <div>기존 기능 구현시 유저가 채팅방에 입장할때마다 Websocket을 Connect하고 나갈때마다 Disconnect 하였으나, 처음 로그인했을때 Connect 후, 로그아웃 하거나 웹페이지를 빠져나갈때 Disconnect가 되어야 안읽은 메세지에 대한 실시간 알림이 구현 된다고 판단하여 로직을 바꾸는 시도를 하였음</div>
            </li>
            <li>현재 상태
                <div>1. 채팅방에 입장할때 Connect 후 그방에대한 Subscribe 진행<br>2. 채팅방에서 나갈때 Disconnect</div> 
            </li>
            <li>목표
               <div>1. 로그인할때 WebSocket Connect<br>2. 채팅방에 입장할때 그방에대한 Subscribe 진행<br>3. 채팅방에서 나갈때 그방에대한 Unsubscribe 진행<br>4. 로그아웃할때 Websocket Disconnect<br>Websocket을 하나 열고 그안에서 여러개의 sub,unsub을 진행하려고 하는 과정에서 에러가 많이 발생했고, 시간관계상 프로젝트 마무리까지 얼마 남지않아 방식을 바꾸기로 결정하고 구글링 및 멘토님께 자문을 구한 결과 http를 이용해서 구현 하기로 결정하였다.</div>  
            </li>
	    <li>실제 반영
               <div>front에서 5초마다 알림을 조회하는 요청을 보내고 그에대한 응답으로 사용자가 채팅방에서 나간 시간을 저장하여, 그시간 이후로 그방에서 보내진 메세지들을 응답으로 보내주는 방식으로 구현</div>  
            </li>
        </ul>
    </details>
    
    <details>
        <summary>Spring Security를 적용시 발생한 Websocket 연결 문제</summary>
        <ul>
            <li>문제 인지
                <div>Spring Security를 적용하지 않은 상태에서 클라이언트와 서버간의 연결에 문제가 없이 정상적으로 작동 하였으나, Security를 적용하고 연결을 시도하니 401 에러가 발생했다.</div>
            </li>
	
- 문제 해결 과정<br>
1-1 WebSocket은 Custom Header 적용이 안되는것으로 확인됬다.<br>
 &nbsp;&nbsp;- 관련자료 : https://velog.io/@tlatldms/Socket-%EC%9D%B8%EC%A6%9D-with-API-Gateway-Refresh-JWT<br>
1-2 Hand Shake하는 과정을 Security에서  Pass를 걸어 시도를 하였다<br>
&nbsp;&nbsp;- 결과는 실패 , 이때까지는 이유를 알 수 가 없었다<br>
1-3 Stomp Handler를 만들어서 intersepter를 적용하여서 Token검사를 시도하였다.<br>
&nbsp;&nbsp;- 실패 , Token 자체를 받아올 수 가 없었다.
![](https://velog.velcdn.com/images/junghunuk456/post/bd2cb4f4-c822-4f9f-9c9f-82855d298b85/image.png)
1-4 첫 HandShake 과정부터 하나하나 log를 찍어서 확인 해 본 결과 Sockjs를 사용시 우리가 정해놓은 EndPoint 뒤에 여러 path을 붙여서 접속을 시도하는것을 확인했다.
&nbsp;&nbsp;- 우리가 정해놓은 EndPoint가 (“ws/chat”)이었는데, “ws/chat/934/czvkhxvy/websocket << 이런식으로 뒤에 path 를 붙여서 요청이 들어왔다.
![](https://velog.velcdn.com/images/junghunuk456/post/5439c533-9e22-40c9-b89c-4886f2972395/image.png)
1-5 우리가 적용했던 security에서 api paht를 시키는 방법이 다음과 같았다.
![](https://velog.velcdn.com/images/junghunuk456/post/424d3e82-4cf1-40c5-aef5-72872b21c3de/image.png)
하지만 이 상태에서는 ws/chat/** 이런식으로 뒤에 와일드카드를 붙여서 전부다 API path를 허용하는것이 불가능 하였기 때문에 ws/chat을 path 시켜도, 뒤에 붙는 path들이 전부 다르기 때문에 적용이 안되었었다.<br>
1-6 Security 구조 변경
![](https://velog.velcdn.com/images/junghunuk456/post/57fb71d1-a381-49b5-8770-b9a4bddbf40f/image.png)
위와 같이, 구조를 변경하고 와일드카드를 사용하여 path시키니 정상적으로 작동하였다!
---
2 .refresh Token 적용 후 , Access Token 의 만료시간이 지나 refresh Token을 사용하여 AccessToken을 갱신 하는 과정에서 갱신을 시도할때 보내는 첫번째 메세지가 채팅창에 입력이되지 않는 현상이 발생
- Token을 사용하여 유저의 유효성을 검증하는것은 처음 WebSocket에 Connect 할때에만  하는것으로도 충분하다고 생각하여 Connect 할때(채팅방에 입장할때)에만 Token을 받아서  유효성 검사를 진행하고, 메세지를 주고 받을때는 기존에 사용하던 Token이 아닌, 그저 User의 PK값을 받아서 user정보를 찾아 return시켜 주는 방법으로 변경하였다.
---
3 .Reids 에 Message들을 저장할때 Serialize 하는 부분에서 에러발생,
- 메세지를 저장할때 메세지를 보낸 시간을 저장하기위해서 LocalDateTime을 사용하였는데, 자료를 조사해본결과 Java8 버전에서는 LocalDateTime을 직렬화,역직렬화 하지 못한다고 한다
  - redis 에 저장하기 전, LocaldateTime 을 String으로 변환하여 저장하였다
---
4 .유저가 채팅 방에 정상 진입 되지 않는 문제
기존에 서버쪽에서 postId(Pk)를 roomId로 사용하였는데 TopicChannel Class에서 param을 String으로 받기 때문에
Long type 인 postId를 String으로 형 변환 하여 사용하는 중이였다.
이때 채팅방에 유저가 진입을 시도할경우 방입장이 정상적으로 진행되지 않음
- 확인 결과 Client에서 보내주는 값이 Long Type으로 와서 채팅방입장이 정상적으로 진행되지 않음
  - room Id를 형변환 하지 않고 Long 형태로 사용 할 수 있는지 방법 확인
  - 확인결과 TopicChannel을 구현된 그대로 사용하는 이상 불가능한 점 확인
  - Client에서 값을 String으로 변환하여 보내줘서 문제 해결        
		</ul>
    </details><br>


<a href="https://github.com/TeamBungle/projectBungle_FE"> FE Trouble Shooting 링크로 가기</a>


### 😍 벙글 [서비스 링크 바로가기](https://bungle.life)

### 😆 프로젝트 Git address

- Back-end Github    https://github.com/TeamBungle/projectBungle_BE
- Front-end Github   https://github.com/TeamBungle/projectBungle_FE

### 😶 벙글 팀원 소개( L : 팀장, LV : 부팀장 )

| 역할 | 이름 | Git 주소 | 메일 주소 |
| --- | --- | --- | --- |
| BE | 강현구님 | https://github.com/kootner | refromto@naver.com |
| BE( LV ) | 김민수님 | https://github.com/minssu86 | manager.kim86@gmail.com |
| BE | 김정훈님 | https://github.com/junghoon-kim96 | 0527wj@naver.com |
| BE | 정현욱님 | https://github.com/Jeonghyeonuk | junghunwook456@naver.com |
| FE | 최서우님 | https://github.com/zerovodka | 264826@naver.com |
| FE | 한결님 | https://github.com/GHan19 | gksrufdla@naver.com |
| FE( L ) | 한지용님 | https://github.com/jigomgom | hjy583@naver.com |
| Designer | 양승연님 | didtmddus123@gmail.com |

![image](https://user-images.githubusercontent.com/107230384/182984092-aa5e7b4e-cf7b-4e39-a90d-6ec822e562eb.png)
