CREATE TABLE auth_user (
    auth_userid BIGINT IDENTITY(1,1) PRIMARY KEY CLUSTERED,
    auth_username VARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    auth_password NVARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    paternal_surname VARCHAR(50) NOT NULL,
    maternal_surname VARCHAR(50) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    last_login DATETIME2,
    CONSTRAINT uq_person_person_user UNIQUE (auth_username),
    CONSTRAINT uq_person_email UNIQUE (email)
);

CREATE TABLE auth_token (
    auth_token_id BIGINT IDENTITY(1,1) PRIMARY KEY CLUSTERED,
    auth_token NVARCHAR(255) NOT NULL UNIQUE,
    auth_user_id BIGINT NOT NULL,
    expiration_at DATETIME2 NOT NULL,
    creation_on DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE UNIQUE INDEX ix_uq_token ON auth_token(auth_token);
CREATE NONCLUSTERED INDEX ixn_expiry ON auth_token(expiration_at);

/*Change to stateful instead*/
ALTER TABLE auth_token
ADD CONSTRAINT fk_auth_token_user
FOREIGN KEY (auth_userid)
REFERENCES auth_user(auth_userid)
ON DELETE CASCADE;

CREATE TABLE role (
    role_id BIGINT IDENTITY(1,1) PRIMARY KEY CLUSTERED,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

