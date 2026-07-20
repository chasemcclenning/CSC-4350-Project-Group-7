-- Embedded Libris schema and the current project data snapshot.
CREATE TABLE user (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'patron' CHECK (role IN ('patron', 'librarian', 'admin')),
    fines_owed DECIMAL(8,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE title (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    isbn VARCHAR(20) UNIQUE
);

CREATE TABLE copy (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    title_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'available'
        CHECK (status IN ('available', 'checked_out', 'reserved', 'lost', 'damaged')),
    `condition` VARCHAR(20) NOT NULL DEFAULT 'good'
        CHECK (`condition` IN ('new', 'good', 'fair', 'poor')),
    CONSTRAINT fk_copy_title FOREIGN KEY (title_id) REFERENCES title(id) ON DELETE RESTRICT
);

CREATE TABLE checkout (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    copy_id VARCHAR(36) NOT NULL,
    checked_out_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_at TIMESTAMP NOT NULL,
    returned_at TIMESTAMP,
    CONSTRAINT fk_checkout_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE RESTRICT,
    CONSTRAINT fk_checkout_copy FOREIGN KEY (copy_id) REFERENCES copy(id) ON DELETE RESTRICT
);

CREATE TABLE hold (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title_id VARCHAR(36) NOT NULL,
    queue_position INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'waiting'
        CHECK (status IN ('waiting', 'ready', 'claimed', 'expired', 'cancelled')),
    placed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    CONSTRAINT uq_hold_user_title UNIQUE (user_id, title_id),
    CONSTRAINT fk_hold_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_hold_title FOREIGN KEY (title_id) REFERENCES title(id) ON DELETE CASCADE
);

CREATE TABLE fine (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    checkout_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    amount DECIMAL(8,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'outstanding'
        CHECK (status IN ('outstanding', 'paid', 'waived')),
    CONSTRAINT fk_fine_checkout FOREIGN KEY (checkout_id) REFERENCES checkout(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fine_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE RESTRICT
);

CREATE TABLE audit_log (
    id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
);

CREATE INDEX idx_copy_title ON copy(title_id);
CREATE INDEX idx_checkout_user ON checkout(user_id);
CREATE INDEX idx_checkout_copy ON checkout(copy_id);
CREATE INDEX idx_hold_title ON hold(title_id);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_action ON audit_log(action);

INSERT INTO user VALUES ('2b52f38e-8226-11f1-b8bf-25cd99951fda','Mark Allen','mark1@example.com','libmember123','patron',0.00);
INSERT INTO user VALUES ('u-001','Alice Morgan','alice1@example.com','libmember1','patron',0.00);
INSERT INTO user VALUES ('u-002','Bob Chen','bob@example.com','hashed_pw_2','patron',0.00);
INSERT INTO user VALUES ('u-003','Carol Reyes','carol@example.com','librarian1','librarian',0.00);
INSERT INTO user VALUES ('u-004','David Park','david@example.com','hashed_pw_4','admin',0.00);
INSERT INTO user VALUES ('u-005','Emmanuel G','emmanuel@library.com','librarian2','librarian',0.00);
INSERT INTO user VALUES ('u-006','Librarian Sample','librarian@example.com','libexample1','librarian',0.00);
INSERT INTO user VALUES ('u-007','Member Sample','member@example.com','memexample1','patron',0.00);

INSERT INTO title VALUES ('43e0779e-8228-11f1-b8bf-25cd99951fda','HP','JKR','SF','12345');
INSERT INTO title VALUES ('t-001','The Pragmatic Programmer','David Thomas','Technology','978-0135957059');
INSERT INTO title VALUES ('t-002','Clean Code','Robert C. Martin','Technology','978-0132350884');
INSERT INTO title VALUES ('t-003','Dune','Frank Herbert','Science Fiction','978-0441013593');

INSERT INTO copy VALUES ('8ffae335-7ad2-4f18-9135-48babb610195','43e0779e-8228-11f1-b8bf-25cd99951fda','available','good');
INSERT INTO copy VALUES ('c-001','t-001','checked_out','good');
INSERT INTO copy VALUES ('c-002','t-001','checked_out','fair');
INSERT INTO copy VALUES ('c-003','t-002','checked_out','new');
INSERT INTO copy VALUES ('c-004','t-003','available','good');
INSERT INTO copy VALUES ('f061cb3e-9f65-424f-ad7f-667c0b8066dc','43e0779e-8228-11f1-b8bf-25cd99951fda','available','good');

INSERT INTO checkout VALUES ('27322b4c-822d-11f1-b8bf-25cd99951fda','2b52f38e-8226-11f1-b8bf-25cd99951fda','8ffae335-7ad2-4f18-9135-48babb610195','2026-07-17 18:16:26.0','2026-07-31 18:16:27.0','2026-07-17 18:21:01.0');
INSERT INTO checkout VALUES ('co-001','u-002','c-002','2026-06-27 10:56:58.0','2026-07-11 10:56:58.0',NULL);
INSERT INTO checkout VALUES ('co-002','u-001','8ffae335-7ad2-4f18-9135-48babb610195','2026-07-17 18:31:38.0','2026-07-31 18:31:38.0','2026-07-17 18:33:10.0');
INSERT INTO checkout VALUES ('co-003','2b52f38e-8226-11f1-b8bf-25cd99951fda','f061cb3e-9f65-424f-ad7f-667c0b8066dc','2026-07-17 18:32:53.0','2026-07-31 18:32:53.0','2026-07-17 18:33:04.0');
INSERT INTO checkout VALUES ('co-004','u-001','c-003','2026-07-17 18:34:04.0','2026-07-31 18:34:05.0',NULL);
INSERT INTO checkout VALUES ('co-005','u-002','c-001','2026-07-17 19:23:09.0','2026-07-31 19:23:09.0',NULL);
INSERT INTO checkout VALUES ('co-006','u-007','c-004','2026-07-17 19:24:02.0','2026-07-31 19:24:03.0','2026-07-17 19:24:12.0');
INSERT INTO checkout VALUES ('co-007','u-002','c-004','2026-07-17 19:26:53.0','2026-07-31 19:26:53.0','2026-07-17 19:27:00.0');
INSERT INTO checkout VALUES ('co-008','u-007','c-004','2026-07-17 19:28:11.0','2026-07-31 19:28:11.0','2026-07-17 19:28:16.0');

INSERT INTO hold VALUES ('5c22eb3e-8228-11f1-b8bf-25cd99951fda','2b52f38e-8226-11f1-b8bf-25cd99951fda','43e0779e-8228-11f1-b8bf-25cd99951fda',1,'cancelled','2026-07-17 17:42:08.0',NULL);
INSERT INTO hold VALUES ('h-001','u-001','t-001',1,'waiting','2026-06-27 10:56:58.0',NULL);
INSERT INTO hold VALUES ('h-002','u-007','t-002',1,'cancelled','2026-07-17 19:23:59.0',NULL);

INSERT INTO audit_log VALUES ('log-001','u-005','CHECKOUT','checkout','co-002','Book checked out to u-001 using copy 8ffae335-7ad2-4f18-9135-48babb610195','2026-07-17 18:31:38.0');
INSERT INTO audit_log VALUES ('log-002','u-005','CHECKOUT','checkout','co-003','Book checked out to 2b52f38e-8226-11f1-b8bf-25cd99951fda using copy f061cb3e-9f65-424f-ad7f-667c0b8066dc','2026-07-17 18:32:53.0');
INSERT INTO audit_log VALUES ('log-003','u-005','RETURN','checkout','co-003','Book returned','2026-07-17 18:33:04.0');
INSERT INTO audit_log VALUES ('log-004','u-005','RETURN','checkout','co-002','Book returned','2026-07-17 18:33:10.0');
INSERT INTO audit_log VALUES ('log-005','u-001','CHECKOUT','checkout','co-004','Book checked out to u-001 using copy c-003','2026-07-17 18:34:04.0');
INSERT INTO audit_log VALUES ('log-006','u-005','CREATE','user','u-006','Librarian account created','2026-07-17 19:05:30.0');
INSERT INTO audit_log VALUES ('log-007','u-005','CREATE','user','u-007','Member account created','2026-07-17 19:07:31.0');
INSERT INTO audit_log VALUES ('log-008','u-005','DELETE','title','43e0779e-8228-11f1-b8bf-25cd99951fda','Book title deleted','2026-07-17 19:08:51.0');
INSERT INTO audit_log VALUES ('log-009','u-006','CHECKOUT','checkout','co-005','Book checked out to Bob Chen','2026-07-17 19:23:09.0');
INSERT INTO audit_log VALUES ('log-010','u-007','CREATE','hold','h-002','Hold placed for u-007','2026-07-17 19:23:59.0');
INSERT INTO audit_log VALUES ('log-011','u-007','CHECKOUT','checkout','co-006','Book checked out to Member Sample','2026-07-17 19:24:02.0');
INSERT INTO audit_log VALUES ('log-012','u-007','RETURN','checkout','co-006','Book returned','2026-07-17 19:24:12.0');
INSERT INTO audit_log VALUES ('log-013','u-006','CHECKOUT','checkout','co-007','Book checked out to Bob Chen','2026-07-17 19:26:53.0');
INSERT INTO audit_log VALUES ('log-014','u-006','RETURN','checkout','co-007','Book returned','2026-07-17 19:27:00.0');
INSERT INTO audit_log VALUES ('log-015','u-007','CHECKOUT','checkout','co-008','Book checked out to Member Sample','2026-07-17 19:28:11.0');
INSERT INTO audit_log VALUES ('log-016','u-007','RETURN','checkout','co-008','Book returned','2026-07-17 19:28:16.0');
INSERT INTO audit_log VALUES ('log-017','u-007','CANCEL','hold','h-002','Hold cancelled','2026-07-17 19:28:21.0');
