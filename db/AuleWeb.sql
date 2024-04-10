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

-- fare il check sul corso: va messo solo quando la tipologia è lezione, esame e parziale
CREATE TABLE IF NOT EXISTS `Evento` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(60) not null,
    `orario_inizio` DATETIME NOT NULL,
    `orario_fine` DATETIME NOT NULL,
    `descrizione` TINYTEXT NOT NULL,
    `nome_organizzatore` VARCHAR(60) NOT NULL,
    `email_responsabile` VARCHAR(60) NOT NULL,
    `tipologia` ENUM ('lezione', 'esame', 'seminario', 'parziale', 'riunione', 'lauree', 'altro') NOT NULL,
    `id_master` INT UNSIGNED,
    `id_aula` INT UNSIGNED NOT NULL,
    `id_corso` INT UNSIGNED DEFAULT null,
    PRIMARY KEY (id),
    CHECK (`tipologia` IN ('lezione', 'esame','parziale') OR (`tipologia` IN ('seminario','riunione','lauree','altro') AND `id_corso` IS NULL)),
    FOREIGN KEY (id_aula)
        REFERENCES Aula (id),
    FOREIGN KEY (id_corso)
        REFERENCES Corso (id)
);

-- DATI DI PROVA
drop user 'auleWebUser'@'localhost';
CREATE USER 'auleWebUser'@'localhost' IDENTIFIED BY 'auleWebPassword';
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, REFERENCES, RELOAD on *.* TO 'auleWebUser'@'localhost' WITH GRANT OPTION;

INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('1', '1.1', 'Coppito', 'blocco 0', '1', '50', 'qualcuno@gmail.com', '4', '1', '');
INSERT INTO `auleweb`.`aula` (`id`, `nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES ('2', '0.2', 'Coppito', 'blocco 0', '0', '70', 'qualcuno@gmail.com', '4', '1', '');

INSERT INTO `auleweb`.`corso` (`id`, `nome`) VALUES ('1', 'AI');

INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_master`, `id_aula`, `id_corso`) VALUES ('1', 'lezioneSwa', '11/11/11 11:00', '11/11/11 12:00', 'AAA', 'AAA', 'AAA', 'lezione', '1', '1', '1');
INSERT INTO `auleweb`.`evento` (`id`, `nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_master`, `id_aula`, `id_corso`) VALUES ('2', 'lezioneWe', '12/11/11 11:00', '12/11/11 12:30', 'AAA', 'AAA', 'AAA', 'seminario', '1', '1', null);

INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('1', 'nomeGruppo', 'descrizioneeee');
INSERT INTO `auleweb`.`gruppo` (`id`, `nome`, `descrizione`) VALUES ('2', 'altro gruppo', 'altra descrizione');

INSERT INTO `auleweb`.`aula_gruppo` (`id`,`id_aula`,`id_gruppo`) VALUES ('1','1','1');