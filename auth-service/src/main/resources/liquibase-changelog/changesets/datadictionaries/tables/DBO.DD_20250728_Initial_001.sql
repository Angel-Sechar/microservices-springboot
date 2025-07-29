--Table: auth_user
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Repository of user register in auth-service.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user';

-- Column: auth_userid
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Identifier of the auth_user table (Primary Key).',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'auth_userid';

-- Column: auth_username
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Unique username used for authentication.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'auth_username';

-- Column: email
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Unique email address of the user.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'email';

-- Column: auth_password
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Hashed password used for user authentication.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'auth_password';

-- Column: first_name
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'User''s first name.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'first_name';

-- Column: paternal_surname
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'User''s paternal surname.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'paternal_surname';

-- Column: maternal_surname
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'User''s maternal surname.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'maternal_surname';

-- Column: is_active
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Flag indicating whether the user account is active (1) or disabled (0).',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'is_active';

-- Column: created_at
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Date and time when the user account was created.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'created_at';

-- Column: last_login
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Timestamp of the last successful login.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_user',  
    @level2type = N'COLUMN', @level2name = N'last_login';
----------------------------------------------------------------------------------------------------
-- Table: refresh_token
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Stores refresh tokens used for authentication, linked to users in auth_user.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token';

-- Column: refresh_tokenid
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Identifier of the refresh_token table (Primary Key).',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token',  
    @level2type = N'COLUMN', @level2name = N'refresh_tokenid';

-- Column: token
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Unique JWT refresh token string assigned to a user session.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token',  
    @level2type = N'COLUMN', @level2name = N'token';

-- Column: auth_userid
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Identifier of the auth_user table. Foreign key reference.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token',  
    @level2type = N'COLUMN', @level2name = N'auth_userid';

-- Column: expiration_at
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Date and time when the refresh token expires.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token',  
    @level2type = N'COLUMN', @level2name = N'expiration_at';

-- Column: creation_on
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Date and time when the refresh token was created.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'refresh_token',  
    @level2type = N'COLUMN', @level2name = N'creation_on';

----------------------------------------------------------------------------------------------------
-- Table: auth_role
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Repository of roles available in the authentication service.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_role';

-- Column: auth_roleid
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Identifier of the auth_role table (Primary Key).',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_role',  
    @level2type = N'COLUMN', @level2name = N'auth_roleid';

-- Column: role_name
EXEC sys.sp_addextendedproperty  
    @name = N'MS_Description',  
    @value = N'Unique name that defines a role, check constant 5.',  
    @level0type = N'SCHEMA', @level0name = N'DBO',  
    @level1type = N'TABLE', @level1name = N'auth_role',  
    @level2type = N'COLUMN', @level2name = N'role_name';