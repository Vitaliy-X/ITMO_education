-- Админиские функции для управления пользователями и рейсами

CREATE OR REPLACE FUNCTION authenticate_user(u_id TEXT, u_pass TEXT)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    p_hash TEXT;
BEGIN
    SELECT pass_hash INTO p_hash FROM Users WHERE UserId = u_id;
    IF p_hash IS NULL THEN
        RETURN FALSE;
    END IF;

    RETURN crypt(u_pass, p_hash) = p_hash;
END;
$$;

CREATE OR REPLACE FUNCTION RegisterUser(new_user TEXT, new_pass TEXT)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
BEGIN
    IF new_user IS NULL OR trim(new_user) = '' OR new_pass IS NULL OR new_pass = '' THEN
        RETURN FALSE;
    END IF;

    IF EXISTS (SELECT 1 FROM Users WHERE UserId = new_user) THEN
        RETURN FALSE;
    END IF;

    INSERT INTO Users(UserId, pass_hash, is_admin, created_at)
    VALUES (new_user, crypt(new_pass, gen_salt('bf')), FALSE, now());

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;

CREATE OR REPLACE FUNCTION ManageFlight(
    admin_id TEXT,
    admin_pass TEXT,
    target_flight INTEGER,
    set_sell BOOLEAN,
    set_reserve BOOLEAN
) RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    valid_user BOOLEAN;
    admin_rights BOOLEAN;
BEGIN
    valid_user := authenticate_user(admin_id, admin_pass);
    IF NOT valid_user THEN
        RETURN FALSE;
    END IF;

    SELECT is_admin INTO admin_rights FROM Users WHERE UserId = admin_id;
    IF admin_rights IS DISTINCT FROM TRUE THEN
        RETURN FALSE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Flights WHERE FlightId = target_flight) THEN
        RETURN FALSE;
    END IF;

    UPDATE Flights
    SET manual_sell_allowed = set_sell,
        manual_reservation_allowed = set_reserve
    WHERE FlightId = target_flight;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;
