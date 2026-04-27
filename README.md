<div align="center">

# 🐾 Hamly — 소동물 스마트 홈 케어 시스템

### *2025 한세대학교 졸업 프로젝트*

> 라즈베리파이 + MQTT 통신 기반 **원격 펫 케어 Android 앱**

<br/>

[![Android](https://img.shields.io/badge/Android-Java-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)](https://github.com/dlsdud01/2025_Graduate_Project)
[![MQTT](https://img.shields.io/badge/MQTT-Paho-660066?style=for-the-badge&logo=mqtt&logoColor=white)](https://github.com/dlsdud01/2025_Graduate_Project)
[![IoT](https://img.shields.io/badge/IoT-Raspberry_Pi-C51A4A?style=for-the-badge&logo=raspberrypi&logoColor=white)](https://github.com/dlsdud01/2025_Graduate_Project)

</div>

---

## 👥 팀 구성

| 역할 | 이름 |
|------|------|
| 조장 | [최인영](https://github.com/dlsdud01) |
| 조원 | [문정필](https://github.com/moonjeongpil) |
| 조원 | [지승용](https://github.com/seungyongjee) |

---

## 📌 프로젝트 소개

외출 시 반려동물의 환경을 원격으로 모니터링하고 제어할 수 있는 스마트 홈 케어 시스템입니다.

라즈베리파이에 연결된 센서·장치와 Android 앱이 **MQTT 프로토콜**로 실시간 통신하여,
앱에서 온습도 확인·조명 제어·현관문 제어·카메라 스트림 확인이 가능합니다.

---

## 👩‍💻 담당 역할 (최인영)

| 영역 | 내용 |
|------|------|
| **Android 앱 화면** | Activity · Fragment · XML 기반 화면 구성 (온습도, 조명, 현관문, 카메라) |
| **MQTT 통신** | Paho 라이브러리 적용, 센서 데이터 수신 후 UI 반영 |
| **라이브러리 호환 문제 해결** | Android 12 PendingIntent 오류 직접 Fork 수정 → 로컬 AAR 적용 |
| **카메라 스트림** | ipcam-view · mjpeg-view · WebView 비교 검토 및 적용 |

---

## ✨ 주요 기능

| 기능 | 내용 |
|------|------|
| 🌡️ **온습도 모니터링** | DHT11 센서 데이터를 MQTT로 수신, 앱 화면에 실시간 표시 |
| 💡 **조명 제어** | 앱에서 ON/OFF 명령을 MQTT로 전송하여 라즈베리파이 GPIO 제어 |
| 🚪 **현관문 제어** | Door Open/Close 명령 전송 |
| 📷 **카메라 스트림** | MJPEG 기반 실시간 카메라 영상 수신 및 화면 표시 |

---

## 🛠️ 기술 스택

![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=androidstudio&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=openjdk&logoColor=white)
![MQTT](https://img.shields.io/badge/MQTT_Paho-660066?style=flat-square&logo=mqtt&logoColor=white)
![Raspberry Pi](https://img.shields.io/badge/Raspberry_Pi-C51A4A?style=flat-square&logo=raspberrypi&logoColor=white)

---

## 🔧 트러블슈팅

### 1. Android 12 이상 MQTT 라이브러리 호환 오류 → Fork 수정

**문제** : Paho Android MQTT Client 사용 시 Android 12 이상 환경에서 앱 실행 불가
```
IllegalArgumentException: requires FLAG_IMMUTABLE or FLAG_MUTABLE
on PendingIntent (AlarmPingSender)
```

**원인** : Android 12부터 `PendingIntent` 생성 시 플래그 명시가 필수인데, Paho 라이브러리 내부 `AlarmPingSender`가 이를 처리하지 않음

**해결** :
1. Paho Android 라이브러리 직접 Fork
2. `AlarmPingSender.java` 내 `PendingIntent.getService()` 호출부에 `FLAG_IMMUTABLE` 플래그 추가
3. JitPack 배포 시도 → Gradle 버전 호환 문제로 빌드 실패
4. 수정된 소스를 **로컬 AAR 파일로 빌드** 후 프로젝트 `libs/`에 직접 포함하여 해결

```gradle
implementation fileTree(dir: 'libs', include: ['*.aar'])
```

---

### 2. MJPEG 카메라 스트림 수신 방식 비교·선택

| 방식 | 장점 | 단점 |
|------|------|------|
| `ipcam-view` | 간단한 사용법 | 유지보수 중단, 일부 기기 호환 문제 |
| `mjpeg-view` | MJPEG 전용 최적화 | 설정 옵션 제한적 |
| `WebView` | 유연성 높음, URL 직접 로드 가능 | 네이티브 대비 퍼포먼스 낮음 |

하드웨어 환경 제약으로 실제 스트림 연결까지 완전히 검증하지 못했고,
테스트 환경에서 WebView 방식을 기반으로 전체 흐름을 구현했습니다.

---

## 📁 프로젝트 구조

```
2025_Graduate_Project/
├── app/
│   └── src/main/
│       ├── java/
│       │   ├── MainActivity.java       # 메인 화면
│       │   ├── CamActivity.java        # 카메라 스트림 화면
│       │   ├── MqttHelper.java         # MQTT 연결 관리
│       │   └── fragment/               # 제어 화면 Fragment
│       └── res/layout/                 # XML 화면 레이아웃
└── libs/
    └── paho-android-mqtt.aar           # Fork 수정 후 로컬 빌드 AAR
```

---

## 📋 프로젝트 문서

| 문서 | 링크 |
|------|------|
| 📄 프로젝트 기획서 | [기획서 및 세부사항](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B8%B0%ED%9A%8D%EC%84%9C) |
| 🔧 하드웨어 구성 | [구성품 목록](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%84%B1%ED%92%88) |
| 📋 기능명세서 | [기능명세서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EA%B8%B0%EB%8A%A5-%EB%AA%85%EC%84%B8%EC%84%9C) |
| 📊 WBS | [Google Sheets](https://docs.google.com/spreadsheets/d/1nqFmSVeI2_Wef7J-9-uzlLRfAswlhbqIIPgTQqo2GwM/edit?gid=1360943592#gid=1360943592) |
| 📑 중간 발표 PPT | [Canva 보기](https://www.canva.com/design/DAGlVeEDv5A/eMKr0lO-yhJzyS4F76yy5g/edit) |
| 🎓 최종 발표 PPT | [햄리.pdf](https://github.com/user-attachments/files/24489099/default.pdf) |

---

## 📅 진행 기록

| 날짜 | 내용 |
|------|------|
| 2025-03-26 | [프로젝트 하드웨어 구성](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%84%B1%ED%92%88) |
| 2025-04-09 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9004%E2%80%9009)) |
| 2025-04-16 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9004%E2%80%9016)) |
| 2025-04-23 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9004%E2%80%9023)) |
| 2025-05-14 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9005%E2%80%9014)) |
| 2025-05-21 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9005%E2%80%9021)) |
| 2025-06-04 | [진행 상황 보고서](https://github.com/dlsdud01/2025_Graduate_Project/wiki/%EC%A7%84%ED%96%89-%EC%83%81%ED%99%A9-%EB%B3%B4%EA%B3%A0%EC%84%9C-(2025%E2%80%9006%E2%80%9004)) |

---

<div align="center">

[![Portfolio](https://img.shields.io/badge/포트폴리오-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/dlsdud01/portfolio)

</div>
