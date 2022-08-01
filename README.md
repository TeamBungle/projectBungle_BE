
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
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. JPA Data 사용<br> 2. Native Query 사용</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [2번 선택]<br>- Kakao Geocoding을 통해 DB에 저장된 위도, 경도 정보를 조회시 Native Query를 사용하여 Query 조회 성능 향상
                </div> 
            </li>
        </ul>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
    <details>
        <summary>모임전 필요한 정보 공유를 위한 채팅 서비스를 제공</summary>
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. 실시간 채팅 라이브러리( ex> PeerJS )<br>2. Stomp, SockJS, Redis pub/sub</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [2번 선택]<br>- Websocket에 대한 전반적인 이해도가 부족한 상태에서, 라이브러리를 통해 구현하려고 하다보니 개발이 잘 진행 되지 않음<br>- 채팅 서버가 여러개로 나뉠경우, Spring 에서 제공하는 내장 broker로는 서로 다른 서버에 요청을 보낸 사용자끼리 채팅이 불가 하여 Reids pub/sub 방식을 사용
                </div> 
            </li>
        </ul>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
    <details>
        <summary>채팅 메세지 저장 및 채팅방 입장시 이전 메세지 출력 방식</summary>
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. Mysql 사용<br>2. Redis Cache사용</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [1, 2번 선택]<br>- 매번 채팅방에 입장 할 때마다 DB에서 조회해오는 방식을 사용하면 성능이 떨어질 것으로 예상하여, 메세지를 저장할때는 Reids와 DB에 같이 저장하고, 메세지를 조회해올경우에는 Redis Cache를 사용하여 메세지를 불러오며, Reids에 저장되어있는 데이터가 손실 되었을 경우, DB에서 조회하도록 로직을 구성.
                </div> 
            </li>
        </ul>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
    <details>
        <summary>읽지 않은 메세지에 대한 알림기능 구현</summary>
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. Websocket을 사용하여 실시간 알림<br>2. SSE를 사용하여 실시간 알림<br>3. http를 사용하여 알림</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [3번 선택]<br>- 프로젝트 마무리 시간을 고려하여, 시간이 충분히 여유롭지 않아 제일 익숙한 방식인 http를 이용하여 알림을 구현하기로 함<br>- front에서 5초마다 알림을 조회하는 요청을 보내고 그에대한 응답으로 사용자가 채팅방에서 나간 시간을 저장하여, 그시간 이후로 그방에서 보내진 메세지들을 return시켜줌.
                </div> 
            </li>
        </ul>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
    
    <details>
        <summary>회원 가입시 사용자 인증</summary>
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. Email 인증<br>2. OAuth 사용<br>3. only Id/Password</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [1, 2번 선택]- 일반 회원 가입의 경우 가입에 사용한 email로 인증 토큰을 전달후 유저가 해당 토큰을 다시 서버로 전달 하면 서비스를 사용할 수 있도록 권한 변경<br>- Oauth를 통해 유저들에게 친숙한 3개의 대형회사에 접근 권한 인증을 위임하여 신규 서비스의 단점인 신뢰성을 보강생
                </div> 
            </li>
        </ul>
        <div markedown="1">
            https://github.com/TeamBungle/projectBungle_FE/blob/00460f7436e216b8d65729aae642864c7185c9ab/src/App.js#L42-L74
        </div>
    </details>
     
    <details>
        <summary>서비스 이용시 탈취 될 수 있는 유저 정보 보안</summary>
        <ul>
            <li>문제 인지
                <div>로그인 하지 않는 사용자가 URL을 직접 입력해서 다른 페이지로 접근할 수 있는 상황이 발생</div>
            </li>
            <li>선택지
                <div>1. Access Token 만 사용<br>2. Access , Refresh Token 함께 사용</div> 
            </li>
            <li>핵심 기술을 선택한 이유 및 근거
                <div>
                    [2 번 선택]<br>- Client에서 Server로 요청시 사용자 인증을 위해 전달하는 Access Token<br>이 중간에 탈취 되면, 탈취 한사람이 원래의 유저의 권한을 획득하여 서비스를 악용할 우려가 있어 이를 방지 하기 위해 Access Token의 만료 시간 짧게 두어 탈취 되었을 경우 악용가능한 시간을 줄였으며, 만료된 토큰을 갱신 하여 새로 발급하기 위해 Refresh Token을 함께 사용
                </div> 
            </li>
        </ul>
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
