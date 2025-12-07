ALTER TABLE auth_user
ALTER COLUMN email DROP NOT NULL,
ALTER COLUMN password DROP NOT NULL;

ALTER TABLE mentor
ADD COLUMN active boolean NOT NULL DEFAULT true;

alter table mentor
alter column user_uuid DROP NOT null;