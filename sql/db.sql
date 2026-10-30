
DROP DATABASE IF EXISTS foundit_db;
CREATE DATABASE foundit_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE foundit_db;


CREATE TABLE User (
    user_id        INT          NOT NULL AUTO_INCREMENT,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) NOT NULL,
    contact_number VARCHAR(13)  NOT NULL,
    user_type      VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email UNIQUE (email),
    CONSTRAINT uq_user_id_type UNIQUE (user_id, user_type),
    CONSTRAINT ck_user_type CHECK (user_type IN ('Claimant', 'Reporter', 'Validator'))
) ENGINE=InnoDB;

CREATE TABLE Claimant (
    user_id   INT         NOT NULL,
    user_type VARCHAR(10) NOT NULL DEFAULT 'Claimant',
    CONSTRAINT pk_claimant PRIMARY KEY (user_id),
    CONSTRAINT ck_claimant_type CHECK (user_type = 'Claimant'),
    CONSTRAINT fk_claimant_user FOREIGN KEY (user_id, user_type)
        REFERENCES User (user_id, user_type)
) ENGINE=InnoDB;

CREATE TABLE Reporter (
    user_id   INT         NOT NULL,
    user_type VARCHAR(10) NOT NULL DEFAULT 'Reporter',
    CONSTRAINT pk_reporter PRIMARY KEY (user_id),
    CONSTRAINT ck_reporter_type CHECK (user_type = 'Reporter'),
    CONSTRAINT fk_reporter_user FOREIGN KEY (user_id, user_type)
        REFERENCES User (user_id, user_type)
) ENGINE=InnoDB;

CREATE TABLE Validator (
    user_id   INT         NOT NULL,
    user_type VARCHAR(10) NOT NULL DEFAULT 'Validator',
    CONSTRAINT pk_validator PRIMARY KEY (user_id),
    CONSTRAINT ck_validator_type CHECK (user_type = 'Validator'),
    CONSTRAINT fk_validator_user FOREIGN KEY (user_id, user_type)
        REFERENCES User (user_id, user_type)
) ENGINE=InnoDB;


CREATE TABLE Location (
    location_id   INT          NOT NULL AUTO_INCREMENT,
    location_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (location_id),
    CONSTRAINT uq_location_name UNIQUE (location_name)
) ENGINE=InnoDB;

CREATE TABLE Item (
    item_id          INT          NOT NULL AUTO_INCREMENT,
    location_id      INT          NOT NULL,
    reporter_id      INT          NOT NULL,
    item_name        VARCHAR(100) NOT NULL,
    item_description TEXT         NOT NULL,
    item_category    VARCHAR(50)  NOT NULL,
    item_type        VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT uq_item_id_type UNIQUE (item_id, item_type),
    CONSTRAINT ck_item_type CHECK (item_type IN ('FoundItem', 'LostItem')),
    CONSTRAINT fk_item_location FOREIGN KEY (location_id)
        REFERENCES Location (location_id),
    CONSTRAINT fk_item_reporter FOREIGN KEY (reporter_id)
        REFERENCES Reporter (user_id),
    INDEX idx_item_category (item_category),
    INDEX idx_item_name (item_name)
) ENGINE=InnoDB;

CREATE TABLE FoundItem (
    item_id   INT         NOT NULL,
    item_type VARCHAR(10) NOT NULL DEFAULT 'FoundItem',
    found_at  DATETIME    NOT NULL,
    CONSTRAINT pk_founditem PRIMARY KEY (item_id),
    CONSTRAINT ck_founditem_type CHECK (item_type = 'FoundItem'),
    CONSTRAINT fk_founditem_item FOREIGN KEY (item_id, item_type)
        REFERENCES Item (item_id, item_type),
    INDEX idx_found_at (found_at)
) ENGINE=InnoDB;

CREATE TABLE LostItem (
    item_id   INT         NOT NULL,
    item_type VARCHAR(10) NOT NULL DEFAULT 'LostItem',
    lost_at   DATETIME    NOT NULL,
    CONSTRAINT pk_lostitem PRIMARY KEY (item_id),
    CONSTRAINT ck_lostitem_type CHECK (item_type = 'LostItem'),
    CONSTRAINT fk_lostitem_item FOREIGN KEY (item_id, item_type)
        REFERENCES Item (item_id, item_type),
    INDEX idx_lost_at (lost_at)
) ENGINE=InnoDB;


CREATE TABLE ItemPhoto (
    item_photo_id INT  NOT NULL AUTO_INCREMENT,
    item_id       INT  NOT NULL,
    photo_url     TEXT NOT NULL,
    CONSTRAINT pk_itemphoto PRIMARY KEY (item_photo_id),
    CONSTRAINT fk_itemphoto_item FOREIGN KEY (item_id)
        REFERENCES Item (item_id)
) ENGINE=InnoDB;


CREATE TABLE ClaimRequest (
    claim_request_id INT         NOT NULL AUTO_INCREMENT,
    item_id          INT         NOT NULL,
    claimant_id      INT         NOT NULL,
    validator_id     INT         NOT NULL,
    request_date     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request_type     VARCHAR(17) NOT NULL,
    CONSTRAINT pk_claimrequest PRIMARY KEY (claim_request_id),
    CONSTRAINT uq_claim_id_type UNIQUE (claim_request_id, request_type),
    CONSTRAINT ck_claim_type CHECK (request_type IN ('VerifiedRequest', 'UnverifiedRequest')),
    CONSTRAINT fk_claim_item FOREIGN KEY (item_id)
        REFERENCES Item (item_id),
    CONSTRAINT fk_claim_claimant FOREIGN KEY (claimant_id)
        REFERENCES Claimant (user_id),
    CONSTRAINT fk_claim_validator FOREIGN KEY (validator_id)
        REFERENCES Validator (user_id)
) ENGINE=InnoDB;

CREATE TABLE VerifiedRequest (
    claim_request_id  INT         NOT NULL,
    request_type      VARCHAR(17) NOT NULL DEFAULT 'VerifiedRequest',
    verification_date DATETIME    NOT NULL,
    CONSTRAINT pk_verifiedrequest PRIMARY KEY (claim_request_id),
    CONSTRAINT ck_verified_type CHECK (request_type = 'VerifiedRequest'),
    CONSTRAINT fk_verified_claim FOREIGN KEY (claim_request_id, request_type)
        REFERENCES ClaimRequest (claim_request_id, request_type)
) ENGINE=InnoDB;

CREATE TABLE UnverifiedRequest (
    claim_request_id INT         NOT NULL,
    request_type     VARCHAR(17) NOT NULL DEFAULT 'UnverifiedRequest',
    rejection_reason TEXT        NULL,  
    CONSTRAINT pk_unverifiedrequest PRIMARY KEY (claim_request_id),
    CONSTRAINT ck_unverified_type CHECK (request_type = 'UnverifiedRequest'),
    CONSTRAINT fk_unverified_claim FOREIGN KEY (claim_request_id, request_type)
        REFERENCES ClaimRequest (claim_request_id, request_type)
) ENGINE=InnoDB;


INSERT INTO Location (location_id, location_name) VALUES
(1, 'Gisbert Library'),
(2, 'Finster Hall Lobby'),
(3, 'University Canteen'),
(4, 'Covered Court'),
(5, 'Main Gate Guardhouse'),
(6, 'Computer Laboratory 2');




INSERT INTO User (user_id, first_name, last_name, email, contact_number, user_type) VALUES
(1,  'AJ',       'Castro',     'aj.castro@addu.edu.ph',     '09171234501',   'Reporter'),
(2,  'Jose',     'Ramirez',    'jose.ramirez@addu.edu.ph',     '09171234502',   'Reporter'),
(3,  'Angela',   'Reyes',      'angela.reyes@addu.edu.ph',     '+639171234503', 'Reporter'),
(4,  'Paolo',    'Contis',   'paolo.contis@addu.edu.ph',   '09171234504',   'Reporter'),
(5,  'Kristine', 'Lim',        'kristine.lim@addu.edu.ph',     '09181234505',   'Claimant'),
(6,  'Miguel',   'Shawn',     'miguel.shawn@addu.edu.ph',    '09181234506',   'Claimant'),
(7,  'BJ',   'Tangaro',     'bj.tangaro@addu.edu.ph','+639181234507', 'Claimant'),
(8,  'Justin',   'Mendoza',    'justin.mendoza@addu.edu.ph',   '09181234508',   'Claimant'),
(9,  'Klent',    'Navarro',    'Klent.navarro@addu.edu.ph',    '09181234509',   'Claimant'),
(10, 'Ramon',    'Aquino',     'ramon.aquino@addu.edu.ph',     '09191234510',   'Validator'),
(11, 'Stephanie',   'Domingo',    'stephanie.domingo@addu.edu.ph',   '09191234511',   'Validator'),
(12, 'Anika',    'Fernandez',  'anika.fernandez@addu.edu.ph',  '+639191234512', 'Validator');



INSERT INTO Reporter  (user_id) VALUES (1), (2), (3), (4);
INSERT INTO Claimant  (user_id) VALUES (5), (6), (7), (8), (9);
INSERT INTO Validator (user_id) VALUES (10), (11), (12);


INSERT INTO Item (item_id, location_id, reporter_id, item_name, item_description, item_category, item_type) VALUES
(1,  3, 1, 'Black Umbrella',          'Black folding umbrella with a wooden handle and a small tear near one rib.',          'Umbrella',    'FoundItem'),
(2,  1, 2, 'Blue Tumbler',            'Matte blue 32 oz insulated tumbler with a dent on the base and a sticker of a cat.',  'Drinkware',   'FoundItem'),
(3,  2, 3, 'Scientific Calculator',   'Casio fx-991ES Plus with a scratched cover and initials "KL" written on the back.',   'Electronics', 'FoundItem'),
(4,  5, 1, 'Student ID with Lanyard', 'Student ID card on a green lanyard, found near the guardhouse.',                       'ID/Documents','FoundItem'),
(5,  6, 4, 'Wireless Earbuds Case',   'White charging case for wireless earbuds, with a small crack on the lid.',            'Electronics', 'FoundItem'),
(6,  4, 2, 'Gray Hoodie',             'Gray pullover hoodie, size M, with a university logo on the left chest.',             'Clothing',    'FoundItem'),
(7,  3, 3, 'Brown Leather Wallet',    'Brown bi-fold leather wallet with several cards inside and a worn corner.',           'Wallet/Bags', 'FoundItem'),
(8,  2, 4, 'House Keys',              'Set of three keys on a red keychain with a small bell.',                              'Keys',        'FoundItem'),
(9,  1, 1, 'Black Backpack',          'Black backpack with a blue zipper pull; contains notebooks and a pencil case.',       'Wallet/Bags', 'LostItem'),
(10, 6, 2, 'Laptop Charger',          'Silver 65W laptop charger with the cable wrapped in black tape near the plug.',       'Electronics', 'LostItem'),
(11, 4, 3, 'Eyeglasses',              'Black rectangular-frame eyeglasses in a gray hard case.',                             'Accessories', 'LostItem'),
(12, 2, 4, 'Blue Spiral Notebook',    'Blue spiral notebook with handwritten notes and a name label on the cover.',          'Stationery',  'LostItem');

INSERT INTO FoundItem (item_id, found_at) VALUES
(1, '2026-09-14 12:30:00'),
(2, '2026-09-15 15:10:00'),
(3, '2026-09-16 10:05:00'),
(4, '2026-09-18 07:45:00'),
(5, '2026-09-21 16:20:00'),
(6, '2026-09-23 18:00:00'),
(7, '2026-10-01 13:15:00'),
(8, '2026-10-05 09:30:00');

INSERT INTO LostItem (item_id, lost_at) VALUES
(9,  '2026-09-17 14:00:00'),
(10, '2026-09-25 11:40:00'),
(11, '2026-10-02 17:30:00'),
(12, '2026-10-08 08:50:00');

INSERT INTO ItemPhoto (item_photo_id, item_id, photo_url) VALUES
(1,  1,  '/uploads/items/item01_front.jpg'),
(2,  2,  '/uploads/items/item02_front.jpg'),
(3,  2,  '/uploads/items/item02_base.jpg'),
(4,  3,  '/uploads/items/item03_front.jpg'),
(5,  4,  '/uploads/items/item04_front.jpg'),
(6,  5,  '/uploads/items/item05_front.jpg'),
(7,  5,  '/uploads/items/item05_open.jpg'),
(8,  6,  '/uploads/items/item06_front.jpg'),
(9,  7,  '/uploads/items/item07_front.jpg'),
(10, 8,  '/uploads/items/item08_front.jpg'),
(11, 9,  '/uploads/items/item09_front.jpg'),
(12, 10, '/uploads/items/item10_front.jpg'),
(13, 11, '/uploads/items/item11_front.jpg'),
(14, 12, '/uploads/items/item12_cover.jpg'),
(15, 12, '/uploads/items/item12_inside.jpg');

INSERT INTO ClaimRequest (claim_request_id, item_id, claimant_id, validator_id, request_date, request_type) VALUES
(1,  1, 5, 10, '2026-09-15 09:00:00', 'VerifiedRequest'),
(2,  2, 6, 11, '2026-09-16 13:00:00', 'VerifiedRequest'),
(3,  3, 7, 10, '2026-09-17 09:30:00', 'VerifiedRequest'),
(4,  4, 8, 12, '2026-09-19 08:10:00', 'VerifiedRequest'),
(5,  7, 9, 11, '2026-10-02 10:00:00', 'VerifiedRequest'),
(6,  2, 5, 11, '2026-09-15 16:30:00', 'UnverifiedRequest'),
(7,  5, 6, 12, '2026-09-22 14:00:00', 'UnverifiedRequest'),
(8,  6, 7, 10, '2026-09-24 11:20:00', 'UnverifiedRequest'),
(9,  8, 8, 12, '2026-10-06 10:00:00', 'UnverifiedRequest'),
(10, 6, 9, 11, '2026-10-07 15:00:00', 'UnverifiedRequest');

INSERT INTO VerifiedRequest (claim_request_id, verification_date) VALUES
(1, '2026-09-15 10:30:00'),
(2, '2026-09-16 14:15:00'),
(3, '2026-09-17 11:00:00'),
(4, '2026-09-19 08:40:00'),
(5, '2026-10-02 10:45:00');

INSERT INTO UnverifiedRequest (claim_request_id, rejection_reason) VALUES
(6,  'Claimant could not describe the sticker on the tumbler or the dent on its base.'),
(7,  'Details given did not match the earbuds case; no proof of ownership shown onsite.'),
(8,  'Claimant presented no proof of ownership when asked onsite.'),
(9,  NULL),  
(10, NULL);  

SHOW TABLES;

SELECT * FROM User;
SELECT * FROM Claimant;
SELECT * FROM Reporter;
SELECT * FROM Validator;
SELECT * FROM Location;
SELECT * FROM Item;
SELECT * FROM FoundItem;
SELECT * FROM LostItem;
SELECT * FROM ItemPhoto;
SELECT * FROM ClaimRequest;
SELECT * FROM VerifiedRequest;
SELECT * FROM UnverifiedRequest;
