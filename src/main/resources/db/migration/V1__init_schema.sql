CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auth_user
(
    id       UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role     VARCHAR(50)         NOT NULL
);

CREATE TABLE interests
(
    id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE mentor
(
    id           UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_uuid    UUID UNIQUE  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    birth_date   DATE,
    social_medias VARCHAR(255),
    description  TEXT,
    state        VARCHAR(100),
    nationality  VARCHAR(100),
    CONSTRAINT fk_mentor_user FOREIGN KEY (user_uuid) REFERENCES auth_user (id) ON DELETE CASCADE
);

CREATE TABLE mentor_interests
(
    mentor_id   UUID NOT NULL,
    interest_id UUID NOT NULL,
    PRIMARY KEY (mentor_id, interest_id),
    CONSTRAINT fk_mentor FOREIGN KEY (mentor_id) REFERENCES mentor (id) ON DELETE CASCADE,
    CONSTRAINT fk_interest FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE
);

CREATE TABLE mentored
(
    id           UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_uuid    UUID UNIQUE  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    birth_date   DATE,
    social_medias VARCHAR(255),
    description  TEXT,
    state        VARCHAR(100),
    nationality  VARCHAR(100),
    CONSTRAINT fk_mentored_user FOREIGN KEY (user_uuid) REFERENCES auth_user (id) ON DELETE CASCADE
);

CREATE TABLE mentored_interests
(
    mentored_id UUID NOT NULL,
    interest_id UUID NOT NULL,
    PRIMARY KEY (mentored_id, interest_id),
    CONSTRAINT fk_mentored FOREIGN KEY (mentored_id) REFERENCES mentored (id) ON DELETE CASCADE,
    CONSTRAINT fk_interest FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE
);

CREATE TABLE class
(
    id           UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    link         VARCHAR(255),
    max_guest    INT,
    start_time   TIMESTAMP    NOT NULL,
    end_time     TIMESTAMP,
    mentor_id    UUID         NOT NULL,
    present_code VARCHAR(100),
    CONSTRAINT fk_class_mentor FOREIGN KEY (mentor_id) REFERENCES mentor (id) ON DELETE CASCADE
);

CREATE TABLE class_interests
(
    class_id    UUID NOT NULL,
    interest_id UUID NOT NULL,
    PRIMARY KEY (class_id, interest_id),
    CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES class (id) ON DELETE CASCADE,
    CONSTRAINT fk_interest FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE
);

CREATE TABLE class_mentored
(
    class_id    UUID NOT NULL,
    mentored_id UUID NOT NULL,
    PRIMARY KEY (class_id, mentored_id),
    CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES class (id) ON DELETE CASCADE,
    CONSTRAINT fk_mentored FOREIGN KEY (mentored_id) REFERENCES mentored (id) ON DELETE CASCADE
);

CREATE TABLE certificate
(
    id          UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    mentored_id UUID NOT NULL,
    class_id    UUID NOT NULL,
    CONSTRAINT fk_certificate_mentored FOREIGN KEY (mentored_id) REFERENCES mentored (id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_class FOREIGN KEY (class_id) REFERENCES class (id) ON DELETE CASCADE,
    CONSTRAINT uq_certificate UNIQUE (mentored_id, class_id)
);

CREATE INDEX idx_mentor_user_uuid ON mentor (user_uuid);
CREATE INDEX idx_mentored_user_uuid ON mentored (user_uuid);
CREATE INDEX idx_class_mentor_id ON class (mentor_id);
CREATE INDEX idx_class_mentored_class_id ON class_mentored (class_id);
CREATE INDEX idx_class_mentored_mentored_id ON class_mentored (mentored_id);
