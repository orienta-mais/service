CREATE TABLE registration_token
(
    id         UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expiration TIMESTAMP    NOT NULL
);

CREATE INDEX idx_registration_token_email ON registration_token (email);
CREATE INDEX idx_registration_token_token ON registration_token (token);