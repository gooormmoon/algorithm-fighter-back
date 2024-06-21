#
# 해당 설정으론 401 응답 오류 발생
#
# 빌드 이미지로 OpenJDK 11 & Gradle을 지정
FROM gradle:jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build -x test --no-daemon

# Base 이미지로 OpenJDK 17 Alpine 설정
FROM openjdk:23-ea-17-oracle

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 로컬에서 직접 가져오도록 설정
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/algofi.jar

# 8080 포트를 외부에 노출
EXPOSE 8080

# 컨테이너 실행 시 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "algofi.jar"]