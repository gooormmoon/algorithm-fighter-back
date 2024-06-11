-- Users table creation
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       gender CHAR(1) NOT NULL,
                       birth_date DATE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
);

-- Plants table creation
CREATE TABLE plants (
                        plant_id INT AUTO_INCREMENT PRIMARY KEY,
                        plant_name VARCHAR(50) NOT NULL,
                        plant_type VARCHAR(50) NOT NULL,
                        plant_desc VARCHAR(255),
                        image_url VARCHAR(255),
                        temperature_low FLOAT NOT NULL,
                        temperature_high FLOAT NOT NULL,
                        humidity_low FLOAT NOT NULL,
                        humidity_high FLOAT NOT NULL,
                        watering_interval INT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP
);

-- User_Plants table creation
CREATE TABLE user_plants (
                             user_plant_id INT AUTO_INCREMENT PRIMARY KEY,
                             user_id INT NOT NULL,
                             plant_id INT,
                             plant_nickname VARCHAR(50) NOT NULL,
                             FOREIGN KEY (user_id) REFERENCES users(user_id),
                             FOREIGN KEY (plant_id) REFERENCES plants(plant_id) ON DELETE SET NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP
);

-- Plant_Logs table creation
CREATE TABLE plant_logs (
                            plant_log_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_plant_id INT NOT NULL,
                            log_date DATE NOT NULL,
                            note VARCHAR(255),
                            watered BOOLEAN,
                            FOREIGN KEY (user_plant_id) REFERENCES user_plants(user_plant_id),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP
);

-- Users 테이블에 데이터 삽입
-- $2a$10$vYR4pPQqR/oZcUDZfXrahecEejQHY0kLkDB5s.FctPRMcEMh1PYhG = password123
-- $2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC = password456
-- $2a$10$ke3IM6noeWfQtX6POjZHl.49gSolYbqfrSTIn8sOQubdwjP2IT94q = password789
-- Users 테이블에 데이터 삽입
INSERT INTO users (name, email, password, gender, birth_date) VALUES
                                                                  ('John', 'john123@qmail.com', '$2a$10$vYR4pPQqR/oZcUDZfXrahecEejQHY0kLkDB5s.FctPRMcEMh1PYhG', 'M', '1988-05-01'),
                                                                  ('Jane', 'jane456@qmail.com', '$2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC', 'F', '1995-08-15'),
                                                                  ('Peter', 'peter789@qmail.com', '$2a$10$ke3IM6noeWfQtX6POjZHl.49gSolYbqfrSTIn8sOQubdwjP2IT94q', 'M', '1981-12-25'),
                                                                  ('Susan', 'susan321@qmail.com', '$2a$10$vYR4pPQqR/oZcUDZfXrahecEejQHY0kLkDB5s.FctPRMcEMh1PYhG', 'F', '1990-06-02'),
                                                                  ('David', 'david654@qmail.com', '$2a$10$vYR4pPQqR/oZcUDZfXrahecEejQHY0kLkDB5s.FctPRMcEMh1PYhG', 'M', '1992-03-11'),
                                                                  ('Judy', 'judy987@qmail.com', '$2a$10$ke3IM6noeWfQtX6POjZHl.49gSolYbqfrSTIn8sOQubdwjP2IT94q', 'F', '1983-10-19'),
                                                                  ('Timothy', 'timothy012@qmail.com', '$2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC', 'M', '1996-11-30'),
                                                                  ('Lisa', 'lisa345@qmail.com', '$2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC', 'F', '1988-07-20'),
                                                                  ('Steve', 'steve678@qmail.com', '$2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC', 'M', '1977-01-05'),
                                                                  ('Emily', 'emily321@qmail.com', '$2a$10$Vqx3VUuB8gy9NvtKHQARWOOYB2wG4wV2WXy1sdQHIoY8TivSHZ3sC', 'F', '1994-09-23');

-- Plants 테이블에 데이터 삽입
INSERT INTO plants (plant_name, plant_type, plant_desc, image_url, temperature_low, temperature_high, humidity_low, humidity_high, watering_interval) VALUES
                                                                                                                                                          ('아이비', '덩굴식물', '공기 정화 능력이 뛰어난 식물입니다.', 'https://example.com/ivy.jpg', 12, 28, 40, 70, 7),
                                                                                                                                                          ('스투키', '선인장', '건조한 환경에 잘 적응합니다.', 'https://example.com/stuki.jpg', 10, 30, 10, 50, 21),
                                                                                                                                                          ('로즈마리', '허브', '요리에 사용되는 허브입니다.', 'https://example.com/rosemary.jpg', 10, 30, 30, 50, 14),
                                                                                                                                                          ('자스민', '꽃', '달콤한 향기가 나는 꽃입니다.', 'https://example.com/jasmine.jpg', 15, 30, 40, 70, 7),
                                                                                                                                                          ('스파티필럼', '실내식물', '실내 장식용으로 인기가 높습니다.', 'https://example.com/spathiphyllum.jpg', 18, 28, 50, 70, 10),
                                                                                                                                                          ('스킨답서스', '실내식물', '낮은 빛 조건에서도 잘 자랍니다.', 'https://example.com/syngonium.jpg', 15, 30, 30, 50, 10),
                                                                                                                                                          ('페퍼민트', '허브', '상쾌한 향기가 나는 허브입니다.', 'https://example.com/peppermint.jpg', 15, 28, 40, 60, 7),
                                                                                                                                                          ('산세베리아', '실내식물', '공기 정화 능력이 뛰어납니다.', 'https://example.com/sansevieria.jpg', 15, 30, 20, 50, 21),
                                                                                                                                                          ('식물성이끼', '이끼', '습한 환경에서 잘 자랍니다.', 'https://example.com/moss.jpg', 10, 25, 70, 100, 5),
                                                                                                                                                          ('올리브', '나무', '지중해 기후를 선호합니다.', 'https://example.com/olive.jpg', 10, 30, 30, 50, 14);

-- User_Plants 테이블에 데이터 삽입
INSERT INTO user_plants (user_id, plant_id, plant_nickname) VALUES
                                                                (1, 1, '노을이'), (1, 2, '햇님'), (2, 1, '별빛'), (2, 4, '새벽'), (2, 6, '향기'),
                                                                (3, 7, '구름'), (3, 9, '바람'), (4, 10, '무지개'), (4, 12, '햇살'), (5, 14, '노을');

-- Plant_Logs 테이블에 데이터 삽입
INSERT INTO plant_logs (user_plant_id, log_date, note, watered) VALUES
                                                                    (1, '2023-03-22', '잎이 탈색되었습니다.', TRUE),
                                                                    (1, '2023-03-23', '새로운 아이비를 구입했습니다.', FALSE),
                                                                    (1, '2023-03-24', '비료를 주었습니다.', TRUE),
                                                                    (2, '2023-03-22', '잎이 탈색되었습니다.', FALSE),
                                                                    (2, '2023-03-23', '더 밝은 곳으로 옮겼습니다.', TRUE),
                                                                    (2, '2023-03-24', '물을 적게 주었습니다.', FALSE),
                                                                    (3, '2023-03-22', '물이 쌓였습니다.', FALSE),
                                                                    (3, '2023-03-23', '관수 주기를 조절했습니다.', TRUE),
                                                                    (3, '2023-03-24', '수액을 주었습니다.', TRUE),
                                                                    (4, '2023-03-22', '잎이 떨어졌습니다.', FALSE);