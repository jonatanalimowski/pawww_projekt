-- DROP ROLE IF EXISTS projekt_admin;
-- DROP ROLE IF EXISTS projekt_uzytkownik_full;
-- DROP ROLE IF EXISTS projekt_uzytkownik_limited;

-- CREATE ROLE projekt_admin WITH LOGIN PASSWORD '123';
-- CREATE ROLE projekt_uzytkownik_full WITH LOGIN PASSWORD '123';
-- CREATE ROLE projekt_uzytkownik_limited WITH LOGIN PASSWORD '123';

GRANT CONNECT ON DATABASE "store_everything_DB" TO projekt_admin, projekt_uzytkownik_full, projekt_uzytkownik_limited;
GRANT USAGE ON SCHEMA public TO projekt_admin, projekt_uzytkownik_full, projekt_uzytkownik_limited;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO projekt_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO projekt_admin;

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE information, categories, shared_information TO projekt_uzytkownik_full;
GRANT SELECT ON TABLE uzytkownik TO projekt_uzytkownik_full;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO projekt_uzytkownik_full;

-- Tylko select udostepnionych dla limited user
GRANT SELECT ON TABLE information, shared_information TO projekt_uzytkownik_limited;
GRANT SELECT (id, username) ON TABLE uzytkownik TO projekt_uzytkownik_limited;
-- REVOKE SELECT ON TABLE uzytkownik FROM projekt_uzytkownik_limited;


ALTER TABLE information ENABLE ROW LEVEL SECURITY;
-- DROP POLICY IF EXISTS information_isolation_policy ON information;

-- Użytkownik widzi notatkę jesli:
-- a) Jest jej właścicielem
-- b) Notatka znajduje się w tabeli shared_information dla tego użytkownika
-- c) Jest administratorem
CREATE POLICY information_security_policy ON information
    FOR SELECT
    USING (
        current_user = 'projekt_admin' 
        OR
        EXISTS (
            SELECT 1 FROM uzytkownik u 
            WHERE u.username = current_user AND u.id = information.user_id
        )
        OR
        EXISTS (
            SELECT 1 FROM shared_information si
            JOIN uzytkownik u ON si.recipient_id = u.id
            WHERE u.username = current_user AND si.information_id = information.id
        )
    );

CREATE ROLE user1 WITH LOGIN PASSWORD 'root';
GRANT projekt_uzytkownik_full TO user1;

CREATE ROLE user2 WITH LOGIN PASSWORD 'root';
GRANT projekt_uzytkownik_limited TO user2;

SET ROLE user2;
SELECT * FROM information;
SELECT * FROM shared_information s_i
WHERE s_i.recipient_id = 3;
SELECT * FROM uzytkownik;
RESET ROLE;