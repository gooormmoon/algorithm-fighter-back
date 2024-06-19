# Base 이미지로 OpenJDK 17 설정
FROM openjdk:23-ea-17-jdk-bullseye

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사
COPY ./build/libs/*-SNAPSHOT.jar /app/algofi.jar

# 필요한 패키지 설치 및 정리
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc g++ make python3 python3-pip nodejs npm && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 9000 포트를 외부에 노출
EXPOSE 9000

# 컨테이너 실행 시 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "algofi.jar"]
