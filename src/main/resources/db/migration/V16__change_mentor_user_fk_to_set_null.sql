ALTER TABLE mentor
  ALTER COLUMN user_uuid DROP NOT NULL;

ALTER TABLE mentor
  DROP CONSTRAINT IF EXISTS fk_mentor_user;

ALTER TABLE mentor
  ADD CONSTRAINT fk_mentor_user
    FOREIGN KEY (user_uuid)
      REFERENCES auth_user (id)
      ON DELETE SET NULL;

ALTER TABLE mentored
  ALTER COLUMN user_uuid DROP NOT NULL;

ALTER TABLE mentored
  DROP CONSTRAINT IF EXISTS fk_mentored_user;

ALTER TABLE mentored
  ADD CONSTRAINT fk_mentored_user
    FOREIGN KEY (user_uuid)
      REFERENCES auth_user (id)
      ON DELETE SET NULL;

