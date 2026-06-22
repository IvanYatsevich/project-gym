DELETE FROM trainings;
DELETE FROM trainees;
DELETE FROM trainers;
DELETE FROM users;
DELETE FROM training_types;

INSERT INTO training_types (training_type_id, training_type) VALUES (nextval('training_types_SEQ'), 'CARDIO');
INSERT INTO training_types (training_type_id, training_type) VALUES (nextval('training_types_SEQ'), 'STRENGTH');

INSERT INTO users (user_id, first_name, last_name, user_name, password, is_active) VALUES (nextval('users_SEQ'), 'Ivan', 'Ivanov', 'Ivan.Ivanov', 'IiPass12345', TRUE);
INSERT INTO users (user_id, first_name, last_name, user_name, password, is_active) VALUES (nextval('users_SEQ'), 'Petr', 'Petrov', 'Petr.Petrov', 'PpPass12345', TRUE);
INSERT INTO users (user_id, first_name, last_name, user_name, password, is_active) VALUES (nextval('users_SEQ'), 'Hulk', 'Hogan', 'Hulk.Hogan', 'HhPass12345', TRUE);
INSERT INTO users (user_id, first_name, last_name, user_name, password, is_active) VALUES (nextval('users_SEQ'), 'Arnold', 'Schwarzenegger', 'Arnold.Schwarzenegger', 'AsPass12345', TRUE);

INSERT INTO trainers (trainer_id, user_id, training_type_id) VALUES (nextval('trainers_SEQ'), (SELECT user_id FROM users WHERE user_name = 'Ivan.Ivanov'), (SELECT training_type_id FROM training_types WHERE training_type = 'CARDIO'));
INSERT INTO trainers (trainer_id, user_id, training_type_id) VALUES (nextval('trainers_SEQ'), (SELECT user_id FROM users WHERE user_name = 'Petr.Petrov'), (SELECT training_type_id FROM training_types WHERE training_type = 'STRENGTH'));

INSERT INTO trainees (trainee_id, user_id, date_of_birth, address) VALUES (nextval('trainees_SEQ'), (SELECT user_id FROM users WHERE user_name = 'Hulk.Hogan'), DATE '1995-04-21', 'New York, USA');
INSERT INTO trainees (trainee_id, user_id, date_of_birth, address) VALUES (nextval('trainees_SEQ'), (SELECT user_id FROM users WHERE user_name = 'Arnold.Schwarzenegger'), DATE '1998-10-05', 'Los Angeles, USA');

INSERT INTO trainings (training_id, trainer_id, trainee_id, training_type_id, training_name, training_date, training_duration) VALUES (nextval('trainings_SEQ'), (SELECT tr.trainer_id FROM trainers tr JOIN users u ON tr.user_id = u.user_id WHERE u.user_name = 'Ivan.Ivanov'), (SELECT t.trainee_id FROM trainees t JOIN users u ON t.user_id = u.user_id WHERE u.user_name = 'Hulk.Hogan'), (SELECT training_type_id FROM training_types WHERE training_type = 'CARDIO'), 'Morning Cardio', DATE '2026-06-10', 45);
INSERT INTO trainings (training_id, trainer_id, trainee_id, training_type_id, training_name, training_date, training_duration) VALUES (nextval('trainings_SEQ'), (SELECT tr.trainer_id FROM trainers tr JOIN users u ON tr.user_id = u.user_id WHERE u.user_name = 'Petr.Petrov'), (SELECT t.trainee_id FROM trainees t JOIN users u ON t.user_id = u.user_id WHERE u.user_name = 'Arnold.Schwarzenegger'), (SELECT training_type_id FROM training_types WHERE training_type = 'STRENGTH'), 'Strength Basics', DATE '2026-06-11', 90);

INSERT INTO trainings (training_id, trainer_id, trainee_id, training_type_id, training_name, training_date, training_duration) VALUES (nextval('trainings_SEQ'), (SELECT tr.trainer_id FROM trainers tr JOIN users u ON tr.user_id = u.user_id WHERE u.user_name = 'Ivan.Ivanov'), (SELECT t.trainee_id FROM trainees t JOIN users u ON t.user_id = u.user_id WHERE u.user_name = 'Arnold.Schwarzenegger'), (SELECT training_type_id FROM training_types WHERE training_type = 'CARDIO'), 'Evening Cardio', DATE '2026-06-12', 50);
INSERT INTO trainings (training_id, trainer_id, trainee_id, training_type_id, training_name, training_date, training_duration) VALUES (nextval('trainings_SEQ'), (SELECT tr.trainer_id FROM trainers tr JOIN users u ON tr.user_id = u.user_id WHERE u.user_name = 'Petr.Petrov'), (SELECT t.trainee_id FROM trainees t JOIN users u ON t.user_id = u.user_id WHERE u.user_name = 'Hulk.Hogan'), (SELECT training_type_id FROM training_types WHERE training_type = 'STRENGTH'), 'Upper Body Strength', DATE '2026-06-13', 70);