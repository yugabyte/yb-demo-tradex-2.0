CREATE SEQUENCE ORDER_ID_SEQ CACHE 1000;
CREATE SEQUENCE TRADE_SYMBOL_SEQ CACHE 1000;
CREATE SEQUENCE TRADE_ID_SEQ CACHE 1000000;

CREATE TABLE APP_USER
(
  ID                 SERIAL,
  EMAIL              VARCHAR(50),
  PASSKEY            VARCHAR(100) NOT NULL,
  ENABLED            SMALLINT      NOT NULL DEFAULT 1,
  PERSONAL_DETAILS   VARCHAR(4000)         NOT NULL,
  USER_LANGUAGE      VARCHAR(50)           DEFAULT 'EN-UK',
  USER_NOTIFICATIONS VARCHAR(4000)         NOT NULL,
  PREFERRED_REGION   VARCHAR(20)  NOT NULL,
  CREATED_DATE       TIMESTAMP             DEFAULT NOW(),
  UPDATED_DATE       TIMESTAMP             DEFAULT NOW(),
  FAVOURITES         INTEGER[],
  SECURITY_PIN       NUMERIC(4),
  PRIMARY KEY (ID, PREFERRED_REGION),
  UNIQUE (EMAIL)
);

CREATE TABLE REF_DATA
(
  ID           SERIAL,
  KEY_NAME     VARCHAR(200),
  KEY_VALUE    VARCHAR(4000) NOT NULL,
  CLASSIFIER   VARCHAR(100) DEFAULT 'GLOBAL',
  CREATED_DATE TIMESTAMP    DEFAULT NOW(),
  UPDATED_DATE TIMESTAMP    DEFAULT NOW(),
  primary key (ID, KEY_NAME, CLASSIFIER)
);

CREATE TABLE TRADE_SYMBOL
(
  TRADE_SYMBOL_ID integer NOT NULL DEFAULT NEXTVAL('TRADE_SYMBOL_SEQ'),
  SYMBOL          varchar(6),
  COMPANY         varchar(200),
  EXCHANGE        varchar(8),
  price_time      timestamp,
  open_price      decimal(12, 4),
  low_price       decimal(12, 4),
  high_price      decimal(12, 4),
  close_price     decimal(12, 4),
  market_cap      decimal(22, 4),
  volume          decimal(22, 4),
  avg_volume      decimal(22, 4),
  ENABLED         smallint,
  CREATED_DATE    timestamp        default now(),
  UNIQUE (SYMBOL),
  constraint TRADE_SYMBOL_PKEY primary key (TRADE_SYMBOL_ID)
);

CREATE TABLE TRADE_ORDERS
(
  ORDER_ID         integer        NOT NULL        DEFAULT NEXTVAL('ORDER_ID_SEQ'),
  USER_ID          integer        NOT NULL,
  SYMBOL_ID        integer,
  TRADE_TYPE       character varying(5),
  ORDER_TIME       timestamp(0) without time zone DEFAULT now(),
  BID_PRICE        decimal(10, 3),
  PREFERRED_REGION VARCHAR(20)    NOT NULL,
  stock_units      decimal(10, 3) not null        default 1.0,
  pay_method       varchar(20),
  CONSTRAINT TRADES_PKEY PRIMARY KEY (ORDER_ID)
);

CREATE TABLE TRADE_SYMBOL_PRICE_HISTORIC
(
  TRADE_ID INTEGER NOT NULL        DEFAULT NEXTVAL('TRADE_ID_SEQ'),
  TRADE_SYMBOL_ID INTEGER,
  PRICE_TIME      TIMESTAMP,
  OPEN_PRICE      DECIMAL(12, 4),
  LOW_PRICE       DECIMAL(12, 4),
  HIGH_PRICE      DECIMAL(12, 4),
  PRICE           DECIMAL(12, 4),
  INTERVAL_PERIOD VARCHAR(6) NOT NULL CHECK ( INTERVAL_PERIOD IN
                                              ('1H', '90MIN', '1DAY', '1WEEK', '3MONTH')),
  CONSTRAINT TRADEID_PKEY PRIMARY KEY (TRADE_ID)
);
