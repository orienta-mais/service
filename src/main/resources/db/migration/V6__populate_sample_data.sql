DO
$$
    DECLARE
        v_mentor_id UUID;
        v_mentored_id UUID;
        v_class_id UUID;
        v_interest_id UUID;
        v_user_id UUID;
        v_random_mentor UUID;
        v_random_class UUID;
        i INT;
        j INT;
        k INT;
        class_titles TEXT[] := ARRAY[
            'Introdução à Programação',
            'Lógica de Algoritmos',
            'Java para Iniciantes',
            'Python Básico',
            'Desenvolvimento Web com HTML e CSS',
            'JavaScript Essencial',
            'APIs REST com Spring Boot',
            'Banco de Dados PostgreSQL',
            'Versionamento com Git e GitHub',
            'Introdução a Docker',
            'Microserviços com Spring Cloud',
            'Testes Automatizados com JUnit',
            'Arquitetura Hexagonal na Prática',
            'Clean Code e SOLID',
            'Integração com Google Calendar',
            'Deploy com Docker Compose',
            'Spring Security com JWT',
            'Integração de E-mail com Spring Mail',
            'CI/CD com GitHub Actions',
            'Boas Práticas de Backend'
            ];
    BEGIN
        -- Garantir interesse de tecnologia
        INSERT INTO interests (id, name)
        VALUES (uuid_generate_v4(), 'Tecnologia')
        ON CONFLICT (name) DO NOTHING;
        SELECT id INTO v_interest_id FROM interests WHERE name = 'Tecnologia';

        -- Criar 100 mentores e suas aulas
        FOR i IN 1..100 LOOP
                v_user_id := uuid_generate_v4();
                INSERT INTO auth_user (id, email, password, role)
                VALUES (v_user_id, 'mentor' || i || '@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'MENTOR');

                v_mentor_id := uuid_generate_v4();
                INSERT INTO mentor (id, user_uuid, name, last_name, birth_date, social_medias, description, state, nationality)
                VALUES (
                           v_mentor_id,
                           v_user_id,
                           'Mentor' || i,
                           'Tech',
                           DATE '1985-01-01' + (i % 100),
                           '@mentor' || i,
                           'Especialista em tecnologia e programação.',
                           'São Paulo',
                           'Brasil'
                       );

                INSERT INTO mentor_interests (mentor_id, interest_id) VALUES (v_mentor_id, v_interest_id)
                ON CONFLICT DO NOTHING;

                -- Criar 20 aulas por mentor
                FOR j IN 1..20 LOOP
                        v_class_id := uuid_generate_v4();
                        INSERT INTO class (id, title, description, link, max_guest, start_time, end_time, mentor_id, present_code, external_event_id, created_at)
                        VALUES (
                                   v_class_id,
                                   class_titles[((j - 1) % array_length(class_titles, 1)) + 1],
                                   'Aula prática sobre ' || class_titles[((j - 1) % array_length(class_titles, 1)) + 1],
                                   'https://meet.google.com/' || substr(md5(random()::text), 1, 10),
                                   50,
                                   NOW() + ((i + j) || ' days')::interval,
                                   NOW() + ((i + j) || ' days 2 hours')::interval,
                                   v_mentor_id,
                                   substr(md5(random()::text), 1, 6),
                                   substr(md5(random()::text), 1, 12),
                                   NOW()
                               );

                        INSERT INTO class_interests (class_id, interest_id) VALUES (v_class_id, v_interest_id)
                        ON CONFLICT DO NOTHING;
                    END LOOP;
            END LOOP;

        -- Criar 700 mentorados
        FOR i IN 1..700 LOOP
                v_user_id := uuid_generate_v4();
                INSERT INTO auth_user (id, email, password, role)
                VALUES (v_user_id, 'mentorado' || i || '@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'MENTORED');

                v_mentored_id := uuid_generate_v4();
                INSERT INTO mentored (id, user_uuid, name, last_name, birth_date, social_medias, description, state, nationality)
                VALUES (
                           v_mentored_id,
                           v_user_id,
                           'Mentorado' || i,
                           'Dev',
                           DATE '2000-01-01' + (i % 200),
                           '@mentorado' || i,
                           'Aluno interessado em programação e tecnologia.',
                           'Rio de Janeiro',
                           'Brasil'
                       );

                INSERT INTO mentored_interests (mentored_id, interest_id) VALUES (v_mentored_id, v_interest_id)
                ON CONFLICT DO NOTHING;

                -- Cada mentorado inscrito em 5 a 10 aulas aleatórias
                FOR k IN 1..(5 + (random() * 5)::int) LOOP
                        SELECT id INTO v_random_class FROM class ORDER BY random() LIMIT 1;
                        BEGIN
                            INSERT INTO class_mentored (class_id, mentored_id)
                            VALUES (v_random_class, v_mentored_id);
                        EXCEPTION WHEN unique_violation THEN
                            -- ignora duplicatas
                            CONTINUE;
                        END;
                    END LOOP;
            END LOOP;
    END;
$$;
