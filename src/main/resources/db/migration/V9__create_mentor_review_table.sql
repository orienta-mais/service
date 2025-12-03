CREATE TABLE mentor_review
(
    id              UUID PRIMARY KEY,
    mentor_id       UUID    NOT NULL,
    mentored_id     UUID    NOT NULL,
    didactics       INTEGER NOT NULL,
    subject_mastery INTEGER NOT NULL,
    punctuality     INTEGER NOT NULL,
    communication   INTEGER NOT NULL,
    engagement      INTEGER NOT NULL,
    feedback        VARCHAR(500),
    CONSTRAINT fk_mentor_review_mentor FOREIGN KEY (mentor_id) REFERENCES mentor (id),
    CONSTRAINT fk_mentor_review_mentored FOREIGN KEY (mentored_id) REFERENCES mentored (id)
);

CREATE INDEX idx_mentor_review_mentor_id ON mentor_review (mentor_id);
CREATE INDEX idx_mentor_review_mentored_id ON mentor_review (mentored_id);
