/*
----------------------------------------------------------------------------------------------------
STORED PROCEDURE: SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS
AUTHOR: ANGEL J. SECHAR  VALDEZ
DESCRIPTION: CLEAN UP EXPIRED TOKEN BY TIME
----------------------------------------------------------------------------------------------------
PARAMETERS:
    @ExpirationDate DATETIME2 - Cutoff timestamp; tokens with an expiration date less than or equal
                                to this value will be deleted.
----------------------------------------------------------------------------------------------------
EXAMPLE: 

DECLARE @now DATETIME2 = GETDATE(),
        @RowCounter INT;
EXEC DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS @now, @RowCounter OUTPUT;
SELECT @RowCounter AS DeletedRows;
----------------------------------------------------------------------------------------------------
*/
CREATE OR ALTER PROCEDURE DBO.SP_REFRESH_TOKEN_DEL_EXPIRED_TOKENS
(
    @ExpirationDate DATETIME2,
    @RowCounter INT OUTPUT
)
AS
BEGIN
SET NOCOUNT ON;
BEGIN TRY
    DELETE FROM DBO.refresh_token
    WHERE expiration_at <= @ExpirationDate

    SET @RowCounter = @@ROWCOUNT;
    
END TRY
BEGIN CATCH
    SET @RowCounter = -1; -- Indicate an error occurred
    --ADD AN SP TO STORE ERRORS IN FUTURE
END CATCH;
SET NOCOUNT OFF;
END;