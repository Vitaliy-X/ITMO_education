-- DML

INSERT INTO
  Users (UserId, pass_hash, is_admin)
VALUES
  ('admin', crypt('pwd', gen_salt('bf')), TRUE),
  ('user1', crypt('pwd', gen_salt('bf')), FALSE),
  ('user2', crypt('pwd', gen_salt('bf')), FALSE);

INSERT INTO
  Seats(PlaneId, SeatNo)
VALUES
  (100, 1),
  (100, 2),
  (100, 3),
  (100, 4),
  (100, 5),
  (100, 6),
  (200, 2),
  (200, 3),
  (200, 54),
  (200, 56),
  (200, 71),
  (200, 72);

INSERT INTO
  Flights(FlightId, FlightTime, PlaneId)
VALUES
  (1, now() + INTERVAL '5 days', 200),
  (2, now() + INTERVAL '1 day', 100);
