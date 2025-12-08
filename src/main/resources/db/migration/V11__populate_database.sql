DO
$$
  DECLARE
    v_mentor             UUID;
    v_user               UUID;
    v_mentored           UUID;
    v_class              UUID;
    i                    INT;
    j                    INT;
    k                    INT;
    n_classes_per_mentor INT       := 6;
    mentor_first_names   TEXT[]    := ARRAY [
      'Carlos','Felipe','João','Marcos','Lucas','Rafael','Andre','Gustavo','Bruno','Daniel',
      'Thiago','Rodrigo','Eduardo','Marcelo','Fernando','Paulo','Vitor','Henrique','Leandro','Igor',
      'Diego','Alexandre','Ricardo','Sergio','Murilo','Fábio','Caio','Samuel','Mateus','Guilherme',
      'Artur','Roberto','Elias','Nicolas','Davi','Enzo','Gabriel','Matheus','Pedro','Luiz',
      'Joaquim','Cauã','Murilo','César','Nelson','Adriano','Romulo','Afonso','Washington','Emanuel'
      ];
    mentor_last_names    TEXT[]    := ARRAY [
      'Silva','Souza','Oliveira','Pereira','Costa','Santos','Rodrigues','Almeida','Nascimento','Lima',
      'Gomes','Ramos','Fernandes','Carvalho','Mendes','Barbosa','Barros','Freitas','Moreira','Pinto',
      'Cardoso','Moraes','Teixeira','Azevedo','Nogueira','Campos','Ribeiro','Martins','Rocha','Dias',
      'Cavalcante','Santos','Pacheco','Faria','Santos','Ribeiro','Silveira','Ferreira','Castro','Brito',
      'Leite','Correia','Assis','Macedo','Frota','Amaral','Moura','Vaz','Queiroz','Siqueira','Bueno'
      ];
    mentored_first_names TEXT[]    := ARRAY [
      'Ana','Maria','Beatriz','Laura','Mariana','Camila','Julia','Gabriela','Fernanda','Patricia',
      'Aline','Renata','Paula','Carolina','Bianca','Sofia','Isabela','Lara','Luana','Natália',
      'Bruna','Thaís','Letícia','Rafaela','Catarina','Karla','Monica','Priscila','Adriana','Elisa',
      'Helena','Mirella','Clara','Daniela','Lorena','Yasmin','Evelyn','Sabrina','Yara','Vanessa'
      ];
    mentored_last_names  TEXT[]    := ARRAY [
      'Silva','Santos','Oliveira','Costa','Pereira','Rodrigues','Gomes','Alves','Ferreira','Ribeiro',
      'Martins','Carvalho','Lopes','Barbosa','Melo','Moreira','Dias','Rocha','Freitas','Mendes',
      'Teixeira','Azevedo','Monteiro','Moraes','Viana','Vasconcelos','Queiroz','Cruz','Nunes','Pinto',
      'Cardoso','Neves','Sampaio','Campos','Rangel','Bueno','Brito','Lemos','Faria','Camargo'
      ];
    cities               TEXT[]    := ARRAY [
      'São Paulo','Rio de Janeiro','Minas Gerais'
      ];
    class_titles         TEXT[]    := ARRAY [
      'Introdução à Programação','Lógica de Algoritmos','Java para Iniciantes','Python Básico',
      'Desenvolvimento Web com HTML e CSS','JavaScript Essencial','APIs REST com Spring Boot',
      'Banco de Dados PostgreSQL','Versionamento com Git e GitHub','Introdução a Docker',
      'Microserviços com Spring Cloud','Testes Automatizados com JUnit','Arquitetura Hexagonal',
      'Clean Code e SOLID','Integração com Google Calendar','Deploy com Docker Compose',
      'Spring Security com JWT','Integração de E-mail com Spring Mail','CI/CD com GitHub Actions',
      'Boas Práticas de Backend','Front-end Moderno com Angular','UX Básico para Devs','Node.js para APIs',
      'TypeScript Essencial','Introdução ao Cloud'
      ];
    v_zoom_id            TEXT;
    v_zoom_pwd           TEXT;
    v_present_code       TEXT;
    v_start              TIMESTAMP;
    v_end                TIMESTAMP;
    v_now                TIMESTAMP := now();
    v_random_days        INT;
    v_signed_pwd         TEXT;
  BEGIN
    RAISE NOTICE 'Start populating demo data...';
    FOR i IN 1..50
      LOOP
        v_user := uuid_generate_v4();
        INSERT INTO auth_user (id, email, password, role)
        VALUES (v_user, format('mentor.%s@example.com', i),
                '$2a$10$24Ywdl5tjDAJ1gdWCd1yd.VogOn4cuY1i/unIaDIpfZ.oJOUh0TQu', 'MENTOR')
        ON CONFLICT (email) DO NOTHING;

        v_mentor := uuid_generate_v4();

        INSERT INTO mentor (id, user_uuid, name, last_name, birth_date, social_medias, description, state,
                            nationality, active)
        VALUES (v_mentor,
                v_user,
                mentor_first_names[floor(random() * array_length(mentor_first_names, 1))::int + 1],
                mentor_last_names[floor(random() * array_length(mentor_last_names, 1))::int + 1],
                (date '1978-01-01' + (floor(random() * 8000))::int), -- random birthdate
                '@' || split_part(format('mentor.%s@example.com', i), '@', 1),
                'Mentor com experiência em desenvolvimento e ensino. Aulas práticas e focadas em resolução de problemas.',
                cities[floor(random() * array_length(cities, 1))::int + 1],
                'Brasil',
                true)
        ON CONFLICT DO NOTHING;

        FOR j IN 1..n_classes_per_mentor
          LOOP
            v_class := uuid_generate_v4();

            v_zoom_id := substr(md5(random()::text), 1, 10);
            v_zoom_pwd := substr(md5(random()::text || clock_timestamp()::text), 1, 8);

            IF j <= 2 THEN
              v_random_days := (floor(random() * 180))::int;
              v_start := (v_now - (interval '180 days')) + (v_random_days || ' days')::interval +
                         ((floor(random() * 6)) || ' hours')::interval;
            ELSIF j <= 4 THEN
              v_random_days := (floor(random() * 7))::int;
              v_start := v_now + (v_random_days || ' days')::interval +
                         ((9 + floor(random() * 8)) || ' hours')::interval;
            ELSE
              v_random_days := (floor(random() * 180))::int;
              v_start := v_now + (v_random_days || ' days')::interval +
                         ((10 + floor(random() * 6)) || ' hours')::interval;
            END IF;

            v_end := v_start + ((1 + floor(random() * 2)) || ' hours')::interval; -- 1-2 hours
            v_present_code := substr(md5(random()::text || clock_timestamp()::text), 1, 6);

            INSERT INTO class (id, title, description, link, max_guest, start_time, end_time, mentor_id,
                               present_code, external_event_id, created_at, updated_at)
            VALUES (v_class,
                    class_titles[floor(random() * array_length(class_titles, 1))::int + 1],
                    'Aula prática sobre ' ||
                    class_titles[floor(random() * array_length(class_titles, 1))::int + 1] ||
                    '. Exercícios e Q&A.',
                    format('https://zoom.us/j/%s?pwd=%s', v_zoom_id, v_zoom_pwd),
                    (20 + (floor(random() * 31))::int), -- 20..50
                    v_start,
                    v_end,
                    v_mentor,
                    v_present_code,
                    substr(md5(clock_timestamp()::text || random()::text), 1, 12),
                    now(),
                    NULL)
            ON CONFLICT DO NOTHING;

          END LOOP;
      END LOOP;

    RAISE NOTICE 'Mentors and classes created. Creating mentored users...';

    FOR i IN 1..300
      LOOP
        v_user := uuid_generate_v4();
        INSERT INTO auth_user (id, email, password, role)
        VALUES (v_user, format('mentorado.%s@example.com', i),
                '$2a$10$24Ywdl5tjDAJ1gdWCd1yd.VogOn4cuY1i/unIaDIpfZ.oJOUh0TQu',
                'MENTORED')
        ON CONFLICT (email) DO NOTHING;

        v_mentored := uuid_generate_v4();
        INSERT INTO mentored (id, user_uuid, name, last_name, birth_date, social_medias, description, state,
                              nationality)
        VALUES (v_mentored,
                v_user,
                mentored_first_names[floor(random() * array_length(mentored_first_names, 1))::int + 1],
                mentored_last_names[floor(random() * array_length(mentored_last_names, 1))::int + 1],
                (date '1995-01-01' + (floor(random() * 8000))::int),
                '@' || split_part(format('mentorado.%s@example.com', i), '@', 1),
                'Aluno interessado em aprender e aplicar conhecimento em projetos reais.',
                cities[floor(random() * array_length(cities, 1))::int + 1],
                'Brasil')
        ON CONFLICT DO NOTHING;
      END LOOP;

    RAISE NOTICE 'Mentorados created. Creating enrollments, certificates and reviews...';

    FOR i IN 1..300
      LOOP
        SELECT id INTO v_mentored FROM mentored OFFSET (i - 1) LIMIT 1;
        IF v_mentored IS NULL THEN
          CONTINUE;
        END IF;

        FOR k IN 1..(4 + (floor(random() * 5))::int)
          LOOP
            -- 4..8 enrollments
            SELECT id INTO v_class FROM class ORDER BY random() LIMIT 1;
            IF v_class IS NULL THEN
              CONTINUE;
            END IF;

            BEGIN
              INSERT INTO class_mentored (class_id, mentored_id, present_code_filled,
                                          certificate_generated, certificate_generated_at)
              VALUES (v_class, v_mentored, false, false, NULL);
            EXCEPTION
              WHEN unique_violation THEN
                -- already enrolled, skip
                CONTINUE;
            END;

            IF (SELECT end_time < now() FROM class WHERE id = v_class) THEN
              -- 60% chance they filled presence
              IF random() < 0.60 THEN
                UPDATE class_mentored
                SET present_code_filled = true
                WHERE class_id = v_class
                  AND mentored_id = v_mentored;
              END IF;

              IF (SELECT present_code_filled
                  FROM class_mentored
                  WHERE class_id = v_class
                    AND mentored_id = v_mentored) AND random() < 0.55 THEN
                BEGIN
                  INSERT INTO certificate (id, mentored_id, class_id)
                  VALUES (uuid_generate_v4(), v_mentored, v_class);
                  UPDATE class_mentored
                  SET certificate_generated    = true,
                      certificate_generated_at = now() - ((floor(random() * 120))::int || ' minutes')::interval
                  WHERE class_id = v_class
                    AND mentored_id = v_mentored;
                EXCEPTION
                  WHEN unique_violation THEN
                    -- already has certificate, skip
                    NULL;
                END;
              END IF;

              IF random() < 0.40 THEN
                -- get mentor of class
                DECLARE
                  v_mentor_of_class UUID;
                  v_didactics       INT := 1 + floor(random() * 5)::int;
                  v_subject         INT := 1 + floor(random() * 5)::int;
                  v_punctuality     INT := 1 + floor(random() * 5)::int;
                  v_communication   INT := 1 + floor(random() * 5)::int;
                  v_engagement      INT := 1 + floor(random() * 5)::int;
                  v_feedback        TEXT;
                BEGIN
                  SELECT mentor_id INTO v_mentor_of_class FROM class WHERE id = v_class;
                  v_feedback := CASE
                                  WHEN
                                    (v_didactics + v_subject + v_punctuality + v_communication + v_engagement) >=
                                    22 THEN
                                    'Excelente aula, conteúdo bem explicado e exercícios práticos.'
                                  WHEN
                                    (v_didactics + v_subject + v_punctuality + v_communication + v_engagement) >=
                                    15 THEN
                                    'Boa aula, teve pontos fortes, poderia aprofundar alguns temas.'
                                  ELSE
                                    'A aula deixou a desejar em alguns pontos. Sugiro mais exemplos práticos.'
                    END;

                  INSERT INTO mentor_review (id, mentor_id, mentored_id, didactics, subject_mastery,
                                             punctuality, communication, engagement, feedback)
                  VALUES (uuid_generate_v4(), v_mentor_of_class, v_mentored,
                          v_didactics, v_subject, v_punctuality, v_communication, v_engagement,
                          left(v_feedback || ' (gerado automaticamente)', 500));
                EXCEPTION
                  WHEN OTHERS THEN
                    NULL;
                END;
              END IF;
            END IF;
          END LOOP;
      END LOOP;

    RAISE NOTICE 'Enrollments, certificates and reviews created. Finalizing...';

    -- Insert additional certificates for past classes
    WITH past_classes AS (SELECT id
                          FROM class
                          WHERE end_time < now()
                          ORDER BY random()
                          LIMIT 30)
    INSERT
    INTO certificate (id, mentored_id, class_id)
    SELECT uuid_generate_v4(), cm.mentored_id, cm.class_id
    FROM class_mentored cm
           JOIN past_classes pc ON pc.id = cm.class_id
    WHERE cm.certificate_generated = false
      AND random() < 0.25
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Demo population complete.';
  END;
$$;
