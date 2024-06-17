-- create member table
CREATE TABLE IF NOT EXISTS member (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL,
                                      nickname VARCHAR(50) NOT NULL,
                                      login_id VARCHAR(50) NOT NULL,
                                      password VARCHAR(50) NOT NULL,
                                      role VARCHAR(50) NOT NULL,
                                      profile_image_url VARCHAR(255),
                                      description TEXT,
                                      alert VARCHAR(50)
);

-- create filestorage table
CREATE TABLE IF NOT EXISTS filestorage (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(50) NOT NULL,
                                           member_id INT NOT NULL,
                                           FOREIGN KEY (member_id) REFERENCES member(id)
);

-- create file table
CREATE TABLE IF NOT EXISTS file (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    content TEXT NOT NULL,
                                    name VARCHAR(255) NOT NULL,
                                    type VARCHAR(50) NOT NULL,
                                    path VARCHAR(255) NOT NULL,
                                    language VARCHAR(50) NOT NULL,
                                    file_storage_id INT NOT NULL,
                                    parent_id INT,
                                    FOREIGN KEY (file_storage_id) REFERENCES filestorage(id),
                                    FOREIGN KEY (parent_id) REFERENCES file(id) ON DELETE CASCADE
);

-- create algorithmproblem table
CREATE TABLE IF NOT EXISTS algorithmproblem (
                                                id INT AUTO_INCREMENT PRIMARY KEY,
                                                title VARCHAR(255) NOT NULL,
                                                level INT NOT NULL,
                                                content TEXT NOT NULL,
                                                recommend_time INT NOT NULL
);

-- create chatroom table
CREATE TABLE IF NOT EXISTS chatroom (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        chatroom_name VARCHAR(255) NOT NULL
);

-- create message table
CREATE TABLE IF NOT EXISTS message (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       content TEXT NOT NULL,
                                       sender_id INT NOT NULL,
                                       chatroom_id INT NOT NULL,
                                       FOREIGN KEY (sender_id) REFERENCES member(id),
                                       FOREIGN KEY (chatroom_id) REFERENCES chatroom(id)
);

-- create member_chatroom table
CREATE TABLE IF NOT EXISTS member_chatroom (
                                               chatroom_id INT NOT NULL,
                                               member_id INT NOT NULL,
                                               PRIMARY KEY (chatroom_id, member_id),
                                               FOREIGN KEY (chatroom_id) REFERENCES chatroom(id),
                                               FOREIGN KEY (member_id) REFERENCES member(id)
);

-- create gameresult table
CREATE TABLE IF NOT EXISTS gameresult (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          running_time VARCHAR(50) NOT NULL,
                                          member_code_content TEXT NOT NULL,
                                          other_member_code_content TEXT NOT NULL,
                                          algorithm_problem_id INT NOT NULL,
                                          chatroom_id INT NOT NULL,
                                          FOREIGN KEY (algorithm_problem_id) REFERENCES algorithmproblem(id),
                                          FOREIGN KEY (chatroom_id) REFERENCES chatroom(id)
);

-- create member_gameresult table
CREATE TABLE IF NOT EXISTS member_gameresult (
                                                 game_result_id INT NOT NULL,
                                                 member_id INT NOT NULL,
                                                 PRIMARY KEY (game_result_id, member_id),
                                                 FOREIGN KEY (game_result_id) REFERENCES gameresult(id),
                                                 FOREIGN KEY (member_id) REFERENCES member(id)
);

-- create testcase table
CREATE TABLE IF NOT EXISTS testcase (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        test_input TEXT NOT NULL,
                                        test_output TEXT NOT NULL,
                                        algorithm_problem_id INT NOT NULL,
                                        FOREIGN KEY (algorithm_problem_id) REFERENCES algorithmproblem(id)
);