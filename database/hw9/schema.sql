-- DDL (Инициализация структуры базы данных Airline)

DROP TABLE IF EXISTS Flights CASCADE;
DROP TABLE IF EXISTS Seats CASCADE;
DROP TABLE IF EXISTS Tickets CASCADE;
DROP TABLE IF EXISTS Users CASCADE;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Таблица рейсов
CREATE TABLE IF NOT EXISTS Flights (
    FlightId INTEGER PRIMARY KEY,
    FlightTime TIMESTAMP WITH TIME ZONE NOT NULL,
    PlaneId INTEGER NOT NULL,
    manual_reservation_allowed BOOLEAN NOT NULL DEFAULT TRUE,
    manual_sell_allowed BOOLEAN NOT NULL DEFAULT TRUE
);

-- Таблица мест в самолёте
CREATE TABLE IF NOT EXISTS Seats (
    PlaneId INTEGER NOT NULL,
    SeatNo INTEGER NOT NULL,
    PRIMARY KEY (PlaneId, SeatNo)
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS Users (
    UserId TEXT PRIMARY KEY,
    pass_hash TEXT NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Представление для фактических разрешений
CREATE OR REPLACE VIEW FlightsPermissions AS
SELECT
    f.FlightId,
    f.FlightTime,
    f.PlaneId,
    (f.manual_reservation_allowed AND (f.FlightTime - now() > INTERVAL '3 days')) AS reservation_allowed,
    (f.manual_sell_allowed AND (f.FlightTime - now() > INTERVAL '3 hours')) AS sell_allowed
FROM Flights f;

-- Таблица состояния мест
CREATE TABLE IF NOT EXISTS Tickets (
    FlightId INTEGER NOT NULL,
    SeatNo INTEGER NOT NULL,
    UserId TEXT,
    reserved_until TIMESTAMP WITH TIME ZONE,
    sold BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (FlightId, SeatNo),
    FOREIGN KEY (FlightId) REFERENCES Flights(FlightId),
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);
