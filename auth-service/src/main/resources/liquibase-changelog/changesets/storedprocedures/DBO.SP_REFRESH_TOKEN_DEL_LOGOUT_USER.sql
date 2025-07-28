/*
---------------------------------------------------------------------------
STORED PROCEDURE: SP_REFRESH_TOKEN_DEL_LOGOUT_USER
AUTHOR: ANGEL J. SECHAR  VALDEZ
DESCRIPTION: CLEAN UP TOKENS FOR LOGGED OUT USERS
---------------------------------------------------------------------------
EXAMPLE: 

EXEC DBO.SP_REFRESH_TOKEN_DEL_LOGOUT_USER 1

*/
CREATE OR ALTER PROCEDURE DBO.SP_REFRESH_TOKEN_DEL_LOGOUT_USER
(
    @UserId BIGINT
)
AS
BEGIN
SET NOCOUNT ON;

    DELETE FROM DBO.refresh_token
    WHERE auth_userid = @UserId

SET NOCOUNT OFF;
END;