/*Creation of databases*/
IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = 'db_auth'
)
BEGIN
    CREATE DATABASE db_auth;
END
GO