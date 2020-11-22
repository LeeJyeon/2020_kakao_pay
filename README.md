# 2020_kakao_pay
2020년 카카오페이 경력채용

== DB 명세
1. master_pay
 deal_ymd [거래일자]
 token [토큰값]
 snd_id [뿌린 사용자 ID]
 room_id [뿌린 사용자 ROOM ID]
 regi_id [최초등록자] 
 regi_dt [최초등록일]
 modi_id [최종수정자]
 modi_dt [최종수정일]
 total_money [최초 뿌린금액]
 rcv_money [현재 받기 성공한 금액]
 people_num [뿌리기 명수]

2. trans_pay
 deal_ymd [거래일자]
 token [토큰값]
 snd_id [뿌린 사용자 ID]
 room_id [뿌린 사용자 ROOM ID]
 seq [받기 순서]
 regi_id [최초등록자] 
 regi_dt [최초등록일]
 modi_id [최종수정자]
 modi_dt [최종수정일]
 rcv_id [받은 사용자ID]
 rcv_money [받은 금액]
 rcv_yn [받기 여부]


== Controller

1. SprinkleMoneyController
 [에러]
  - 뿌리기 인원 < 0 일경우
  - 뿌리는 인원에 비해 금액이 부족할 경우

 [Logic]
  - 최소 10~30% 금액은 가져갈 수 있도록 금액 분배
  - 최초 뿌릴시 master table 단건 , trans table n건 입력
  - 최초 뿌릴시 금액산정 완료 

2. RecieveMoneyController
 [에러]
 - 존재하지 않는 토큰 값
 - 같은 방의 인원이 아닐 경우
 - 뿌린 사람일 경우
 - 최초 생성 10분이 지난 경우
 - 이미 받은 경우

 [Logic]
  - master table 의 rcv_money 값 upd (+= money)
  - trans_pay table 의 rcv_yn 값 upd ( "Y" )

3. QueryOwnerController
 [에러]
  - 최근 7일간 내용만 조회가능
  - 본인의 뿌리기만 조회 가능

 [Logic]
  - 뿌린 시각, 뿌린 금액, 받기 완료된 금액 ( master table )
  - 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트) ( trans table )

== 핵심 전략

 1. 각각의 table 조회만으로 운영업무가 가능하게끔 설계.
 2. DB Exception 처리
 3. 뿌리기 기능이지만, 최소 10 ~ 30% 는 수령 가능토록 설계.
 4. 뿌리기 시작시 금액을 산정해놓기 때문에, 받기 기능 중 금액관련 이슈가 차단.

== 한계
 1. 7일내 token 값 제한
  - token 을 대소문자 조합/ 3글자로 설정해 140,608 건 운용가능.
  - 랜덤 생성 후 사용가능한 토큰값인지 DB Search.
 2. 해결방안
  - token 자릿수 증가 및 7일 운영방안 축소
