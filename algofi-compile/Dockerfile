# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:7.6.1-jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 라이브러리 설치에 필요한 파일만 복사
COPY build.gradle settings.gradle ./

# gradle dependencies를 실행하여 의존성 설치
RUN gradle dependencies --no-daemon

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle clean build --no-daemon

# 런타임 이미지로 Amazon Corretto JDK 17 지정
FROM amazoncorretto:17

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/algofi.jar

# 추가 패키지 설치
RUN yum update -y && \
    yum install -y gcc gcc-c++ make \
    python3 python3-pip \
    nodejs npm && \
    yum clean all

# 9999 포트를 외부에 노출
EXPOSE 8080

# 컨테이너 실행 시 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "algofi.jar"]