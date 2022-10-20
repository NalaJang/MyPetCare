# My Pet Care - 반려동물 돌봄 서비스 어플
## My Pet Care 서비스를 통해 반려동물🐶과 반려인🙂의 걱정과 불안을 해소하세요!</br>
✔️ 집에 혼자 있는 반려동물이 걱정될 때</br>
✔️ 장시간 집을 비워두어야 할 때</br>
✔️ 특별한 돌봄이 필요할 때


## Table of contents
* [General info](#general-info)
* [Tech Stack](#tech-stack)
* [Menu Tree](#menu-tree)
* [Main Functions](#main-functions)
* [Main Function Details](#main-function-details)
* [References](#references)
* [Libraries](#libraries)


## General info
* DB 활용 경험을 확대하기 위해 Firebase 사용
* kotlin 언어 사용을 목적으로 제작한 어플
* 개발 기간: 2022.8.22 ~ 2022.9.25


## Tech Stack
* OS: Mac
* 언어: Kotlin
* Android 버전: Bumblebee
* 최소 SDK 버전: 27
* 타겟 SDK 버전: 32
* Database: Firebase

## Menu Tree
<img width="469" alt="image" src="https://user-images.githubusercontent.com/73895803/193718379-2715d82c-0ca3-4359-9d0a-b2cd0aada5e7.png">


## Main Functions
* 회원 가입
* 로그인
* 일정
* 매니저 정보
* 채팅
* 설정

## Main Function Details

<details>
  <summary>회원 가입</summary>
    
  * 사용자 정보와 반려 동물 정보 입력
  * 중복 아이디 
  * 가입 완료 시 Firebase에 계정 생성과 기본 프로필 저장

  <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193750454-955d8ac4-0172-4084-9588-ce89057b08d8.png">
  <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718550-56aced60-0576-4360-b17c-1838d6112f55.png">

 </details>
  
<details>
  <summary>로그인</summary>
    
  * Firebase의 Authentication을 통해 사용자가 입력한 값과 비교해서 값이 일치할 때 로그인 허용
  * 로그인 성공 시 일정 화면으로 이동
  * Flow chart
  <p align=center><img width="290" alt="image" src="https://user-images.githubusercontent.com/73895803/193762038-a5f4c858-3bad-4842-b114-2198c3ecdb3d.png"></p>
  
  * Images
  
  <img width="274" alt="image" src="https://user-images.githubusercontent.com/73895803/193718223-e62aea2a-939b-4820-9d9a-318527cfe79c.png">
  <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193750741-c40cb771-f6f1-4a8b-940a-8b88e503d467.png">
  <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193750762-f3bc9424-290d-471c-92ba-7f71a5f99ae7.png">

</details>

  
<details>
  <summary>일정</summary>
    
  <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718725-67526f27-dbb0-4fb6-8559-dbdbd150a1d4.png">

  * 일정 신청
    * 날짜 선택 -> 신청 버튼 클릭 -> 신청하고자 하는 서비스 유형과 돌봄이 필요한 시간, 메모 내용 등을 입력 -> 완료 버튼 클릭

    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193750993-f50cda02-a951-4933-94a2-439e6b8f6bae.png">
    <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718792-3fb4675e-28ee-4a1f-8155-c7adda1a10da.png">
    <img width="274" alt="image" src="https://user-images.githubusercontent.com/73895803/193751147-4bf69f6f-8e33-4b06-b78c-48858bf54a82.png">
  
  * 일정 확인
    * 일정이 있는 날짜 선택 -> 일정 클릭 -> 신청된 서비스 유형과 시간, 메모 내용, 담당 매니저를 확인할 수 있음
    <img width="271" alt="image" src="https://user-images.githubusercontent.com/73895803/193749534-c422533c-0fbe-4312-86cd-9e6f7f7e01a5.png">
    <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193719504-865fb10e-b12c-4831-8e3b-3457e8cfa527.png">
    
</details>
    
    
<details>
  <summary>매니저 정보</summary>
    
  <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193749848-39c4cc21-7c59-487c-ad23-ecd98a618aa5.png">
  <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193719841-644db40f-988a-4277-b9a2-5c99f87ac2d3.png">
  
  * 리뷰 작성
  
  <img width="269" alt="image" src="https://user-images.githubusercontent.com/73895803/193751974-bbb504b5-1fc7-4e36-8518-02b271986172.png">
  <img width="269" alt="image" src="https://user-images.githubusercontent.com/73895803/193752062-fa230640-4ae4-40a7-8d18-7e6135f29b80.png">

</details>

  
<details>
  <summary>채팅</summary>
    
 * 매니저와 첫 채팅 시작 하기
    * 일정이 있는 날짜 선택 -> 일정 클릭 -> 일정 정보 화면 -> 매니저 이름 클릭 ->
    * 매니저 정보 화면으로 이동 -> 화면 우측 상단의 채팅 버튼 클릭 -> 채팅 화면으로 이동
  
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193750213-2a3a7656-b2d5-4380-adbc-5446ec3b1d93.png">
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720521-2a1b7d83-2c7f-4caa-85cb-29f5fa0c1587.png">

 * 채팅 목록
    * 화면 하단의 채팅 아이콘 클릭 -> 채팅 목록 중 원하는 방을 선택 -> 채팅 화면으로 이동
  
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193748816-12f48b3f-40ca-4607-873f-3be335c419e1.png">
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193748942-f2dfb597-6668-483b-a562-34d5e3a22f9b.png">
    <img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193749041-266cca84-8edc-4cff-be1f-947fe4856c64.png">

</details>


<details>
  <summary>설정</summary>
    
  <img width="271" alt="image" src="https://user-images.githubusercontent.com/73895803/193720884-d1411ec8-dde5-41ad-9627-e709addc612d.png">

* 나의 프로필
    * 정보 변경
      * 수정을 원하는 정보를 재 입력 -> 화면 우측 상단의 완료 버튼 클릭
    * 프로필 사진 변경
      * 프로필 이미지 클릭 -> 갤러리로 이동 -> 변경하고자 하는 이미지 선택 -> 프로필 화면으로 이동 -> 화면 우측 상단의 완료 버튼 클릭
   
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720639-a2ca23ce-836d-4738-947d-5a706797d99f.png">
    <img width="274" alt="image" src="https://user-images.githubusercontent.com/73895803/193720763-de74430c-5a99-4c1d-ac33-817429449d12.png">


  * 작성한 리뷰
    * 작성한 리뷰 버튼 클릭 -> 리뷰 삭제를 원한다면 해당 리뷰의 삭제 버튼을 클릭
    <img width="269" alt="image" src="https://user-images.githubusercontent.com/73895803/193752179-c5f9f0b3-6dfd-497b-8455-59834ab7648d.png">

  * 로그아웃
    * 로그아웃 버튼 클릭 -> 로그인 화면으로 이동
</details>

   

## References
* Pefam
* 도그메이트

## Libraries

    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.5.0'
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.5.1'
    implementation 'androidx.navigation:navigation-ui-ktx:2.5.1'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.google.firebase:firebase-database-ktx:20.0.5'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'

    // Import the Firebase BoM
    implementation platform('com.google.firebase:firebase-bom:30.3.2')
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation 'com.google.firebase:firebase-analytics-ktx'
    // firebase email login
    implementation 'com.google.firebase:firebase-auth-ktx'
    // firestore 사용하기
    implementation 'com.google.firebase:firebase-firestore-ktx'
    // database 접근
    implementation 'com.google.firebase:firebase-database:20.0.5'
    // firestorage 사용
    implementation 'com.google.firebase:firebase-storage-ktx'

    // circle imageView 라이브러리
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    // Glide 라이브러리
    implementation 'com.github.bumptech.glide:glide:3.7.0'


