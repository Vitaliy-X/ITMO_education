-- Купить забронированное место

CREATE OR REPLACE FUNCTION BuyReserved(u_id TEXT, u_pass TEXT, f_id INTEGER, seat INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    auth_ok BOOLEAN;
    can_sell BOOLEAN;
BEGIN
    auth_ok := authenticate_user(u_id, u_pass);
    IF NOT auth_ok THEN
        RETURN FALSE;
    END IF;

    SELECT sell_allowed INTO can_sell
    FROM FlightsPermissions
    WHERE FlightId = f_id;

    IF NOT can_sell THEN
        RETURN FALSE;
    END IF;

    -- Проверяем права на бронь
    IF NOT EXISTS (
        SELECT 1 FROM Tickets
        WHERE FlightId = f_id AND SeatNo = seat
          AND UserId = u_id
          AND reserved_until > now()
          AND sold = FALSE
    ) THEN
        RETURN FALSE;
    END IF;

    UPDATE Tickets
    SET sold = TRUE, reserved_until = NULL
    WHERE FlightId = f_id AND SeatNo = seat;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;
