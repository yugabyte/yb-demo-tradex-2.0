-- us-east-1 us-east-2 us-west-1 us-west-2

CREATE TABLESPACE west_tablespace WITH (
  replica_placement='{"num_replicas": 3, "placement_blocks":
  [{"cloud":"aws","region":"us-west-1","zone":"us-west-1a","min_num_replicas":1,"leader_preference":1},
  {"cloud":"aws","region":"us-west-2","zone":"us-west-2a","min_num_replicas":1,"leader_preference":2},{"cloud":"aws","region":"us-east-1","zone":"us-west-1a","min_num_replicas":1}]}'
);

CREATE TABLESPACE east_tablespace WITH (
  replica_placement='{"num_replicas": 3, "placement_blocks":
  [{"cloud":"aws","region":"us-east-1","zone":"us-east-1a","min_num_replicas":1,"leader_preference":1},{"cloud":"aws","region":"us-east-2","zone":"us-east-2a","min_num_replicas":1,"leader_preference":2},
  {"cloud":"aws","region":"us-west-1","zone":"us-west-1a","min_num_replicas":1}
  ]}'
);


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
)
PARTITION BY LIST(PREFERRED_REGION);


CREATE TABLE APP_USER_EAST partition of APP_USER (
  ID,  EMAIL, PASSKEY , ENABLED, PERSONAL_DETAILS, USER_LANGUAGE, USER_NOTIFICATIONS, PREFERRED_REGION, CREATED_DATE, UPDATED_DATE, FAVOURITES, SECURITY_PIN, PRIMARY KEY (ID HASH, PREFERRED_REGION)
)
FOR VALUES IN ('us-east-1', 'us-east-2')
TABLESPACE east_tablespace;

CREATE TABLE APP_USER_WEST partition of APP_USER (
  ID,  EMAIL, PASSKEY , ENABLED, PERSONAL_DETAILS, USER_LANGUAGE, USER_NOTIFICATIONS, PREFERRED_REGION, CREATED_DATE, UPDATED_DATE, FAVOURITES, SECURITY_PIN, PRIMARY KEY (ID HASH, PREFERRED_REGION)
)
FOR VALUES IN ('us-west-1', 'us-west-2')
TABLESPACE east_tablespace;


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
)
PARTITION BY LIST(PREFERRED_REGION);


CREATE TABLE TRADE_ORDERS_EAST partition of TRADE_ORDERS (
  ORDER_ID, USER_ID, SYMBOL_ID, TRADE_TYPE, ORDER_TIME, BID_PRICE, PREFERRED_REGION, STOCK_UNITS, PAY_METHOD, PRIMARY key (ORDER_ID hash, PREFERRED_REGION)
  ) for values in ('us-east-1', 'us-east-2') tablespace boston_tablespace;

CREATE TABLE TRADE_ORDERS_WEST partition of TRADE_ORDERS (
  ORDER_ID, USER_ID, SYMBOL_ID, TRADE_TYPE, ORDER_TIME, BID_PRICE, PREFERRED_REGION, STOCK_UNITS, PAY_METHOD, PRIMARY key (ORDER_ID hash, PREFERRED_REGION)
  ) for values in ('us-west-1', 'us-west-2') tablespace washington_tablespace;
