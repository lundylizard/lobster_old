-- CREATE SETTINGS TABLE
CREATE TABLE guildSettings(
    guildId BIGINT NOT NULL PRIMARY KEY,
    lastChannelUsedId BIGINT NOT NULL DEFAULT -1,
    keepVolume BOOLEAN NOT NULL DEFAULT FALSE,
    volume SMALLINT NOT NULL DEFAULT 100,
    embedColor TEXT NOT NULL DEFAULT 'role',
    betaFeatures BOOLEAN NOT NULL DEFAULT FALSE,
    collectStatistics BOOLEAN NOT NULL DEFAULT TRUE,
    updateNotifications BOOLEAN NOT NULL DEFAULT TRUE,
    lastVersionUsed FLOAT NOT NULL DEFAULT 3.0,
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    bannedReason TEXT DEFAULT ''
);

CREATE TABLE userSettings(
    userId BIGINT NOT NULL PRIMARY KEY,

);

-- INSERT SETTINGS DEBUG DATA
INSERT INTO guildSettings (guildId) VALUES (0);

-- CREATE GLOBAL STATISTICS TABLE
CREATE TABLE guildStatistics(
    guildId BIGINT NOT NULL PRIMARY KEY,
    firstTimeUsed BIGINT NOT NULL DEFAULT -1,
    lastTimeUsed BIGINT NOT NULL DEFAULT -1,
    timeInVoiceChannel BIGINT NOT NULL DEFAULT -1
);

-- INSERT GUILD STATISTICS DEBUG DATA
INSERT INTO guildStatistics VALUES (0);

CREATE TABLE userStatistics(
    userId BIGINT NOT NULL PRIMARY KEY,

);

-- CREATE COMMAND HISTORY TABLE
CREATE TABLE commandHistory(
    guildId BIGINT NOT NULL PRIMARY KEY,
    commandText TEXT NOT NULL,
    timeUsed BIGINT NOT NULL
);

-- INSERT COMMAND HISTORY DEBUG DATA
INSERT INTO commandHistory VALUES (0, '/play search:test', 0);