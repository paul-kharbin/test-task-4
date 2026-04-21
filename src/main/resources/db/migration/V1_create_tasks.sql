CREATE TABLE tasks (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       title VARCHAR(100) NOT NULL,
       description VARCHAR(1000),
       status VARCHAR(20) NOT NULL,
       created_at TIMESTAMP NOT NULL,
       updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_tasks_status ON tasks(status)