DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO donut;

CREATE TABLE users (
	uuid UUID PRIMARY KEY,
	name VARCHAR(255) UNIQUE NOT NULL,
	role VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	carma INTEGER NOT NULL,
	is_deleted BOOLEAN NOT NULL
);

CREATE TABLE vault (
	user_uuid UUID PRIMARY KEY,
	password VARCHAR(255) NOT NULL,
	FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);

CREATE TABLE submission (
	author_uuid UUID NOT NULL,
	content VARCHAR(255) NOT NULL,
	score INTEGER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	is_deleted BOOLEAN NOT NULL
);

CREATE TABLE forum (
	uuid UUID PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	FOREIGN KEY (author_uuid) REFERENCES users (uuid)
) INHERITS (submission);

CREATE TABLE post (
	uuid UUID PRIMARY KEY,
	forum_uuid UUID NOT NULL,
	title VARCHAR(255) NOT NULL,
	link VARCHAR(255),
	FOREIGN KEY (author_uuid) REFERENCES users (uuid),
	FOREIGN KEY (forum_uuid) REFERENCES forum (uuid)
) INHERITS (submission);

CREATE TABLE comment (
	uuid UUID PRIMARY KEY,
	post_uuid UUID NOT NULL,
	parent_uuid UUID,
	FOREIGN KEY (author_uuid) REFERENCES users (uuid),
	FOREIGN KEY (post_uuid) REFERENCES post (uuid)
) INHERITS (submission);

CREATE TABLE subscription (
	user_uuid UUID NOT NULL,
	forum_uuid UUID NOT NULL,
	FOREIGN KEY (forum_uuid) REFERENCES forum (uuid),
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	PRIMARY KEY (user_uuid, forum_uuid),
	CONSTRAINT unique_user_forum UNIQUE (user_uuid, forum_uuid)
);

CREATE TABLE vote (
	user_uuid UUID NOT NULL,
	target_uuid UUID NOT NULL,
	target_parent_uuid UUID NOT NULL,
	direction VARCHAR(16) NOT NULL
);

CREATE TABLE comment_vote (
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	FOREIGN KEY (target_uuid) REFERENCES comment (uuid),
	FOREIGN KEY (target_parent_uuid) REFERENCES post (uuid),
	PRIMARY KEY (user_uuid, target_uuid)
) INHERITS (vote);

CREATE TABLE post_vote (
	FOREIGN KEY (user_uuid) REFERENCES users (uuid),
	FOREIGN KEY (target_uuid) REFERENCES post (uuid),
	FOREIGN KEY (target_parent_uuid) REFERENCES forum (uuid),
	PRIMARY KEY (user_uuid, target_uuid)
) INHERITS (vote);