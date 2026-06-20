DROP TABLE IF EXISTS Runs;
DROP TABLE IF EXISTS Sessions;
DROP TABLE IF EXISTS Problems;
DROP TABLE IF EXISTS Contests;
DROP TABLE IF EXISTS Teams;

create table Teams (
  TeamId int primary key not null,
  TeamName varchar(30) not null      
);

create table Contests (
   ContestId int primary key not null,
   ContestName varchar(30) not null
);

create table Problems (
   Letter char(1) not null,
   ProblemName varchar(30) not null,
   ContestId int not null,
   primary key (Letter, ContestId),
   foreign key (ContestId) references Contests (ContestId)
);

create table Sessions (
  SessionId int not null,
  Start timestamp not null,
  ContestId int not null,
  TeamId int not null,
  primary key (SessionId),                   
  foreign key (ContestId) references Contests (ContestId),
  foreign key (TeamId) references Teams (TeamId)
);

create table Runs (
  RunId int not null,
  SubmitTime int not null,
  Accepted bool not null,
  Letter char(1) not null, ContestId int not null,
  SessionId int not null,
  primary key (RunId, SessionId),
  foreign key (Letter, ContestId) references Problems (Letter, ContestId),
  foreign key (SessionId) references Sessions (SessionId)
);
