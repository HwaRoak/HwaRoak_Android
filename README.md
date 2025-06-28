# 화록 Anroid README


## 라이브러리 및 기술 스택

### 1. 공통
- **버튼**: vector 이미지 기반 `ImageButton`  
- **하단 네비게이션**: `BottomNavigationView` + Fragment Transaction

### 2. Splash Activity
- **지연 처리**: `Handler` & `Looper`

### 3. 로그인
- **소셜 로그인**: Kakao SDK  
- **약관/정책 화면**: `ScrollView` (필요 시 `NestedScrollView`), `CheckBox`

### 4. 메인 화면
- **리워드 이미지 보관함**: `RecyclerView` + `GridLayoutManager`  
- **알림 창**: `RecyclerView`

### 5. 일기 작성
- **감정 & 일기 슬라이드 바**: `TransitionManager` or `MotionLayout`  
- **캐릭터 팝업 애니메이션**: `View.post { … }` + Scale Transform  
- **불꽃 파티클**: Leonids particle system (`com.github.plattysoft.leonids:Leonids:1.3.2`)

### 6. 일기 히스토리
- **달력**: `MaterialCalendarView` + `DayViewDecorator`  
- **삭제 팝업**: Custom Dialog

### 7. 친구 목록
- **친구 리스트**: `RecyclerView`  
- **검색 팝업**: Custom Dialog  
- **화록 페이지**: 메인 화면 레이아웃 재사용

### 8. 마이페이지
- **감정 분석**: `PieChart`  
- **프로필 이미지**: `CircleImageView`  
- **알림 설정**: `SwitchMaterial`

---

## 커밋 타입 태그
- `[UI]` : XML 및 Drawable 관련 작업  
- `[Logic]` : 기능 구현  
- `[Add]` : 기능 추가  
- `[Delete]` : 파일/코드 삭제  
- `[Fix]` : 버그 수정  
- `[Rename]` : 이름 변경(변수, 파일, 클래스 등)  
- `[Merge]` : Merge 작업  
- `[Back_Connect]` : 백엔드 API/데이터 연결

---

## Branch 전략
모든 작업은 `main` 브랜치에서 파생된 기능별 하위 브랜치에서 진행합니다.

```text
main
 ├─ [팀원1/#1/메인화면]
 ├─ [팀원2/#2/로그인화면]
 └─ [팀원3/#3/일기작성화면]
```

- **브랜치명**: `[팀원명/#이슈번호/구현기능]`

---

## Issue 컨벤션
- **제목 형식**: `[팀원명/타입] 구현 내용`  
- **예시**: `[어헛차/UI] 메인 화면 UI 구현`

---

## PR 컨벤션
- **제목 형식**: `[팀원명/#이슈번호/구현기능] - 작업 내용`  
- **예시**: `[어헛차/#1/메인화면] - 아이템 RecyclerView 연결`

---

## Commit 컨벤션
- **메시지 형식**: `[팀원명/#이슈번호/구현기능] - 작업 내용`  
- **예시**: `[어헛차/#1/메인화면] - RecyclerView 어댑터 구현`

---

## Code 컨벤션

### 1. 위젯 ID 네이밍
```
[Activity/Fragment]_[기능]_[위젯이름]
예) MainActivity 뒤로가기 버튼 → main_back_btn
```

### 2. UI 주석
- XML 내 주요 레이아웃/위젯에 한 줄 설명
```xml
<!-- 상단 앱바 -->

 xml 작성...

<!-- 중앙 fragment -->

 xml 작성...

<!-- 하단 bottomNavigationView-->

 xml 작성...

```

### 3. 패키지 구조
```
com.hwarok
├─ data        # Data classes, 모델
├─ api         # 네트워크/API
├─ ui          # Activity, Fragment, Adapter
└─ util        # 유틸리티
```

### 4. Activity & Fragment 네이밍
- **CamelCase** 사용  
  - 예) `WriteVocActivity`, `DiaryHistoryFragment`

---

## 개발 환경

- **Android Studio 버전**  
  `Android Studio Narwhal | 2025.1.1`

- **SDK 설정**  
  - `targetSdkVersion`: 24  
  - `minSdkVersion`: 35

- **테스트 환경**  
  다양한 기기에서의 원활한 테스트를 위해 **Emulator** 및 **실제 디바이스**를 함께 사용하여 검증을 진행합니다.

  ---
