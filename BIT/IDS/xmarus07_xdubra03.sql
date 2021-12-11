---drops---
DROP TABLE dodavatel CASCADE CONSTRAINT;
DROP TABLE zahranicny CASCADE CONSTRAINT;
DROP TABLE miestny CASCADE CONSTRAINT;
DROP TABLE zem_povodu CASCADE CONSTRAINT;
DROP TABLE zamestnanec CASCADE CONSTRAINT;
DROP TABLE objednavka CASCADE CONSTRAINT;
DROP TABLE druh_syra CASCADE CONSTRAINT;
DROP TABLE bochnik CASCADE CONSTRAINT;
DROP TABLE dodavatel_druh_syra CASCADE CONSTRAINT;
DROP TABLE objednavka_druh_syra CASCADE CONSTRAINT;
DROP SEQUENCE id_zamestnanca_seq;
DROP SEQUENCE id_objednavky_seq;
DROP MATERIALIZED VIEW syr_bochnik;

-- ======================== VYTVARANIE TABULIEK ================================
-- =============================================================================

CREATE TABLE dodavatel(
    ico varchar(8) NOT NULL,
    nazov varchar2(64) NOT NULL,
    ulica varchar2(64) ,
    mesto varchar2(64) NOT NULL,
    psc   varchar2(64) NOT NULL,
    telefon varchar2(64) NOT NULL,
    email varchar2(256) 
);

CREATE TABLE zahranicny(
   ico varchar(8) NOT NULL,
	 clenstvo_eu CHAR(1) NOT NULL,
	 mena varchar2(3) NOT NULL,
	 minimalne_mnozstvo numeric(8,2)
);

CREATE TABLE miestny(
	 ico varchar(8) NOT NULL,
	 region varchar2(64) NOT NULL
);

CREATE TABLE zem_povodu(
	nazov varchar2(64) NOT NULL,
	popis varchar2(1024) NOT NULL
);

CREATE TABLE zamestnanec(
	id_zamestnanca integer NOT NULL,
	meno varchar2(64) NOT NULL,
	priezvisko varchar2(64) NOT NULL,
	ulica varchar2(64),
	mesto varchar2(64) NOT NULL,
	psc varchar2(64) NOT NULL,
	telefon varchar2(64) NOT NULL,
	email varchar2(255),
	funkcia varchar2(64) NOT NULL
);

CREATE TABLE objednavka(
	id_objednavky integer NOT NULL,
    stav varchar2(64) NOT NULL,
	datum_vytvorenia timestamp NOT NULL,
	miesto_dodania varchar2(64) NOT NULL,
	id_zamestnanca integer NOT NULL,
	ico varchar(8) NOT NULL
);

CREATE TABLE druh_syra(
	nazov varchar2(64) NOT NULL,
	typ varchar2(64) NOT NULL,
	zivocich varchar2(64) NOT NULL,
	percento_tuku numeric(6,2) NOT NULL,
	nazov_zeme_povodu varchar2(64) NOT NULL
);

CREATE TABLE bochnik(
	poradove_cislo integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	druh_syra varchar2(64) NOT NULL,
	pociatocna_hmotnost numeric(8,2) NOT NULL,
	aktualna_hmotnost numeric(8,2) NOT NULL,
	datum_dodania timestamp NOT NULL,
	trvanlivost date NOT NULL,
	aktualne_umiestnenie varchar2(64) NOT NULL,
  id_objednavky integer NOT NULL
);

CREATE TABLE dodavatel_druh_syra(
	nazov_druh_syra varchar2(64) NOT NULL,
	ico varchar(8) NOT NULL
);


CREATE TABLE objednavka_druh_syra(
	id_objednavky integer NOT NULL,
	druh_syra varchar(64) NOT NULL,
	hmotnost numeric(8,2) NOT NULL
);

-- ======================== KONIEC VYTVARANIA TABULIEK =========================
-- =============================================================================

-- ======================== PRIDAVANIE INTEGRITNYCH OBMEDZENI ==================
-- =============================================================================

ALTER TABLE dodavatel ADD CONSTRAINT pk_ico_dodavatel PRIMARY KEY (ico);

ALTER TABLE zahranicny ADD CONSTRAINT pk_ico_zahranicny PRIMARY KEY (ico);

ALTER TABLE miestny ADD CONSTRAINT pk_ico_miestny PRIMARY KEY (ico);

ALTER TABLE zem_povodu ADD CONSTRAINT pk_nazov_zem_povodu PRIMARY KEY (nazov);

ALTER TABLE zamestnanec ADD CONSTRAINT pk_id_zamestnanca PRIMARY KEY (id_zamestnanca);

ALTER TABLE objednavka ADD CONSTRAINT pk_id_objednavky PRIMARY KEY (id_objednavky);

ALTER TABLE druh_syra ADD CONSTRAINT pk_nazov_druh_syra PRIMARY KEY (nazov);


ALTER TABLE bochnik ADD CONSTRAINT pk_bochnik PRIMARY KEY (poradove_cislo, druh_syra);

ALTER TABLE dodavatel_druh_syra ADD CONSTRAINT pk_dodavatel_druh_syra PRIMARY KEY (nazov_druh_syra,ico);

ALTER TABLE objednavka_druh_syra ADD CONSTRAINT pk_objednavka_druh_syra PRIMARY KEY(id_objednavky,druh_syra); 


---set foreign keys

---bochnik
ALTER TABLE bochnik ADD CONSTRAINT fk_id_objednavky_bochnik FOREIGN KEY (id_objednavky) REFERENCES objednavka(id_objednavky);

ALTER TABLE bochnik ADD CONSTRAINT fk_druh_syra_bochnik FOREIGN KEY (druh_syra) REFERENCES druh_syra(nazov);


---objednavka druh syra

ALTER TABLE objednavka_druh_syra ADD CONSTRAINT fk_id_objednavky_obj_druh FOREIGN KEY (id_objednavky) REFERENCES objednavka(id_objednavky);

ALTER TABLE objednavka_druh_syra ADD CONSTRAINT fk_druh_objednavka_druh FOREIGN KEY (druh_syra) REFERENCES druh_syra(nazov);

--dodavatel druh syra

ALTER TABLE dodavatel_druh_syra ADD CONSTRAINT fk_nazov_dodavatel_druh FOREIGN KEY (nazov_druh_syra) REFERENCES druh_syra(nazov);

ALTER TABLE dodavatel_druh_syra ADD CONSTRAINT fk_ico_dodavatel_druh FOREIGN KEY (ico) REFERENCES dodavatel(ico);

--druh syra

ALTER TABLE druh_syra ADD CONSTRAINT fk_zem_povodu FOREIGN KEY (nazov_zeme_povodu) REFERENCES zem_povodu(nazov);

--objednavka

ALTER TABLE objednavka ADD CONSTRAINT fk_id_zamestnanca FOREIGN KEY (id_zamestnanca) REFERENCES zamestnanec(id_zamestnanca);

ALTER TABLE objednavka ADD CONSTRAINT fk_ico_objednavka FOREIGN KEY (ico) REFERENCES dodavatel(ico);

--zahranicny

ALTER TABLE zahranicny ADD CONSTRAINT fk_ico_zahranicny FOREIGN KEY (ico) REFERENCES dodavatel(ico);

--miestny

ALTER TABLE miestny ADD CONSTRAINT fk_ico_miestny FOREIGN KEY (ico) REFERENCES dodavatel(ico);


--- add checks 
ALTER TABLE zahranicny ADD CONSTRAINT ch_eu_clen CHECK(clenstvo_eu in ('A','N'));
ALTER TABLE zahranicny ADD CONSTRAINT ch_min_mnozstvo CHECK(minimalne_mnozstvo > 0);
ALTER TABLE objednavka ADD CONSTRAINT ch_miesto_dodania CHECK(miesto_dodania IN ('predajna','sklad'));
ALTER TABLE druh_syra ADD CONSTRAINT ch_percento_tuku CHECK(percento_tuku >= 0);
ALTER TABLE objednavka ADD CONSTRAINT ch_stav CHECK(stav IN('zadana','zrusena','zamietnuta','vybavena'));
ALTER TABLE bochnik ADD CONSTRAINT ch_aktualne_umiestnenie CHECK(aktualne_umiestnenie IN ('predajna','sklad'));
ALTER TABLE bochnik ADD CONSTRAINT ch_pociatocna_hmotnost CHECK(pociatocna_hmotnost > 0);
ALTER TABLE bochnik ADD CONSTRAINT ch_aktualna_hmotnost CHECK(aktualna_hmotnost >= 0);


-- ======================== KONIEC PRIDAVANIA INTEGRITNYCH OBMEDZENI ===========
-- =============================================================================
  
-- ======================== TRIGGERY============================================
-- =============================================================================

-- vytvotenie skevencie pre generovanie ID zamestanca a objednavky, ktora je pouzita v triggeroch
CREATE SEQUENCE id_zamestnanca_seq 
  START WITH 1 
  INCREMENT BY 1;
  
CREATE SEQUENCE id_objednavky_seq
  START WITH 1 
  INCREMENT BY 1;

-- trigger pre automaticke generovanie id zamestnanca a objednavky pri inserte, ak je hodnota noveho id NULL
CREATE OR REPLACE TRIGGER generuj_id_zamestnanca_ak_null
  BEFORE INSERT ON zamestnanec
  FOR EACH ROW
WHEN (NEW.id_zamestnanca is NULL)
BEGIN
  :NEW.id_zamestnanca := id_zamestnanca_seq.nextval;
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER generuj_id_objednavky_ak_null
  BEFORE INSERT ON objednavka
  FOR EACH ROW
WHEN (NEW.id_objednavky is NULL)
BEGIN
  :NEW.id_objednavky := id_objednavky_seq.nextval;
END;
/
SHOW ERRORS;


-- trigger pre kontrolu IČA
CREATE OR REPLACE TRIGGER kontrola_ico
  BEFORE INSERT OR UPDATE OF ico ON dodavatel
  FOR EACH ROW
DECLARE
  suma integer;
  kontrolna_cislica integer;
BEGIN

  -- kontrola, ze retazez obsahuje len cislice 0-9
  IF NOT REGEXP_LIKE(:NEW.ico,'^[0-9]+$') THEN
    RAISE_APPLICATION_ERROR(-20000, 'ICO musi obsahovat len cislice 0-9!');
  END IF;
  
  -- kontola dlzky ica
  IF LENGTH(:NEW.ico) != 8 THEN
    RAISE_APPLICATION_ERROR(-20001, 'ICO musi obsahovat 8 cislic v rozsahu 0-9!');
  END IF;
  
  -- vypocet kontrolnej cislice ica
  suma := 8*TO_NUMBER(SUBSTR(:NEW.ico,1,1)) + 7*TO_NUMBER(SUBSTR(:NEW.ico,2,1)) +
          6*TO_NUMBER(SUBSTR(:NEW.ico,3,1)) + 5*TO_NUMBER(SUBSTR(:NEW.ico,4,1)) +
          4*TO_NUMBER(SUBSTR(:NEW.ico,5,1)) + 3*TO_NUMBER(SUBSTR(:NEW.ico,6,1)) +
          2*TO_NUMBER(SUBSTR(:NEW.ico,7,1));
  kontrolna_cislica := MOD(11 - MOD(suma,11),10);
  
  -- porovnanie kontrolnej cislice, ktoru sme vypocitali s tou, ktora sa nachadza v ICO
  IF NOT TO_NUMBER(SUBSTR(:NEW.ico,8,1)) = kontrolna_cislica THEN
    RAISE_APPLICATION_ERROR(-20002, 'Neplatne ICO!');
  END IF;
END;
/
SHOW ERRORS;

-- triger pre update nazvu syra 
CREATE OR REPLACE TRIGGER druh_syra_update
  AFTER UPDATE OF nazov ON druh_syra
  FOR EACH ROW
BEGIN
  UPDATE bochnik SET druh_syra = :NEW.nazov
  WHERE druh_syra = :OLD.nazov;
  UPDATE dodavatel_druh_syra SET nazov_druh_syra = :NEW.nazov
  WHERE nazov_druh_syra = :OLD.nazov;
  UPDATE objednavka_druh_syra SET druh_syra = :NEW.nazov
  WHERE druh_syra = :OLD.nazov;
END;
/
SHOW ERRORS;

-- ======================== KONIEC TRIGGEROV ==================================
-- ============================================================================

-- ======================== PROCEDURY =========================================
-- ============================================================================
-- procedura vypise bochniky, ktore je potrebne pouzit, aby sme mali dostali pozadovanu hmotnost syru
-- bochniky s mensou trvanlivostou maju prednost
CREATE OR REPLACE PROCEDURE vhodne_bochniky(druh druh_syra.nazov%TYPE, suma_hmotnost bochnik.aktualna_hmotnost%TYPE) AS  
      CURSOR bochnik_cursor IS SELECT * FROM bochnik WHERE druh_syra = druh ORDER BY trvanlivost;
      pboch int;
      bochnik_polozka bochnik%ROWTYPE;
      priebezna_suma bochnik.aktualna_hmotnost%TYPE;
      zvysok bochnik.aktualna_hmotnost%TYPE;
      neplatna_vaha EXCEPTION;
      
BEGIN
  -- mame k dispozicii nejake bochniky ?
  SELECT COUNT(*) INTO pboch FROM bochnik WHERE druh_syra = druh;
  IF pboch = 0 THEN
    RAISE NO_DATA_FOUND;
  END IF;
  -- je pozadovana hmotnost kladna ?
  IF suma_hmotnost <= 0 THEN
    RAISE neplatna_vaha;
  END IF;
  
  OPEN bochnik_cursor;
  priebezna_suma := 0;
  DBMS_OUTPUT.PUT_LINE('Potrebné množstvo syru '||druh||' je '|| suma_hmotnost || 'kg.');
  DBMS_OUTPUT.PUT_LINE('Potrebne bochniky syru ' || druh || ':');
  DBMS_OUTPUT.PUT_LINE('PC'||chr(9)||'Trvanlivost'||chr(9)||'Hmotnost'||chr(9)||'Umiestnenie');
  LOOP
    FETCH bochnik_cursor INTO bochnik_polozka;
    EXIT WHEN bochnik_cursor%NOTFOUND;
    -- vypis bochnikov
    DBMS_OUTPUT.PUT_LINE(bochnik_polozka.poradove_cislo||chr(9)||chr(9)||bochnik_polozka.trvanlivost||chr(9)||chr(9)||
    bochnik_polozka.aktualna_hmotnost||chr(9)||chr(9)||chr(9)||chr(9)||bochnik_polozka.aktualne_umiestnenie);
    priebezna_suma := priebezna_suma + bochnik_polozka.aktualna_hmotnost;
    EXIT WHEN priebezna_suma >= suma_hmotnost;
  END LOOP;
  CLOSE bochnik_cursor;
  zvysok := priebezna_suma-suma_hmotnost;
  IF zvysok > 0 THEN
    DBMS_OUTPUT.PUT_LINE('Z bochnika bude potrebne odrezat, prebytok je '||zvysok||'kg.');
  ELSIF zvysok < 0 THEN
    DBMS_OUTPUT.PUT_LINE('Dane bochniky nestacia, je potrebny este '||-zvysok||'kg syru '||druh || '.');
  ELSE
    DBMS_OUTPUT.PUT_LINE('Bochniky vazia presne tolko kolko je potrebne!');
  END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN  
      DBMS_OUTPUT.PUT_LINE('Nie je k dispozicii ziadny bochnik druhu: ' || druh);
    WHEN neplatna_vaha THEN
      RAISE_APPLICATION_ERROR(-20004,'Vaha musi byt kladne nenulove cislo!');
    WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20003, 'Pri zistovany vhodnych bochnikov doslo k chybe!');
END;
/
SHOW ERRORS;

-- procedura kontroluje objednanu a dodanu hmotnost
-- v objednavke je definovana len hmotnost, jednotlive bochniky su evidovane az po dodani
-- preto je potrebne overit ci sucet hmotnosti bochikov odpoveda objednanej hmotnosti
create or replace PROCEDURE skontroluj_dodane_bochniky(id_obj objednavka.id_objednavky%TYPE) AS
  CURSOR sumy_druhov_obj IS SELECT druh_syra, hmotnost 
                            FROM objednavka_druh_syra
                            WHERE id_obj = id_objednavky 
                            ORDER BY druh_syra ASC;
  CURSOR sumy_druhov_realne IS SELECT druh_syra, SUM(pociatocna_hmotnost), COUNT(poradove_cislo) pocet_bochnikov 
                               FROM bochnik 
                               WHERE id_obj = id_objednavky 
                               GROUP BY druh_syra ORDER BY druh_syra ASC;
  syr_obj objednavka_druh_syra.druh_syra%TYPE;
  syr_realne objednavka_druh_syra.druh_syra%TYPE;
  hmotnost_obj objednavka_druh_syra.hmotnost%TYPE;
  hmotnost_realne objednavka_druh_syra.hmotnost%TYPE;
  diff objednavka_druh_syra.hmotnost%TYPE;
  pocet_bochnikov int;
  ok int := 1;
  chybajuci_druh EXCEPTION;
  nezhodna_hmotnost EXCEPTION;
BEGIN
  OPEN sumy_druhov_obj;
  OPEN sumy_druhov_realne;
  LOOP
   FETCH sumy_druhov_obj INTO syr_obj, hmotnost_obj;
   FETCH sumy_druhov_realne INTO syr_realne, hmotnost_realne, pocet_bochnikov;
    IF (sumy_druhov_obj%NOTFOUND AND sumy_druhov_realne%FOUND) OR (sumy_druhov_obj%FOUND AND sumy_druhov_realne%NOTFOUND) THEN
     RAISE chybajuci_druh;
    END IF;
    EXIT WHEN sumy_druhov_obj%NOTFOUND AND sumy_druhov_realne%NOTFOUND;
    IF NOT syr_obj = syr_realne THEN 
     RAISE chybajuci_druh;
    END IF;
    IF NOT hmotnost_obj = hmotnost_realne THEN
        diff := hmotnost_obj - hmotnost_realne;
        ok := 0;
        DBMS_OUTPUT.PUT_LINE('Objednana a dorucena hmotnost syru '|| syr_obj ||' sa lisi, objednana:'||hmotnost_obj||', dorucena:'|| hmotnost_realne || ', rozdiel: ' || diff);
        DBMS_OUTPUT.PUT_LINE('Syr bol doruceni v  ' || pocet_bochnikov || ' bochnikoch.');
      NULL;
    END IF;
  END LOOP;
  CLOSE sumy_druhov_obj;
  CLOSE sumy_druhov_realne;
  IF ok = 1 THEN 
    DBMS_OUTPUT.PUT_LINE('Objednane a dorucene hmotnosti syrov suhlasia!');
  END IF;
  EXCEPTION
    WHEN chybajuci_druh THEN
      DBMS_OUTPUT.PUT_LINE('Druhy syra v objednavke nesuhlasia s druhmy syra, ktore boli dorucene!');
    WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20005, 'Pri overovani hmotnosti doslo k chybe!');
END;
/
SHOW ERRORS;

-- ======================== KONIEC PROCEDUR ====================================
-- =============================================================================

-- ======================== NAPLNANIE TABULIEK =================================
-- =============================================================================

-- zamestnanec
INSERT INTO zamestnanec(meno,priezvisko,ulica,mesto,psc,telefon,email,funkcia) VALUES('Jan','Novak','Lesna 670','Bratislava','841 07','0902033123','jannovak@gmail.com','veduci predajne');
INSERT INTO zamestnanec(meno,priezvisko,ulica,mesto,psc,telefon,email,funkcia) VALUES('Milan','Sabol','Na Troskach 36/4','Bratislava','841 07','0902033145',NULL,'skladnik');
INSERT INTO zamestnanec(meno,priezvisko,ulica,mesto,psc,telefon,email,funkcia) VALUES('Peter','Novy','Bajkalska 5','Bratislava','841 07','0902033456','novypeter@gmail.com','predavac');
INSERT INTO zamestnanec(meno,priezvisko,ulica,mesto,psc,telefon,email,funkcia) VALUES('Tomas','Surovy','Einsteinova 4','Bratislava','841 07','0902124589','surovyt@gmail.com','predavac');
INSERT INTO zamestnanec(meno,priezvisko,ulica,mesto,psc,telefon,email,funkcia) VALUES('Matus','Devecka','Dunajska 23','Skalica','845 04','0915369524','deveckamatus@gmail.com','skladnik');

--dodavatel
INSERT INTO dodavatel(ico,nazov,ulica,mesto,psc,telefon,email)  VALUES('12345679','Farma Vychodna',NULL,'Vychodna 580','03105','0445524578','farmavychodna@gmail.com');
INSERT INTO dodavatel(ico,nazov,ulica,mesto,psc,telefon,email)  VALUES('12346926','Koliba Hrinova','Krivec 2663','Hrinova','96205','0905022456','kolibahrinova@gmail.com');
INSERT INTO dodavatel(ico,nazov,ulica,mesto,psc,telefon,email)  VALUES('36897728','Mlekarna Otinoves s.r.o',NULL,'Otinoves 201','79861','+420903654723','mlekarnaotin@gmail.com');
INSERT INTO dodavatel(ico,nazov,ulica,mesto,psc,telefon,email)  VALUES('36845931','FORMAN Brno spol. s r.o.','Vestonicka 4289/12','Brno','62800','+420902156983','forman@gmail.com');
INSERT INTO dodavatel(ico,nazov,ulica,mesto,psc,telefon,email)  VALUES('45892237','Cheese Factory Volendam','Haven 25','Volendam','131EP','+312993504790',' info@cheesefactoryvolendam.nl');

--zahranicny
INSERT INTO zahranicny VALUES('36897728','A','CZK',NULL);
INSERT INTO zahranicny VALUES('36845931','A','CZK','100');
INSERT INTO zahranicny VALUES('45892237','A','EUR','250');

--miestny
INSERT INTO miestny VALUES('12345679','Liptov');
INSERT INTO miestny VALUES('12346926','Horehronie');

--objednavka

INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('vybavena',TO_TIMESTAMP('2017-02-21 14:20:00','YYYY-MM-DD HH24:MI:SS.FF'),'predajna','1','12345679');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('vybavena',TO_TIMESTAMP('2017-02-05 10:21:25','YYYY-MM-DD HH24:MI:SS.FF'),'predajna','1','12346926');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('zadana',TO_TIMESTAMP('2017-03-15 08:05:00','YYYY-MM-DD HH24:MI:SS.FF'),'predajna','3','36897728');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('zadana',TO_TIMESTAMP('2017-03-21 08:06:55','YYYY-MM-DD HH24:MI:SS.FF'),'predajna','3','36845931');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('vybavena',TO_TIMESTAMP('2017-03-15 09:00:00','YYYY-MM-DD HH24:MI:SS.FF'),'sklad','1','36897728');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('vybavena',TO_TIMESTAMP('2017-04-16 09:00:00','YYYY-MM-DD HH24:MI:SS.FF'),'sklad','2','36897728');
INSERT INTO objednavka(stav,datum_vytvorenia,miesto_dodania,id_zamestnanca,ico) VALUES('vybavena',TO_TIMESTAMP('2017-04-20 10:00:00','YYYY-MM-DD HH24:MI:SS.FF'),'sklad','2','12345679');
--zem povodu
INSERT INTO zem_povodu VALUES('Francuzsko','Syry vo Francuzsku predstavuju neoddelitelnu sucast jedalnicka a ich konzumacia sa zaradila medzi gurmanske pozitky.
Je priam nemozne vymenovat vsetky ich syry, hovori sa až o 500 roznych druhov.Tie najkvalitnejsie nesu ochrannu znacku kontroly povodu - Appellation d Origine Controlee.');

INSERT INTO zem_povodu VALUES ('Ceska republika','Historia vyroby syrov v Cechach sa zacala pisat v priebehu 10. storocia, kedy sa podomacky pripravovaly jednoduche tvarohove syry.
Vyrazny rozmach sa odohral v priebehu 19. a 20. storocia, kedy sa spustila vyroba novych typov syrov inspirovanych svajciarskou, francuzskou ci holandskou tradiciou. Medzi najoblubenejsie syry patri eidam
a tavene syry.');

INSERT INTO zem_povodu VALUES ('Slovenska republika','Regionalne jedla su pestre tak ako slovenska krajina samotna. Tradicna kuchyna horskych oblasti praje najma milovnikom syrov a mliecnych vyrobkov.
Typicke su mliecne vyrobky ako cmar, tvaroh, smotana, bryndza (slany ovci tvaroh) a ovcie syry. K najoblubenejsim patria syrove korbaciky, udene i neudene parenice, ostiepky a bryndza.');

INSERT INTO zem_povodu VALUES ('Svajciarsko','Svajciarske syry patria k najuznavanejsim a najchutnejsim na svete. Nenajdete ich iba v luxusnych restauraciach, ale aj v malych vidieckych pohostinstvach.
Uspech syrov v tejto krajine pripisuju vybornym klimatickym podmienkam, cerstvemu vzduchu a kvalitnym pastvinam.');

--druh syra

INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Cabridoux','makky','koza','14','Francuzsko');
INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Roquefort','plesnivy','ovca','55','Francuzsko');
INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Granmoravia','tvrdy','krava','30','Ceska republika');
INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Svajciarsky emental','tvrdy','krava','45','Svajciarsko');
INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Polostiepok','polotvrdy udeny','krava','40','Slovenska republika');
INSERT INTO druh_syra(nazov,typ,zivocich,percento_tuku,nazov_zeme_povodu) VALUES('Ostiepok','polotvrdy udeny','krava','45','Slovenska republika');



--objednvka druh syra

INSERT INTO objednavka_druh_syra VALUES('1','Polostiepok','100');
INSERT INTO objednavka_druh_syra VALUES('1','Ostiepok','10');
INSERT INTO objednavka_druh_syra VALUES('2','Roquefort','75');
INSERT INTO objednavka_druh_syra VALUES('5','Svajciarsky emental','50');
INSERT INTO objednavka_druh_syra VALUES('4','Cabridoux','80');
INSERT INTO objednavka_druh_syra VALUES('3','Granmoravia','15');
INSERT INTO objednavka_druh_syra VALUES('6','Granmoravia','20');
INSERT INTO objednavka_druh_syra VALUES('7','Ostiepok','12');
--dodavatel druh syra

INSERT INTO dodavatel_druh_syra VALUES('Polostiepok','12345679');
INSERT INTO dodavatel_druh_syra VALUES('Ostiepok','12345679');
INSERT INTO dodavatel_druh_syra VALUES('Svajciarsky emental','12346926');
INSERT INTO dodavatel_druh_syra VALUES('Roquefort','45892237');
INSERT INTO dodavatel_druh_syra VALUES('Cabridoux','36845931');
INSERT INTO dodavatel_druh_syra VALUES('Granmoravia','36897728');


--bochnik

INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Polostiepok','25','10',TO_DATE('2017-02-21','YYYY-MM-DD'),TO_DATE('2017-06-21','YYYY-MM-DD'),'predajna','1');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Polostiepok','25','25',TO_DATE('2017-02-21','YYYY-MM-DD'),TO_DATE('2017-06-28','YYYY-MM-DD'),'predajna','1');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Polostiepok','50','50',TO_DATE('2017-02-21','YYYY-MM-DD'),TO_DATE('2017-07-28','YYYY-MM-DD'),'predajna','1');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Ostiepok','10','10',TO_DATE('2017-02-21','YYYY-MM-DD'),TO_DATE('2017-06-5','YYYY-MM-DD'),'predajna','1');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Roquefort','75','35',TO_DATE('2017-02-05','YYYY-MM-DD'),TO_DATE('2017-05-05','YYYY-MM-DD'),'predajna','2');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Granmoravia','15','11',TO_DATE('2017-03-15','YYYY-MM-DD'),TO_DATE('2017-06-25','YYYY-MM-DD'),'predajna','3');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Cabridoux','80','40,5',TO_DATE('2017-03-21','YYYY-MM-DD'),TO_DATE('2017-07-01','YYYY-MM-DD'),'predajna','4');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Svajciarsky emental','50','32',TO_DATE('2017-03-15','YYYY-MM-DD'),TO_DATE('2017-07-22','YYYY-MM-DD'),'sklad','5');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Granmoravia','20','11',TO_DATE('2017-04-16','YYYY-MM-DD'),TO_DATE('2017-07-25','YYYY-MM-DD'),'predajna','6');
INSERT INTO bochnik(druh_syra,pociatocna_hmotnost,aktualna_hmotnost,datum_dodania,trvanlivost,aktualne_umiestnenie,id_objednavky) VALUES('Ostiepok','12','12',TO_DATE('2017-05-22','YYYY-MM-DD'),TO_DATE('2017-08-25','YYYY-MM-DD'),'predajna','7');
-- ======================== KONIEC NAPLNANIA TABULIEK ==========================
-- =============================================================================


-- ======================== SELECTY ============================================
-- =============================================================================

--vypis informacie o dodavateloch u ktorych je stav objednavky = zadana
SELECT nazov,ulica,mesto,ico 
FROM dodavatel NATURAL JOIN objednavka 
WHERE stav = 'zadana';

--vypise informacie o regionalnych dodavateloch
SELECT nazov,ulica,mesto,region 
FROM dodavatel NATURAL JOIN miestny;

--vypis objednavok,kde sa objednava tvrdy typ syra
SELECT id_objednavky,nazov 
FROM (objednavka NATURAL JOIN objednavka_druh_syra) JOIN druh_syra ON druh_syra = nazov 
WHERE typ='tvrdy';


--zisti pocet bochnikov v ramci jedneho zivocicha, ktore maju kladnu aktualnu hmotnost. Zaroven urobi sumu aktualnych hmotnosti bochnikov pochadzajucich z daneho zivociha. 
SELECT D.zivocich, COUNT (*) pocet_bochnikov, SUM(B.aktualna_hmotnost) suma_hmotnosti_bochnikov
FROM bochnik B, druh_syra D
WHERE B.druh_syra = D.nazov AND B.aktualna_hmotnost > 0
GROUP BY D.zivocich;

-- Pocet objednavok, ktore uskutocnili zamestnanci, zoradena zostupne
SELECT meno, priezvisko, COUNT(*) pocet
FROM zamestnanec NATURAL JOIN objednavka
GROUP BY meno, priezvisko,id_zamestnanca
ORDER BY pocet DESC;

-- Vypise nazov a typ syra, z ktorych niektorym bochnikom prejde zaruka skor ako 29.6.2017
SELECT nazov, typ
FROM druh_syra 
WHERE nazov IN
  (SELECT druh_syra nazov FROM bochnik 
   WHERE trvanlivost < TO_DATE('2017-06-29','YYYY-MM-DD'));

--Vypise druhy syra z ktorych mame bochniky o velkosti vacsej ako 30 kg
SELECT nazov
FROM druh_syra 
WHERE EXISTS
  (SELECT * FROM bochnik 
   WHERE druh_syra = druh_syra.nazov AND aktualna_hmotnost > 30); 

-- ======================== KONIEC SELECTOV ====================================
-- =============================================================================

-- ======================== INDEX A EXPLAIN PLAN  ==============================
-- =============================================================================
-- plan pre select, ktory vypise kolko je k dispozicii syru daneho typu
EXPLAIN PLAN FOR 
SELECT D.typ, SUM(B.aktualna_hmotnost) hmotnost
FROM druh_syra D JOIN bochnik B ON D.nazov = B.druh_syra
GROUP BY  D.typ
ORDER BY hmotnost DESC;
SELECT plan_table_output from table(dbms_xplan.display());

-- vytvaranie indexu pre bochnik
CREATE INDEX bochnik_druh_syra_ix ON bochnik (druh_syra);
--DROP INDEX bochnik_druh_syra_ix;

-- plan s napovedov na pouzitie indexu pre bochnik
EXPLAIN PLAN FOR 
SELECT  /*+ INDEX(B bochnik_druh_syra_ix) */ D.typ, SUM(B.aktualna_hmotnost) hmotnost
FROM druh_syra D JOIN bochnik B ON D.nazov = B.druh_syra
GROUP BY  D.typ
ORDER BY hmotnost DESC;
SELECT plan_table_output from table(dbms_xplan.display());

-- ======================== KONIEC INDEXU A EXPLAIN PLAN  ======================
-- =============================================================================


-- ======================== UDELENIE PRAV PRE XDUBRA03 =========================
-- =============================================================================
-- udelenie privilegia ALL na vsetky tabulky
GRANT ALL PRIVILEGES ON dodavatel TO xdubra03;
GRANT ALL PRIVILEGES ON zahranicny TO xdubra03;
GRANT ALL PRIVILEGES ON miestny TO xdubra03;
GRANT ALL PRIVILEGES ON zem_povodu TO xdubra03;
GRANT ALL PRIVILEGES ON zamestnanec TO xdubra03;
GRANT ALL PRIVILEGES ON objednavka TO xdubra03;
GRANT ALL PRIVILEGES ON druh_syra TO xdubra03;
GRANT ALL PRIVILEGES ON bochnik TO xdubra03;
GRANT ALL PRIVILEGES ON dodavatel_druh_syra TO xdubra03;
GRANT ALL PRIVILEGES ON objednavka_druh_syra TO xdubra03;
-- udelenie prava na spustenie procedury
GRANT EXECUTE ON vhodne_bochniky TO xdubra03;
GRANT EXECUTE ON skontroluj_dodane_bochniky TO xdubra03;
-- pravo na pouzivanie sekvencie
GRANT SELECT ON id_zamestnanca_seq TO xdubra03;
GRANT SELECT ON id_objednavky_seq TO xdubra03;
-- ======================== KONIEC UDELENIE PRAV ===============================
-- =============================================================================

-- ======================== MATERIALIZOVANY POHLAD =============================
-- =============================================================================
-- vytvorenie loguov pre fast refresh
CREATE OR REPLACE SYNONYM druhy_bochnik FOR xdubra03.bochnik; 
CREATE OR REPLACE SYNONYM druhy_druh_syra FOR xdubra03.druh_syra; 

-- kontrola prav
SELECT * FROM druhy_bochnik;
SELECT * FROM druhy_druh_syra;

-- script druheho clena tymu 
--GRANT ALL PRIVILEGES ON druh_syra TO xmarus07;
--GRANT ALL PRIVILEGES ON bochnik TO xmarus07;
--CREATE MATERIALIZED VIEW log on druh_syra with rowid; 
--CREATE MATERIALIZED VIEW log on bochnik with rowid;
--GRANT ALL ON MLOG$_bochnik TO xmarus07;
--GRANT ALL ON MLOG$_druh_syra TO xmarus07;

-- materializovany pohlad
CREATE MATERIALIZED VIEW syr_bochnik
  NOLOGGING
  CACHE
  BUILD IMMEDIATE
  REFRESH FAST ON COMMIT
  ENABLE QUERY REWRITE
  AS
    SELECT nazov,typ, poradove_cislo as bochnik, aktualna_hmotnost, trvanlivost, D.rowid as druh_syra_rowid, B.rowid as bochnik_row_id
    FROM druhy_druh_syra D JOIN druhy_bochnik B ON nazov = druh_syra;
    
-- udelenie prav na pohlad
GRANT ALL ON syr_bochnik TO xdubra03;

-- ukazka prace s pohladom
SELECT trvanlivost, aktualna_hmotnost FROM syr_bochnik WHERE nazov = 'Polostiepok';
-- vypisanie konkretneho bochniku a jeho hmotnosti
SELECT nazov,typ,aktualna_hmotnost FROM syr_bochnik WHERE bochnik = 1 and nazov = 'Polostiepok';
-- zmena hodnoty v bazovej tabulke
UPDATE druhy_bochnik SET aktualna_hmotnost = 15 WHERE poradove_cislo = 1 and druh_syra = 'Polostiepok';
-- zmena sa neprajavila
SELECT nazov,typ,aktualna_hmotnost FROM syr_bochnik WHERE bochnik = 1 and nazov = 'Polostiepok';
-- refresh on commit
COMMIT;
-- zmena sa premietla do pohladu
SELECT nazov,typ,aktualna_hmotnost FROM syr_bochnik WHERE bochnik = 1 and nazov = 'Polostiepok';
-- navratenie zmenenej hodnoty do povodneho stavu
UPDATE druhy_bochnik SET aktualna_hmotnost = 10 WHERE poradove_cislo = 1 and druh_syra = 'Polostiepok';
-- ======================== KONIEC MATERIALIZOVANEHO POHLADU ===================
-- =============================================================================

-- pre vypis pomocou dbmd_output.put_line
SET SERVEROUTPUT ON;

-- DEMONSTRACIA procedury vhodne_bochniky
EXECUTE vhodne_bochniky('Granmoravia',22);
EXECUTE vhodne_bochniky('Granmoravia',19);
EXECUTE vhodne_bochniky('Granmoravia',-1);
EXECUTE vhodne_bochniky('Polostiepok',80);


-- DEMONSTRACIA procedury skontroluj_dodane_bochniky
-- skontroluje objednavku s id 1
-- prikazy pre demonstraciu procedury skontroluj_dodane_bochniky
SELECT * from bochnik;
SELECT * from objednavka_druh_syra;
UPDATE bochnik set pociatocna_hmotnost = 21  where poradove_cislo = 1;
UPDATE bochnik set pociatocna_hmotnost = 9  where poradove_cislo = 4;
EXECUTE skontroluj_dodane_bochniky(1);
UPDATE bochnik set pociatocna_hmotnost = 25  where poradove_cislo = 1;
UPDATE bochnik set pociatocna_hmotnost = 10  where poradove_cislo = 4;
EXECUTE skontroluj_dodane_bochniky(1);
-- overenie objednavky s id 7
EXECUTE skontroluj_dodane_bochniky(7);
INSERT INTO objednavka_druh_syra VALUES('7','Polostiepok','5');
EXECUTE skontroluj_dodane_bochniky(7);

-- DEMONSTRACIA TRIGGRU druh_syra_update
SELECT * 
FROM druh_syra JOIN bochnik ON nazov = druh_syra NATURAL JOIN objednavka_druh_syra
WHERE druh_syra = 'Polostiepok';
UPDATE druh_syra SET nazov = 'Polo' WHERE nazov = 'Polostiepok';
SELECT *
FROM druh_syra JOIN bochnik ON nazov = druh_syra NATURAL JOIN objednavka_druh_syra
WHERE druh_syra = 'Polo';
UPDATE druh_syra SET nazov = 'Polostiepok' WHERE nazov = 'Polo';

-- DEMONSTRACIA kontroly ica - trigger kontrola_ico
SELECT * FROM dodavatel WHERE ico = '45892237';
-- pokus o zmenu na neplatne ico - zmena kontrolnej cislice
UPDATE dodavatel SET ico = '45892238' WHERE ico = '45892237';

