-- Список свободных мест на рейс (непроданных и незабронированных)

CREATE OR REPLACE FUNCTION FreeSeats(target_flight INTEGER)
RETURNS TABLE(SeatNo INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Проверка, что бронирование или продажа разрешены
    IF NOT EXISTS (
        SELECT 1
        FROM FlightsPermissions fp
        WHERE fp.FlightId = target_flight
          AND (fp.reservation_allowed OR fp.sell_allowed)
    ) THEN
        RETURN;
    END IF;

    RETURN QUERY
    SELECT s.SeatNo
    FROM Seats s
    WHERE s.PlaneId = (
        SELECT PlaneId FROM Flights WHERE FlightId = target_flight
    )
    AND NOT EXISTS (
        SELECT 1
        FROM Tickets t
        WHERE t.FlightId = target_flight AND t.SeatNo = s.SeatNo
          AND (
               t.sold = TRUE
               OR (t.reserved_until IS NOT NULL AND t.reserved_until > now())
          )
    );
END;
$$;
