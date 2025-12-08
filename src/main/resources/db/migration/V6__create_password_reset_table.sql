CREATE TABLE IF NOT EXISTS password_reset_token
(
    token      VARCHAR(128) PRIMARY KEY,
    email      VARCHAR(320)             NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_password_reset_email ON password_reset_token (email);
