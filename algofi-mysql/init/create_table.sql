/* member table 생성 */
CREATE TABLE member (
                        member_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(10) NOT NULL,
                        nickname VARCHAR(30) NOT NULL,
                        login_id VARCHAR(100) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        role VARCHAR(255),
                        profile_image_url VARCHAR(255),
                        description VARCHAR(255),
                        alert VARCHAR(255),
                        login_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* filestorage table 생성 */
CREATE TABLE filestorage (
                             file_storage_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             member_id INT NOT NULL,
                             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_time TIMESTAMP,
                             FOREIGN KEY(member_id) REFERENCES member(member_id)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* file table 생성 */
CREATE TABLE file (
                      file_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                      content TEXT,
                      name VARCHAR(255),
                      type VARCHAR(255),
                      path VARCHAR(255),
                      language VARCHAR(20),
                      file_storage_id INT NOT NULL,
                      parent_id INT NULL,
                      FOREIGN KEY(file_storage_id) REFERENCES filestorage(file_storage_id),
                      FOREIGN KEY(parent_id) REFERENCES file(file_id) ON DELETE CASCADE
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* algorithmproblem table 생성 */
CREATE TABLE algorithmproblem (
                                  algorithmproblem_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                                  title VARCHAR(100) NOT NULL,
                                  level VARCHAR(10) NOT NULL,
                                  content TEXT,
                                  recommend_time INT
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* chatroom table 생성 */
CREATE TABLE chatroom (
                          chatroom_id VARCHAR(36) PRIMARY KEY,
                          chatroom_name VARCHAR(100)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* message table 생성 */
CREATE TABLE message (
                         message_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                         content TEXT,
                         type VARCHAR(10),
                         created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         sender_id INT NULL,
                         chatroom_id VARCHAR(36) NOT NULL,
                         FOREIGN KEY(sender_id) REFERENCES member(member_id),
                         FOREIGN KEY(chatroom_id) REFERENCES chatroom(chatroom_id)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* member_chatroom table 생성 */
CREATE TABLE member_chatroom (
                                 chatroom_id VARCHAR(36) NOT NULL,
                                 member_id INT NULL,
                                 FOREIGN KEY(chatroom_id) REFERENCES chatroom(chatroom_id),
                                 FOREIGN KEY(member_id) REFERENCES  member(member_id)
);

/* gameresult table 생성 */
CREATE TABLE gameresult (
                            game_result_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                            running_time VARCHAR(100),
                            host_code_content VARCHAR(5500),
                            guest_code_content VARCHAR(5500),
                            created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            algorithmproblem_id INT NOT NULL,
                            chatroom_id VARCHAR(36) NOT NULL,
                            FOREIGN KEY (algorithmproblem_id) REFERENCES algorithmproblem(algorithmproblem_id),
                            FOREIGN KEY (chatroom_id) REFERENCES chatroom(chatroom_id)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* member_gameresult table 생성 */
CREATE TABLE member_gameresult (
                                   game_result_id INT NOT NULL,
                                   member_id INT NOT NULL,
                                   FOREIGN KEY(game_result_id) REFERENCES gameresult(game_result_id),
                                   FOREIGN KEY(member_id) REFERENCES member(member_id)
);

/* testcase table 생성 */
CREATE TABLE testcase (
                          test_case_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                          test_input VARCHAR(500),
                          test_output VARCHAR(500),
                          algorithmproblem_id INT NOT NULL,
                          FOREIGN KEY(algorithmproblem_id) REFERENCES algorithmproblem(algorithmproblem_id)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
