CREATE TABLE game_types (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE games ADD game_type_id BIGINT;

ALTER TABLE games ADD CONSTRAINT fk_game_type FOREIGN KEY (game_type_id) REFERENCES game_types (id);

CREATE INDEX idx_games_game_type_id ON games (game_type_id);
