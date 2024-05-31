DROP DATABASE IF EXISTS `auleWeb`;
CREATE DATABASE IF NOT EXISTS `auleWeb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `auleWeb`;

CREATE TABLE IF NOT EXISTS `Aula` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) NOT NULL,
    `luogo` VARCHAR(60) NOT NULL,
    `edificio` VARCHAR(60) NOT NULL,
    `piano` VARCHAR(60) NOT NULL,
    `capienza` INT DEFAULT 0,
    `email_responsabile` VARCHAR(60) NOT NULL,
    `prese_elettriche` INT UNSIGNED DEFAULT 0,
    `prese_rete` INT UNSIGNED DEFAULT 0,
    `note` TINYTEXT DEFAULT NULL,
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
    `descrizione` TINYTEXT DEFAULT NULL,
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
    `id_master` INT UNSIGNED,
    `id_aula` INT UNSIGNED NOT NULL,
    `id_corso` INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (id),
    CHECK (`tipologia` IN ('lezione' , 'esame', 'parziale')
        OR (`tipologia` IN ('seminario' , 'riunione', 'lauree', 'altro')
        AND `id_corso` IS NULL)),
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

INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('1', '1.1', 'Coppito', 'blocco 0', '1', '50', 'qualcuno@gmail.com', '4', '1', '');
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('2', '0.2', 'Coppito', 'blocco 0', '0', '70', 'qualcuno@gmail.com', '4', '1', '');

INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('1', 'AI');

INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('1', 'lezioneSwa', '11/11/11 11:00', '11/11/11 12:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', '1');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('2', 'lezioneWe', '12/11/11 11:00', '12/11/11 12:30', 'AAA', 'AAA', 'AAA', 'seminario', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('3', 'TerzaSettPrimo', '2024/01/16 11:00', '2024/01/16 13:00', 'AAA', 'AAA', 'AAA', 'seminario', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('4', 'TerzaSettSecondo', '2024/01/18 11:00', '2024/01/18 13:30', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('5', 'TerzaSettTerzo', '2024/01/20 11:00', '2024/01/20 12:00', 'AAA', 'AAA', 'AAA', 'seminario', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('6', 'A caso', '2023/01/11 09:00', '2023/01/11 09:30', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('7', 'A caso2', '2023/01/19 09:00', '2023/01/19 09:30', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('8', 'Attuale 1', '2024/05/14 09:00', '2024/05/15 09:00', 'AAA', 'AAA', 'AAA', 'lezione',  '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('9', 'Attuale 2', '2024/05/14 09:00', '2024/05/15 09:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('10', 'Prossimo 1', '2024/05/14 17:00', '2024/05/15 18:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ('11', 'Prossimo 2', '2024/05/14 22:00', '2024/05/15 23:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', null);

-- evento ricorrente
INSERT INTO `auleweb`.`Ricorrenza` (`id`,`tipo`,`data_termine`) VALUES ('1', 'settimanale', '2024-06-30 23:59:00');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`, `id_master`) VALUES ('12', 'Prossimo 2', '2024/05/14 22:00', '2024/05/15 23:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', null, '1');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`, `id_master`) VALUES ('13', 'Prossimo 2', '2024/05/14 22:00', '2024/05/15 23:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', null, '1');

INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('1', 'nomeGruppo', 'descrizioneeee');
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('2', 'altro gruppo', 'altra descrizione');

INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('1','1','1');
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('2','1','2');
INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('3','2','1');

INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('1','proiettore#1');
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('2','computer fisso#1');
INSERT INTO `auleweb`.`attrezzatura` (`id`,`tipo`) VALUES ('3','condizionatore');

INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('1','1','1');
INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('2','1','2');
INSERT INTO `auleweb`.`aula_attrezzatura` (`id`,`id_aula`,`id_attrezzatura`) VALUES ('3','2','3');

INSERT INTO `auleweb`.`admin` (`id`,`username`,`password`,`token`) VALUES ('1','username1','pass', null);

