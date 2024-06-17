DROP DATABASE IF EXISTS `auleWeb`;
CREATE DATABASE IF NOT EXISTS `auleWeb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `auleWeb`;

CREATE TABLE IF NOT EXISTS `Aula` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) NOT NULL,
    `luogo` VARCHAR(60) NOT NULL,
    `edificio` VARCHAR(60) NOT NULL,
    `piano` VARCHAR(60) NOT NULL,
    `capienza` INT NOT NULL,
    `email_responsabile` VARCHAR(60) NOT NULL,
    `prese_elettriche` INT UNSIGNED NOT NULL,
    `prese_rete` INT UNSIGNED NOT NULL,
    `note` TINYTEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Attrezzatura` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `tipo` VARCHAR(60) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Aula_Attrezzatura` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_aula INT UNSIGNED NOT NULL,
    id_attrezzatura INT UNSIGNED NOT NULL,
    FOREIGN KEY (id_aula)
        REFERENCES Aula (id),
    FOREIGN KEY (id_attrezzatura)
        REFERENCES Attrezzatura (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Gruppo` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) NOT NULL,
    `descrizione` TINYTEXT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Aula_Gruppo` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_aula INT UNSIGNED NOT NULL,
    id_gruppo INT UNSIGNED NOT NULL,
    FOREIGN KEY (id_aula)
        REFERENCES Aula (id),
    FOREIGN KEY (id_gruppo)
        REFERENCES Gruppo (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Corso` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Ricorrenza` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `tipo` ENUM('giornaliera', 'settimanale', 'mensile') NOT NULL,
    `data_termine` DATETIME NOT NULL,
    PRIMARY KEY (id)
);

-- fare il check sul corso: va messo solo quando la tipologia è lezione, esame e parziale
CREATE TABLE IF NOT EXISTS `Evento` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) NOT NULL,
    `orario_inizio` DATETIME NOT NULL,
    `orario_fine` DATETIME NOT NULL,
    `descrizione` TINYTEXT NOT NULL,
    `nome_organizzatore` VARCHAR(60) NOT NULL,
    `email_responsabile` VARCHAR(60) NOT NULL,
    `tipologia` ENUM('lezione', 'esame', 'seminario', 'parziale', 'riunione', 'lauree', 'altro') NOT NULL,
    `id_master` INT UNSIGNED DEFAULT NULL,
    `id_aula` INT UNSIGNED NOT NULL,
    `id_corso` INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (id),
    CHECK ((`tipologia` IN ('lezione' , 'esame', 'parziale') AND `id_corso` IS NOT NULL)
        OR (`tipologia` IN ('seminario' , 'riunione', 'lauree', 'altro'))),
    FOREIGN KEY (id_aula)
        REFERENCES Aula (id),
    FOREIGN KEY (id_corso)
        REFERENCES Corso (id),
    FOREIGN KEY (id_master)
        REFERENCES Ricorrenza (id)
);

CREATE TABLE IF NOT EXISTS `Admin`(
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(60) NOT NULL,
    `password` VARCHAR(60) NOT NULL,
    `token` varchar(255),
    primary key(id)
);

-- DATI DI PROVA
drop user 'auleWebUser'@'localhost';
CREATE USER 'auleWebUser'@'localhost' IDENTIFIED BY 'auleWebPassword';
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, REFERENCES, RELOAD on *.* TO 'auleWebUser'@'localhost' WITH GRANT OPTION;

-- Inserimento AULE **************
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('1', 'C1.10', 'Coppito', 'Renato Ricamo', '0', '50', 'sandro@gmail.com', '9', '5', 'Nota1');
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('2', 'A1.1', 'Coppito', 'Blocco 0', '1', '65', 'mario@gmail.com', '20', '11', 'Nota2');
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('3', 'A1.6', 'Coppito', 'Blocco 0', '1', '55', 'giovanna@gmail.com', '15', '14', 'Nota3');
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('4', 'C1.16', 'Coppito', 'Renato Ricamo', '0', '35', 'gilda@gmail.com', '10', '7', 'Nota4');

-- Inserimento CORSI *********** 
INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('1', 'Artificial Intellgence');
INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('2', 'Data Analytics');
INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('3', 'Ingegneria del software');
INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('4', 'Software quality engineering');

-- Inserimento EVENTI ***************************
INSERT INTO Evento (nome, orario_inizio, orario_fine, descrizione, nome_organizzatore, email_responsabile, tipologia, id_master, id_aula, id_corso)
VALUES 
('Lezione di Matematica', '2024-06-15 09:00:00', '2024-06-15 11:00:00', 'Lezione sul calcolo differenziale', 'Prof. Mario Rossi', 'm.rossi@universita.it', 'lezione', NULL, 1, 2),
('Esame di Fisica', '2024-06-16 14:00:00', '2024-06-16 17:00:00', 'Esame finale di fisica generale', 'Prof.ssa Anna Bianchi', 'a.bianchi@universita.it', 'esame', NULL, 2, 2),
('Seminario di Informatica', '2024-06-17 10:00:00', '2024-06-17 12:00:00', 'Seminario su Intelligenza Artificiale', 'Prof. Luigi Verdi', 'l.verdi@universita.it', 'seminario', NULL, 3, NULL),
('Riunione del Dipartimento', '2024-06-18 09:30:00', '2024-06-18 11:00:00', 'Riunione mensile del dipartimento di ingegneria', 'Dott. Carlo Neri', 'c.neri@universita.it', 'riunione', NULL, 4, NULL),
('Parziale di Chimica', '2024-06-19 08:00:00', '2024-06-19 10:00:00', 'Parziale di chimica organica', 'Prof.ssa Laura Verde', 'l.verde@universita.it', 'parziale', NULL, 1, 2),
('Lauree di Giugno', '2024-06-20 09:00:00', '2024-06-20 18:00:00', 'Cerimonia di laurea per gli studenti del dipartimento di economia', 'Prof. Giovanni Bianchi', 'g.bianchi@universita.it', 'lauree', NULL, 2, NULL),
('Workshop di Fotografia', '2024-06-21 10:00:00', '2024-06-21 13:00:00', 'Workshop pratico di fotografia', 'Dott. Andrea Neri', 'a.neri@universita.it', 'altro', NULL, 3, NULL),
('Seminario di Economia', '2024-06-14 15:00:00', '2024-06-14 23:00:00', 'Conferenza sulle nuove tendenze economiche', 'Prof.ssa Elena Martini', 'e.martini@universita.it', 'seminario', NULL, 1, NULL);



-- Inserimento EVENTI RICORRENTI
INSERT INTO `auleweb`.`Ricorrenza` (`id`,`tipo`,`data_termine`) VALUES ('1', 'settimanale', '2024-06-30 23:59:00');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`, `id_master`) VALUES ('12', 'Lezione Ricorrente1', '2024/05/14 22:00', '2024/05/15 23:00', 'BBB', 'BBB', 'BBB', 'lezione', '1', 1, '1');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`, `id_master`) VALUES ('13', 'Lezione Ricorrente2', '2024/05/14 22:00', '2024/05/15 23:00', 'BBB', 'BBB', 'BBB', 'altro', '1', null, '1');

-- Inserimento GRUPPI
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('1', 'Gruppo1', 'Gruppo fantastico');
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('2', 'Gruppo2', 'Gruppo numeroso');
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('3', 'Gruppo3', 'Descrizione casuale');
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('4', 'Gruppo4', 'Descrizione casuale');

-- Associazione AULE-GRUPPI
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('1','1','1');
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('2','2','2');
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('3','3','3');
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('4','4','3');

-- Inserimento ATTREZZATURE
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('1','proiettore#1');
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('2','computer fisso#1');
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('3','condizionatore');
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('4','multipresa');

-- Associazione AULE-ATTREZZATURE
INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('1','1','1');
INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('2','1','2');
INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('3','2','3');

INSERT INTO `auleweb`.`admin` (`id`,`username`,`password`,`token`) VALUES ('1','username1','pass', null);
-- fine 
