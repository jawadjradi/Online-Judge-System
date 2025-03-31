CREATE TABLE problems (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          title VARCHAR(255),
                          description TEXT
);

CREATE TABLE test_cases (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            problem_id INT,
                            input_text TEXT,
                            expected_output TEXT,
                            FOREIGN KEY (problem_id) REFERENCES problems(id)
);

CREATE TABLE submissions (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             user_id INT,
                             problem_id INT,
                             code TEXT,
                             status VARCHAR(20),  -- 'Accepted', 'Wrong Answer', 'Runtime Error', etc.
                             score INT,
                             submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE leaderboard (
                             user_id INT PRIMARY KEY,
                             total_score INT,
                             total_solved INT,
                             FOREIGN KEY (user_id) REFERENCES users(id)
);
