ALTER TABLE class_mentored
    ADD COLUMN certificate_generated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE class_mentored
    ADD COLUMN certificate_generated_at TIMESTAMP NULL;
