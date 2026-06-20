TRUNCATE TABLE Runs, Sessions, Problems, Contests, Teams RESTART IDENTITY CASCADE;

insert into Teams
  (TeamId, TeamName)
  values
    (1, '267'),
    (2, 'Zhenya Trasher'),
    (3, 'Pravoslavie');

insert into Contests
  (ContestId, ContestName)
  values
    (1, 'Petrozavodsk 2018'),
    (2, 'Goodbye Yellow Brick Road'),
    (3, 'Telegram Cup 2017'),        
    (4, 'ВКОШП');

insert into Problems
  (Letter, ProblemName, ContestId)
  values
    ('A', 'A+B', 1),
    ('A', 'A+B', 2),
    ('A', 'A+B', 3),
    ('A', 'A+B', 4),
    ('B', 'Enter the Goat into the Farm', 1),
    ('Z', 'Your Song', 2),
    ('B', 'Telemetric', 3),
    ('Я', 'Window Function', 4);

insert into Sessions
  (SessionId, Start, ContestId, TeamId)
  values
    (1, now() - interval '3 years', 1, 1),
    (2, now() - interval '4 years', 2, 2),
    (3, now() - interval '5 years', 3, 3);

insert into Runs
  (RunId, SubmitTime, Accepted, Letter, ContestId, SessionId)
  values
    (1, 3, false, 'A', 1, 1),
    (3, 128, true, 'B', 1, 1),
    (2, 128, true, 'B', 1, 1);
