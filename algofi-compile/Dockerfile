# 빌드 이미지로 OpenJDK 11 & Gradle을 지정
FROM gradle:jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build -x test --no-daemon


FROM openjdk:23-ea-17-jdk-bullseye

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/algofi.jar

# 필요한 패키지 설치 및 정리
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc g++ make python3 python3-pip nodejs npm && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 9000 포트를 외부에 노출
EXPOSE 9000

# 컨테이너 실행 시 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "algofi.jar"]