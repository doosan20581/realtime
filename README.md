# Vibe Realtime Stock Service

## 📌 프로젝트 소개
WebSocket 기반 실시간 주식 조회 및 거래 토이 프로젝트입니다.
AI 툴을 활용한 바이브 코딩 방식으로 구조 설계와 개발을 진행했습니다.

## 🛠 기술 스택
- Java 17
- Spring Boot
- Spring Security + JWT
- WebSocket
- JPA (Hibernate)
- ORACLE
- Redis (선택)

## 🧱 프로젝트 구조
- auth : 인증 / 토큰 관리
- user : 사용자 도메인
- stock : 주식 조회 및 실시간 가격 처리
- subscription : 종목 구독
- trade : 매수 / 매도
- notification : 실시간 알림

## 🚀 개발 방향
- 초기에는 구조와 책임 분리에 집중
- 반복적인 작업은 스크립트 + AI로 자동화
- 실시간 처리와 트랜잭션 설계 경험을 목표로 함
