-- Продлить бронь на сутки

CREATE OR REPLACE FUNCTION ExtendReservation(u_id TEXT, u_pass TEXT, f_id INTEGER, seat INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    auth_ok BOOLEAN;
BEGIN
    auth_ok := authenticate_user(u_id, u_pass);
    IF NOT auth_ok THEN
        RETURN FALSE;
    END IF;

    -- Проверка права на продление
    IF NOT EXISTS (
        SELECT 1 FROM Tickets
        WHERE FlightId = f_id AND SeatNo = seat
          AND UserId = u_id
          AND sold = FALSE
          AND reserved_until > now()
    ) THEN
        RETURN FALSE;
    END IF;

    UPDATE Tickets
    SET reserved_until = now() + INTERVAL '1 day'
    WHERE FlightId = f_id AND SeatNo = seat;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;
