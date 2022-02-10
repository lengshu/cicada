
CREATE TABLE MOVIE (
	TITLE VARCHAR(4096) NOT NULL,
	SITE VARCHAR(512) NOT NULL,
	PAGE_URL VARCHAR(4096) NOT NULL,
	MP4_FILE VARCHAR(4096),
	ACTOR VARCHAR(2048),
	TAG VARCHAR(4096),
	PRODUCER VARCHAR(512),
	UNI_ID VARCHAR(4096),
	UPDATE_TIME TIMESTAMP,
	IMAGE_URL VARCHAR(4096),
	CHANNEL VARCHAR(4096),
	CATEGORY VARCHAR(4096),
	PUBLISH_DATE DATE,
	MEMO VARCHAR(4096),
	DURATION TIMESTAMP,
	NAME VARCHAR(4096),
	DETAILED BOOLEAN DEFAULT FALSE,
	PREMIUM BOOLEAN DEFAULT FALSE,
	ALBUM_COUNT INTEGER DEFAULT 0 NOT NULL,
	SCORE INTEGER DEFAULT 0 NOT NULL,
	STATE INTEGER DEFAULT 0 NOT NULL,
	ID INTEGER  NOT NULL AUTO_INCREMENT,
	CONSTRAINT MOVIE_PK PRIMARY KEY (ID)
);

CREATE INDEX MOVIE_ACTOR_IDX ON MOVIE (ACTOR);
CREATE INDEX MOVIE_CATEGORY_IDX ON MOVIE (CATEGORY);
CREATE INDEX MOVIE_CHANNEL_IDX ON MOVIE (CHANNEL);
CREATE INDEX MOVIE_DETAILED_IDX ON MOVIE (DETAILED);
CREATE INDEX MOVIE_PREMIUM_IDX ON MOVIE (PREMIUM);
CREATE INDEX MOVIE_ID_IDX ON MOVIE (ID);
CREATE INDEX MOVIE_NAME_IDX ON MOVIE (NAME);
CREATE INDEX MOVIE_PAGE_URL_IDX ON MOVIE (PAGE_URL);
CREATE INDEX MOVIE_PRODUCER_IDX ON MOVIE (PRODUCER);
CREATE INDEX MOVIE_PUBLISH_DATE_IDX ON MOVIE (PUBLISH_DATE);
CREATE INDEX MOVIE_SITE_IDX ON MOVIE (SITE);
CREATE INDEX MOVIE_STATE_IDX ON MOVIE (STATE);
CREATE INDEX MOVIE_TAG_IDX ON MOVIE (TAG);
CREATE INDEX MOVIE_TITLE_IDX ON MOVIE (TITLE);
CREATE INDEX MOVIE_UNI_ID_IDX ON MOVIE (UNI_ID);
CREATE INDEX MOVIE_UPDATE_TIME_IDX ON MOVIE (UPDATE_TIME);

CREATE TABLE VISIT_HISTORY (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	SITE_NAME VARCHAR(256) NOT NULL,
	LAST_VISIT_PAGE_NUMBER INTEGER NOT NULL,
	LAST_TOTAL_PAGE_COUNT INTEGER NOT NULL,
	LAST_UPDATE DATE NOT NULL,
	LAST_PAGE_URLS VARCHAR(8192),
	CHANNEL VARCHAR(256) NOT NULL,
	FINISHED BOOLEAN DEFAULT FALSE,
	CONSTRAINT VISIT_HISTORY_PK PRIMARY KEY (ID)
);

CREATE INDEX VISIT_HISTORY_SITE_NAME_IDX ON VISIT_HISTORY (SITE_NAME);