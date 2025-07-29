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
----------------------------------------------------------------------------------------------------
CREATE TABLE refresh_token (
    refresh_tokenid BIGINT IDENTITY(1,1) ,
    token NVARCHAR(500) NOT NULL UNIQUE,
    auth_userid BIGINT NOT NULL,
    expiration_at DATETIME2 NOT NULL,
    creation_on DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT pk_refresh_token PRIMARY KEY CLUSTERED (refresh_tokenid),
    CONSTRAINT uq_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (auth_userid)
        REFERENCES auth_user(auth_userid)
        ON DELETE CASCADE
);

--PERFORMANCE
CREATE NONCLUSTERED INDEX nix_refresh_token_token ON DBO.refresh_token (token) 
INCLUDE (auth_userid, expiration_at, creation_on);
----------------------------------------------------------------------------------------------------
CREATE TABLE auth_role (
    auth_roleid BIGINT IDENTITY(1,1) PRIMARY KEY CLUSTERED,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

