# fedora 계열의 Oracle Linux 이미지를 사용
# arm64, amd64 모두 지원
FROM mysql:oraclelinux9

# 사용자 정의 설정 파일을 /etc/my.cnf.d로 복사
# 이 디렉토리에 파일을 넣으면 MySQL이 자동으로 이 설정 파일을 읽게 됩니다.
#COPY ./config/mysql.cnf /etc/my.cnf
#RUN chmod 644 /etc/my.cnf
COPY ./config/mysql.cnf /etc/my.cnf.d/my.cnf
RUN chmod 644 /etc/my.cnf.d/my.cnf

# init 파일을 /docker-entrypoint-initdb.d/로 복사
# 이 디렉토리에 있는 SQL 스크립트는 컨테이너가 처음 실행 시 자동 실행됨
COPY ./init/create_table.sql /docker-entrypoint-initdb.d/
COPY ./init/insert_data.sql /docker-entrypoint-initdb.d/
RUN chmod 755 /docker-entrypoint-initdb.d/*.sql

# 환경변수로 MySQL 계정 정보 설정
ENV MYSQL_ROOT_PASSWORD=mypassword
ENV MYSQL_USER=myuser
ENV MYSQL_PASSWORD=mypassword
ENV MYSQL_DATABASE=mydb

# MySQL이 사용하는 3306 포트를 노출
EXPOSE 3306

# MySQL 서버 실행, MySQL 서버가 설정 파일을 읽어서 시작하도록 함
CMD ["mysqld"]