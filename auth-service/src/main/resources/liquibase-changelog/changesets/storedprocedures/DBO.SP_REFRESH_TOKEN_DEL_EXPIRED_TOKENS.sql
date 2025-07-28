/*
---------------------------------------------------------------------------
STORED PROCEDURE: SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS
AUTHOR: ANGEL J. SECHAR  VALDEZ
DESCRIPTION: CLEAN UP EXPIRED TOKEN BY TIME
---------------------------------------------------------------------------
EXAMPLE: 

DECLARE @now DATETIME2 = GETDATE();
EXEC DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS @now;

*/
CREATE OR ALTER PROCEDURE DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS
(
    @ExpirationDate DATETIME2
)
AS
BEGIN
SET NOCOUNT ON;

    DELETE FROM DBO.refresh_token
    WHERE expiration_at <= @ExpirationDate

SET NOCOUNT OFF;
END;