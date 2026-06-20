-- Покупка свободного места (без аутентификации)

CREATE OR REPLACE FUNCTION BuyFree(f_id INTEGER, seat INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql AS
$$
DECLARE
    can_sell BOOLEAN;
BEGIN
    SELECT sell_allowed INTO can_sell
    FROM FlightsPermissions
    WHERE FlightId = f_id;

    IF NOT can_sell THEN
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

    -- В условии не передается user_id, поэтому null :((
    INSERT INTO Tickets(FlightId, SeatNo, UserId, reserved_until, sold)
    VALUES (f_id, seat, NULL, NULL, TRUE)
    ON CONFLICT (FlightId, SeatNo) DO UPDATE
    SET UserId = NULL,
        reserved_until = NULL,
        sold = TRUE;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;
