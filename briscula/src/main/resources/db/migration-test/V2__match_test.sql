CREATE TABLE match (
  id VARCHAR PRIMARY KEY,
  type INT NOT NULL CHECK (type IN (2, 3, 4))
);

CREATE TABLE user_match (
  id VARCHAR PRIMARY KEY,
  user_id VARCHAR NOT NULL,
  match_id VARCHAR NOT NULL,
  points INT NOT NULL,
  winner BOOLEAN NOT NULL,
  group_number INT NOT NULL CHECK (group_number >= 0 AND group_number <= 2),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (match_id) REFERENCES match(id)
);

INSERT INTO match (id, type) VALUES ('1', 2);
INSERT INTO match (id, type) VALUES ('2', 3);
INSERT INTO match (id, type) VALUES ('3', 4);
INSERT INTO match (id, type) VALUES ('4', 2);
INSERT INTO match (id, type) VALUES ('5', 4);
INSERT INTO match (id, type) VALUES ('6', 3);
INSERT INTO match (id, type) VALUES ('7', 2);
INSERT INTO match (id, type) VALUES ('8', 4);
INSERT INTO match (id, type) VALUES ('9', 2);
INSERT INTO match (id, type) VALUES ('10', 3);
INSERT INTO match (id, type) VALUES ('11', 4);
INSERT INTO match (id, type) VALUES ('12', 2);
INSERT INTO match (id, type) VALUES ('13', 4);
INSERT INTO match (id, type) VALUES ('14', 3);
INSERT INTO match (id, type) VALUES ('15', 2);
INSERT INTO match (id, type) VALUES ('16', 4);
INSERT INTO match (id, type) VALUES ('17', 2);
INSERT INTO match (id, type) VALUES ('18', 3);
INSERT INTO match (id, type) VALUES ('19', 4);
INSERT INTO match (id, type) VALUES ('20', 2);
INSERT INTO match (id, type) VALUES ('21', 3);
INSERT INTO match (id, type) VALUES ('22', 4);
INSERT INTO match (id, type) VALUES ('23', 2);
INSERT INTO match (id, type) VALUES ('24', 3);
INSERT INTO match (id, type) VALUES ('25', 4);
INSERT INTO match (id, type) VALUES ('26', 2);
INSERT INTO match (id, type) VALUES ('27', 3);
INSERT INTO match (id, type) VALUES ('28', 4);
INSERT INTO match (id, type) VALUES ('29', 2);
INSERT INTO match (id, type) VALUES ('30', 4);

-- Match 1 (2 players, group = 0)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('1a', '123e4567-e89b-12d3-a456-426614174003', '1', 70, true, 0),
('1b', '123e4567-e89b-12d3-a456-426614174009', '1', 50, false, 0);

-- Match 2 (3 players, group = 0)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('2a', '123e4567-e89b-12d3-a456-426614174007', '2', 70, true, 0),
('2b', '123e4567-e89b-12d3-a456-42661417400d', '2', 30, false, 0),
('2c', '123e4567-e89b-12d3-a456-42661417400b', '2', 20, false, 0);

-- Match 3 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('3a', '123e4567-e89b-12d3-a456-426614174002', '3', 35, true, 1),
('3b', '123e4567-e89b-12d3-a456-42661417400a', '3', 35, true, 1),
('3c', '123e4567-e89b-12d3-a456-426614174006', '3', 25, false, 2),
('3d', '123e4567-e89b-12d3-a456-426614174008', '3', 25, false, 2);

-- Match 4 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('4a', '123e4567-e89b-12d3-a456-426614174004', '4', 65, true, 0),
('4b', '123e4567-e89b-12d3-a456-426614174005', '4', 55, false, 0);

-- Match 5 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('5a', '123e4567-e89b-12d3-a456-426614174007', '5', 65, true, 0),
('5b', '123e4567-e89b-12d3-a456-42661417400f', '5', 40, false, 0),
('5c', '123e4567-e89b-12d3-a456-42661417400c', '5', 15, false, 0);

-- Match 6 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('6a', '123e4567-e89b-12d3-a456-426614174003', '6', 40, true, 1),
('6b', '123e4567-e89b-12d3-a456-42661417400e', '6', 40, true, 1),
('6c', '123e4567-e89b-12d3-a456-42661417400b', '6', 20, false, 2),
('6d', '123e4567-e89b-12d3-a456-42661417400c', '6', 20, false, 2);

-- Match 7 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('7a', '123e4567-e89b-12d3-a456-426614174009', '7', 65, true, 0),
('7b', '123e4567-e89b-12d3-a456-42661417400a', '7', 55, false, 0);

-- Match 8 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('8a', '123e4567-e89b-12d3-a456-426614174006', '8', 50, true, 0),
('8b', '123e4567-e89b-12d3-a456-426614174002', '8', 40, false, 0),
('8c', '123e4567-e89b-12d3-a456-426614174007', '8', 30, false, 0);

-- Match 9 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('9a', '123e4567-e89b-12d3-a456-42661417400d', '9', 35, true, 1),
('9b', '123e4567-e89b-12d3-a456-42661417400f', '9', 35, true, 1),
('9c', '123e4567-e89b-12d3-a456-426614174008', '9', 25, false, 2),
('9d', '123e4567-e89b-12d3-a456-42661417400c', '9', 25, false, 2);

-- Match 10 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('10a', '123e4567-e89b-12d3-a456-426614174005', '10', 70, true, 0),
('10b', '123e4567-e89b-12d3-a456-42661417400a', '10', 50, false, 0);

-- Match 11 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('11a', '123e4567-e89b-12d3-a456-42661417400e', '11', 60, true, 0),
('11b', '123e4567-e89b-12d3-a456-426614174003', '11', 45, false, 0),
('11c', '123e4567-e89b-12d3-a456-426614174007', '11', 35, false, 0);

-- Match 12 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('12a', '123e4567-e89b-12d3-a456-426614174009', '12', 50, true, 1),
('12b', '123e4567-e89b-12d3-a456-42661417400a', '12', 50, true, 1),
('12c', '123e4567-e89b-12d3-a456-42661417400b', '12', 30, false, 2),
('12d', '123e4567-e89b-12d3-a456-426614174008', '12', 30, false, 2);

-- Match 13 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('13a', '123e4567-e89b-12d3-a456-42661417400d', '13', 75, true, 0),
('13b', '123e4567-e89b-12d3-a456-426614174005', '13', 40, false, 0);

-- Match 14 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('14a', '123e4567-e89b-12d3-a456-426614174006', '14', 65, true, 0),
('14b', '123e4567-e89b-12d3-a456-426614174002', '14', 55, false, 0),
('14c', '123e4567-e89b-12d3-a456-42661417400f', '14', 30, false, 0);

-- Match 15 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('15a', '123e4567-e89b-12d3-a456-426614174003', '15', 40, true, 1),
('15b', '123e4567-e89b-12d3-a456-42661417400e', '15', 40, true, 1),
('15c', '123e4567-e89b-12d3-a456-426614174004', '15', 25, false, 2),
('15d', '123e4567-e89b-12d3-a456-42661417400c', '15', 25, false, 2);

-- Match 16 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('16a', '123e4567-e89b-12d3-a456-42661417400a', '16', 80, true, 0),
('16b', '123e4567-e89b-12d3-a456-426614174008', '16', 50, false, 0);

-- Match 17 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('17a', '123e4567-e89b-12d3-a456-426614174007', '17', 70, true, 0),
('17b', '123e4567-e89b-12d3-a456-426614174002', '17', 40, false, 0),
('17c', '123e4567-e89b-12d3-a456-42661417400b', '17', 30, false, 0);

-- Match 18 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('18a', '123e4567-e89b-12d3-a456-42661417400d', '18', 50, true, 1),
('18b', '123e4567-e89b-12d3-a456-426614174005', '18', 50, true, 1),
('18c', '123e4567-e89b-12d3-a456-42661417400f', '18', 20, false, 2),
('18d', '123e4567-e89b-12d3-a456-426614174009', '18', 20, false, 2);

-- Match 19 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('19a', '123e4567-e89b-12d3-a456-42661417400e', '19', 70, true, 0),
('19b', '123e4567-e89b-12d3-a456-426614174006', '19', 60, false, 0);

-- Match 20 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('20a', '123e4567-e89b-12d3-a456-42661417400a', '20', 55, true, 0),
('20b', '123e4567-e89b-12d3-a456-426614174004', '20', 45, false, 0),
('20c', '123e4567-e89b-12d3-a456-426614174003', '20', 30, false, 0);

-- Match 21 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('21a', '123e4567-e89b-12d3-a456-426614174002', '21', 60, true, 1),
('21b', '123e4567-e89b-12d3-a456-42661417400c', '21', 60, true, 1),
('21c', '123e4567-e89b-12d3-a456-426614174008', '21', 30, false, 2),
('21d', '123e4567-e89b-12d3-a456-42661417400f', '21', 30, false, 2);

-- Match 22 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('22a', '123e4567-e89b-12d3-a456-426614174007', '22', 75, true, 0),
('22b', '123e4567-e89b-12d3-a456-42661417400d', '22', 40, false, 0);

-- Match 23 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('23a', '123e4567-e89b-12d3-a456-426614174005', '23', 65, true, 0),
('23b', '123e4567-e89b-12d3-a456-426614174009', '23', 55, false, 0),
('23c', '123e4567-e89b-12d3-a456-426614174003', '23', 20, false, 0);

-- Match 24 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('24a', '123e4567-e89b-12d3-a456-42661417400b', '24', 40, true, 1),
('24b', '123e4567-e89b-12d3-a456-426614174006', '24', 40, true, 1),
('24c', '123e4567-e89b-12d3-a456-426614174004', '24', 30, false, 2),
('24d', '123e4567-e89b-12d3-a456-42661417400e', '24', 30, false, 2);

-- Match 25 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('25a', '123e4567-e89b-12d3-a456-42661417400a', '25', 80, true, 0),
('25b', '123e4567-e89b-12d3-a456-426614174007', '25', 60, false, 0);

-- Match 26 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('26a', '123e4567-e89b-12d3-a456-42661417400d', '26', 75, true, 0),
('26b', '123e4567-e89b-12d3-a456-426614174002', '26', 50, false, 0),
('26c', '123e4567-e89b-12d3-a456-42661417400f', '26', 25, false, 0);

-- Match 27 (4 players, teams)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('27a', '123e4567-e89b-12d3-a456-426614174003', '27', 60, true, 1),
('27b', '123e4567-e89b-12d3-a456-426614174005', '27', 60, true, 1),
('27c', '123e4567-e89b-12d3-a456-42661417400a', '27', 30, false, 2),
('27d', '123e4567-e89b-12d3-a456-426614174008', '27', 30, false, 2);

-- Match 28 (2 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('28a', '123e4567-e89b-12d3-a456-42661417400c', '28', 70, true, 0),
('28b', '123e4567-e89b-12d3-a456-426614174006', '28', 55, false, 0);

-- Match 29 (3 players)
INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('29a', '123e4567-e89b-12d3-a456-426614174007', '29', 65, true, 0),
('29b', '123e4567-e89b-12d3-a456-42661417400d', '29', 45, false, 0),
('29c', '123e4567-e89b-12d3-a456-426614174002', '29', 40, false, 0);

INSERT INTO user_match (id, user_id, match_id, points, winner, group_number) VALUES
('30a', '123e4567-e89b-12d3-a456-42661417400b', '30', 55, true, 1),
('30b', '123e4567-e89b-12d3-a456-42661417400f', '30', 55, true, 1),
('30c', '123e4567-e89b-12d3-a456-426614174009', '30', 35, false, 2),
('30d', '123e4567-e89b-12d3-a456-42661417400c', '30', 35, false, 2);
