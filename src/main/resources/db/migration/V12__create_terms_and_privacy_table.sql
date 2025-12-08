CREATE TABLE terms_and_privacy
(
  id         UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
  type       VARCHAR(20) NOT NULL CHECK (type IN ('TERMS', 'PRIVACY')),
  content    TEXT        NOT NULL,
  version    INTEGER     NOT NULL DEFAULT 1,
  created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_active  BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX idx_unique_active_term_type ON terms_and_privacy (type) WHERE is_active = TRUE;

CREATE INDEX idx_terms_privacy_type ON terms_and_privacy (type);

CREATE INDEX idx_terms_privacy_active ON terms_and_privacy (is_active) WHERE is_active = TRUE;

COMMENT ON TABLE terms_and_privacy IS 'Stores terms of use and privacy policy content';

COMMENT ON COLUMN terms_and_privacy.type IS 'Type of term: TERMS or PRIVACY';
COMMENT ON COLUMN terms_and_privacy.content IS 'HTML content of the term';
COMMENT ON COLUMN terms_and_privacy.version IS 'Version number of the term';
COMMENT ON COLUMN terms_and_privacy.is_active IS 'Indicates if this is the active version';

INSERT INTO terms_and_privacy (type, content, version, is_active)
VALUES ('TERMS', '<h1>Termos de Uso</h1><p>Conteúdo dos termos de uso será adicionado aqui.</p>', 1, TRUE),
       ('PRIVACY',
        '<h1>Política de Privacidade</h1><p>Conteúdo da política de privacidade será adicionado aqui.</p>', 1, TRUE);
