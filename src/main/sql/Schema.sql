CREATE TABLE empresa (
  id int NOT NULL,
  CIF varchar(45) DEFAULT NULL,
  RazonSocial varchar(200) NOT NULL,
  DenominacionComercial varchar(200) NOT NULL,
  Direccion varchar(200) NOT NULL,
  Poblacion varchar(200),
  CodigoPostal varchar(200),
  Web varchar(200) NOT NULL,
  Email varchar(200) NOT NULL,
  Contacto varchar(200) NOT NULL,
  Telefono varchar(200) NOT NULL,
  Fax varchar(200) NOT NULL,
  Trabajadores int,
  Asociaciones varchar(400),
  Certificaciones varchar(400),
  AnyoConstitucion int,  
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



