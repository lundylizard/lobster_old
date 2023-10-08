CREATE TABLE settings(
    guildId BIGINT NOT NULL PRIMARY KEY,
    lastChannelUsedId BIGINT,
    keepVolume BOOLEAN NOT NULL DEFAULT FALSE,
    embedColor TEXT NOT NULL DEFAULT 'role',
    betaFeatures BOOLEAN NOT NULL DEFAULT FALSE,
    collectStatistics BOOLEAN NOT NULL DEFAULT TRUE,
    updateNotifications BOOLEAN NOT NULL DEFAULT TRUE
);