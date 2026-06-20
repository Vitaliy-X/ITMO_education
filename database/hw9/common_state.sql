-- Вспомогательная функция для FlightStat и FlightsStatistics

CREATE OR REPLACE FUNCTION CommonState(plane_id INTEGER, flight_id INTEGER)
RETURNS TABLE(
    free_count INTEGER,
    reserved_count INTEGER,
    sold_count INTEGER
)
LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
    SELECT
        -- free_count
        (
            SELECT COUNT(*)::integer
            FROM Seats s
            WHERE s.PlaneId = plane_id
              AND NOT EXISTS (
                    SELECT 1 FROM Tickets t
                    WHERE t.FlightId = flight_id AND t.SeatNo = s.SeatNo
                      AND (
                           t.sold = TRUE OR
                           (t.reserved_until IS NOT NULL AND t.reserved_until > now())
                      )
              )
        ),
        -- reserved_count
        (
            SELECT COUNT(*)::integer
            FROM Tickets t
            WHERE t.FlightId = flight_id AND t.sold = FALSE AND t.reserved_until > now()
        ),
        -- sold_count
        (
            SELECT COUNT(*)::integer
            FROM Tickets t
            WHERE t.FlightId = flight_id AND t.sold = TRUE
        );
END;
$$;
