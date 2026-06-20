-- Статистика по всем рейсам

CREATE OR REPLACE FUNCTION FlightsStatistics(u_id TEXT, u_pass TEXT)
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
BEGIN
    auth_ok := authenticate_user(u_id, u_pass);
    IF NOT auth_ok THEN
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
    JOIN LATERAL CommonState(p.PlaneId, p.FlightId) s ON TRUE;
END;
$$;
