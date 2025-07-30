/*
----------------------------------------------------------------------------------------------------
STORED PROCEDURE: SP_AUTH_USER_UPD_LASTLOGIN
AUTHOR: ANGEL J. SECHAR  VALDEZ
DESCRIPTION: UPDATE LAST LOGIN FOR USER BY ID

PARAMETERS:
    @UserId (BIGINT) - The ID of the user whose last login time should be updated.
    @LastLogin (DATETIME2) - The new last login timestamp for the user.
----------------------------------------------------------------------------------------------------
EXAMPLE: 
DECLARE @LastLogin DATETIME2 = GETDATE();
EXEC DBO.SP_AUTH_USER_UPD_LASTLOGIN 1, @LastLogin;
----------------------------------------------------------------------------------------------------

*/
CREATE OR ALTER PROCEDURE DBO.SP_AUTH_USER_UPD_LASTLOGIN
(
    @UserId BIGINT,
    @LastLogin DATETIME2
)
AS
BEGIN
SET NOCOUNT ON;

    UPDATE U
    SET U.last_login = @LastLogin   
    FROM DBO.auth_user U
    WHERE U.auth_userid = @UserId;

SET NOCOUNT OFF;
END;