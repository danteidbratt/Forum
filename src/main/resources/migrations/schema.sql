DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO donut;

CREATE TABLE user (
      uuid UUID PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      role VARCHAR(255) NOT NULL,
      created_at TIMESTAMP NOT NULL,
	  carma INTEGER NOT NULL,
      is_deleted BOOLEAN NOT NULL DEFAULT false
);


CREATE TABLE forum (
      uuid UUID PRIMARY KEY,
      created_by UUID NOT NULL,
      name VARCHAR(255) NOT NULL,
      description VARCHAR(255) NOT NULL,
      created_at TIMESTAMP NOT NULL,
      is_deleted BOOLEAN NOT NULL DEFAULT false,
	FOREIGN KEY (created_by) REFERENCES user (uuid)
);

CREATE TABLE subscription (
      uuid UUID PRIMARY KEY,
      poster_uuid UUID NOT NULL,
      forum_uuid UUID NOT NULL,
      created_at TIMESTAMP NOT NULL,
      is_deleted BOOLEAN NOT NULL DEFAULT false,
      FOREIGN KEY (forum_uuid) REFERENCES forum (uuid),
	FOREIGN KEY (poster_uuid) REFERENCES user (uuid)
);

CREATE TABLE entry (
      uuid UUID PRIMARY KEY,
      created_by UUID NOT NULL,
      content VARCHAR(255) NOT NULL,
      score INTEGER NOT NULL,
      type VARCHAR(255) NOT NULL,
      created_at TIMESTAMP NOT NULL,
      is_deleted BOOLEAN NOT NULL DEFAULT false,
	FOREIGN KEY (created_by) REFERENCES user (uuid)
);

CREATE TABLE comment (
      parent_entry_uuid UUID NOT NULL
) INHERITS (entry);

CREATE TABLE post (
      forum_uuid UUID NOT NULL,
      title VARCHAR(255) NOT NULL,
      link VARCHAR(255)
) INHERITS (entry);

CREATE TABLE vote (
    uuid UUID PRIMARY KEY,
    poster_uuid UUID NOT NULL,
    entry_uuid UUID NOT NULL,
    direction VARCHAR(8) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (entry_uuid) REFERENCES entry (uuid),
	FOREIGN KEY (poster_uuid) REFERENCES user (uuid)
);

CREATE TABLE direct_message (
	uuid UUID PRIMARY KEY,
	author_uuid UUID NOT NULL,
	receiver_uuid UUID NOT NULL,
	content VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	FOREIGN KEY (author_uuid) REFERENCES user (uuid),
	FOREIGN KEY (receiver_uuid) REFERENCES user (uuid)
);