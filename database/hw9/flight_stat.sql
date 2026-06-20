-- статистика по рейсу

CREATE OR REPLACE FUNCTION FlightStat(u_id TEXT, u_pass TEXT, f_id INTEGER)
RETURNS TABLE(
    reservation_allowed BOOLEAN,
    sell_allowed BOOLEAN,
    free_count INTEGER,
    reserved_count INTEGER,
    sold_count INTEGER
)
LANGUAGE plpgsql AS
$$
DECLARE
    auth_ok BOOLEAN;
    plane INTEGER;
BEGIN
    auth_ok := authenticate_user(u_id, u_pass);
    IF NOT auth_ok THEN
        RETURN;
    END IF;

    SELECT f.PlaneId INTO plane FROM Flights f WHERE f.FlightId = f_id;

    IF plane IS NULL THEN
        RETURN;
    END IF;

    RETURN QUERY
    SELECT
        p.reservation_allowed,
        p.sell_allowed,
        s.free_count,
        s.reserved_count,
        s.sold_count
    FROM FlightsPermissions p
    JOIN LATERAL CommonState(plane, f_id) s ON TRUE
    WHERE p.FlightId = f_id;
END;
$$;
