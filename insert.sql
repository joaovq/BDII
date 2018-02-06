CREATE TABLE Evento (
id int NOT NULL,
nome VARCHAR(20) NOT NULL,
capacidade INT NOT NULL,
CONSTRAINT PK_Estadio PRIMARY KEY (id)
);

CREATE TABLE Ingresso (
id INT NOT NULL,
comprado BOOLEAN NOT NULL,
evento INT NOT NULL REFERENCES Evento (id),
CONSTRAINT PK_Ingresso PRIMARY KEY (id, evento)
);

INSERT INTO Evento 
values (1,'Festa 1', 10), (2,'Festa 2', 15), (3,'Festa 3', 20);

CREATE OR REPLACE FUNCTION insereIngresso (id_evento INt, capacidade_evento INT) RETURNS boolean AS $$

DECLARE 
	incremento_ingresso INT := 1; 

BEGIN
	WHILE incremento_ingresso <= capacidade_evento LOOP
		INSERT INTO Ingresso values (incremento_ingresso, false, id_evento);
		incremento_ingresso := incremento_ingresso + 1;
	END LOOP;
	return 1;
END;
$$ LANGUAGE plpgsql;

SELECT insereIngresso(1,10);
SELECT insereIngresso(2,15);
SELECT insereIngresso(3,20);

