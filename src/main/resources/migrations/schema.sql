DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO donut;

CREATE TABLE users (
	uuid UUID PRIMARY KEY,
	name VARCHAR(31) UNIQUE NOT NULL,
	role VARCHAR(8) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	is_deleted BOOLEAN NOT NULL
);

CREATE TABLE vault (
	user_uuid UUID PRIMARY KEY,
	password VARCHAR(255) NOT NULL,
	FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);

CREATE TABLE submission (
	uuid UUID UNIQUE NOT NULL,
	author_uuid UUID NOT NULL,
	content VARCHAR(511) NOT NULL,
	score INTEGER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	is_deleted BOOLEAN NOT NULL
);

CREATE TABLE forum (
	name VARCHAR(31) NOT NULL,
	FOREIGN KEY (author_uuid) REFERENCES users (uuid),
	PRIMARY KEY (uuid)
) INHERITS (submission);

CREATE TABLE post (
	forum_uuid UUID NOT NULL,
	title VARCHAR(255) NOT NULL,
	comment_count INTEGER NOT NULL,
	FOREIGN KEY (author_uuid) REFERENCES users (uuid),
	FOREIGN KEY (forum_uuid) REFERENCES forum (uuid),
	PRIMARY KEY (uuid)
) INHERITS (submission);

CREATE TABLE comment (
	post_uuid UUID NOT NULL,
	parent_uuid UUID,
	FOREIGN KEY (author_uuid) REFERENCES users (uuid),
	FOREIGN KEY (post_uuid) REFERENCES post (uuid),
	PRIMARY KEY (uuid)
) INHERITS (submission);

CREATE TABLE subscription (
	user_uuid UUID NOT NULL,
	forum_uuid UUID NOT NULL,
	FOREIGN KEY (forum_uuid) REFERENCES forum (uuid),
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	PRIMARY KEY (user_uuid, forum_uuid)
);

CREATE TABLE vote (
	user_uuid UUID NOT NULL,
	target_uuid UUID NOT NULL,
	direction VARCHAR(4) NOT NULL
);

CREATE TABLE comment_vote (
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	FOREIGN KEY (target_uuid) REFERENCES comment (uuid),
	PRIMARY KEY (user_uuid, target_uuid)
) INHERITS (vote);

CREATE TABLE post_vote (
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	FOREIGN KEY (target_uuid) REFERENCES post (uuid),
	PRIMARY KEY (user_uuid, target_uuid)
) INHERITS (vote);