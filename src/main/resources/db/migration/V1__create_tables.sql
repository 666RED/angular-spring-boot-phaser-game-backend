-- 1. Create the Users table
CREATE TABLE users(
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	provider VARCHAR(255),
	provider_id VARCHAR(255),
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL,
	deleted BOOLEAN DEFAULT FALSE
);
-- 2. Create the Games table
CREATE TABLE games(
	id BIGSERIAL PRIMARY KEY,
	round BIGINT NOT NULL DEFAULT 1,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL
);
-- 3. Create the User_Game junction table
CREATE TABLE user_game(
	user_id BIGINT NOT NULL,
	game_id BIGINT NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL, 
	CONSTRAINT pk_user_game PRIMARY KEY(user_id,
	game_id),
	CONSTRAINT fk_user_game_user FOREIGN KEY(user_id) REFERENCES users(id),
	CONSTRAINT fk_user_game_game FOREIGN KEY(game_id) REFERENCES games(id)
);
-- Indices for faster lookups
CREATE INDEX idx_user_game_game_id
ON user_game(game_id);
CREATE INDEX idx_user_game_user_id
ON user_game(user_id);
