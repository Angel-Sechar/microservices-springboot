CREATE TABLE refresh_token (
    token_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    token NVARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE UNIQUE INDEX idx_token ON refresh_token(token);
CREATE INDEX idx_expiry ON refresh_token(expiry_date);

/*Change to stateful instead*/
ALTER TABLE refresh_token
ADD CONSTRAINT fk_refresh_token_user
FOREIGN KEY (user_id)
REFERENCES user(user_id)
ON DELETE CASCADE;

CREATE TABLE role (
    role_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(MAX) NOT NULL UNIQUE,
    first_name NVARCHAR(50),
    paternal_surname NVARCHAR(50),
    maternal_surname NVARCHAR(50),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    last_login DATETIME2,
    CONSTRAINT idx_username UNIQUE (username),
    CONSTRAINT idx_email UNIQUE (email)
);