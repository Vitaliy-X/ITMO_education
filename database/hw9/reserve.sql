-- Бронь на 1 сутки

CREATE OR REPLACE FUNCTION Reserve(u_id TEXT, u_pass TEXT, f_id INTEGER, seat INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    auth_ok BOOLEAN;
    can_reserve BOOLEAN;
BEGIN
    auth_ok := authenticate_user(u_id, u_pass);
    IF NOT auth_ok THEN
        RETURN FALSE;
    END IF;

    SELECT reservation_allowed INTO can_reserve
    FROM FlightsPermissions
    WHERE FlightId = f_id;

    IF NOT can_reserve THEN
        RETURN FALSE;
    END IF;

    -- Проверка, что место свободно
    IF EXISTS (
        SELECT 1 FROM Tickets
        WHERE FlightId = f_id AND SeatNo = seat
          AND (
               sold = TRUE OR
               (reserved_until IS NOT NULL AND reserved_until > now())
          )
    ) THEN
        RETURN FALSE;
    END IF;

    INSERT INTO Tickets(FlightId, SeatNo, UserId, reserved_until, sold)
    VALUES (f_id, seat, u_id, now() + INTERVAL '1 day', FALSE)
    ON CONFLICT (FlightId, SeatNo) DO UPDATE
    SET UserId = u_id,
        reserved_until = now() + INTERVAL '1 day',
        sold = FALSE;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;
