CREATE TABLE users (
                       pk_user_id BIGINT NOT NULL AUTO_INCREMENT,

                       email VARCHAR(254) NOT NULL,

                       first_name VARCHAR(100) NOT NULL,
                       middle_name VARCHAR(100) NULL,
                       last_name VARCHAR(100) NOT NULL,

                       phone_number VARCHAR(20) NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT pk_users PRIMARY KEY (pk_user_id),
                       CONSTRAINT uk_users_email UNIQUE (email)

) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
