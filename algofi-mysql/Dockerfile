# MySQL 8.0 버전을 베이스 이미지로 사용
FROM mysql:8.0

# init.sql 파일을 /docker-entrypoint-initdb.d/로 복사
# /docker-entrypoint-initdb.d/에 있는 SQL 스크립트는 컨테이너가 처음 실행 시 자동 실행됨
COPY ./init/init.sql /docker-entrypoint-initdb.d/

# 사용자 정의 설정 파일을 /etc/mysql/custom.cnf로 복사
COPY ./config/mysql.conf /etc/mysql/custom.cnf

# 계정 정보 설정
ENV MYSQL_ROOT_PASSWORD=mypassword
ENV MYSQL_USER=myuser
ENV MYSQL_PASSWORD=mypassword
ENV MYSQL_DATABASE=mydb

# MySQL이 사용하는 3306 포트를 노출
EXPOSE 3306

# MySQL 서버 실행, 사용자 정의 설정 파일 지정
CMD ["mysqld", "--defaults-file=/etc/mysql/custom.cnf"]