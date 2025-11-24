CREATE TABLE class_additional_links
(
    class_id UUID NOT NULL,
    link     TEXT NOT NULL,
    CONSTRAINT fk_class_additional_links_class
        FOREIGN KEY (class_id)
            REFERENCES class (id)
            ON DELETE CASCADE
);
