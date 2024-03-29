DROP SEQUENCE IF EXISTS ORDER_ID_SEQ;
DROP TABLE IF EXISTS APP_USER;
drop table if exists trade_symbol_price_today;
drop table if exists trade_symbol_price_historic;
DROP TABLE IF EXISTS TRADE_ORDERS;
DROP TABLE IF EXISTS REF_DATA;
DROP TABLE IF EXISTS TRADE_SYMBOL;
DROP SEQUENCE IF EXISTS TRADE_SYMBOL_SEQ;


CREATE SEQUENCE ORDER_ID_SEQ CACHE 1000;
CREATE SEQUENCE TRADE_SYMBOL_SEQ CACHE 1000;

CREATE TABLE APP_USER
(
  ID                 SERIAL,
  EMAIL              VARCHAR(50),
  PASSKEY            VARCHAR(100) NOT NULL,
  ENABLED            BOOLEAN      NOT NULL DEFAULT TRUE,
  PERSONAL_DETAILS   JSON         NOT NULL,
  USER_LANGUAGE      VARCHAR(50)           DEFAULT 'EN-UK',
  USER_NOTIFICATIONS JSON         NOT NULL,
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
  KEY_VALUE    JSON NOT NULL,
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
  ENABLED         boolean,
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
  TRADE_SYMBOL_ID INTEGER,
  PRICE_TIME      TIMESTAMP,
  OPEN_PRICE      DECIMAL(12, 4),
  LOW_PRICE       DECIMAL(12, 4),
  HIGH_PRICE      DECIMAL(12, 4),
  PRICE           DECIMAL(12, 4),
  INTERVAL_PERIOD VARCHAR(6) NOT NULL CHECK ( INTERVAL_PERIOD IN
                                              ('1H', '90MIN', '1DAY', '1WEEK', '3MONTH'))
);

CREATE OR REPLACE VIEW five_days_stock_trend_v AS
(
SELECT trade_symbol_id                                          AS symbol_id,
       ARRAY_AGG(TRUNC(high_price, 2) ORDER BY price_time DESC) AS trend
FROM trade_symbol_price_historic tsph
WHERE interval_period = '1DAY'
GROUP BY trade_symbol_id
  );

CREATE OR REPLACE VIEW current_stocks_v AS
(
SELECT o.user_id,
       o.preferred_region,
       o.symbol_id,
       sum(CASE WHEN trade_type = 'BUY' THEN stock_units ELSE -stock_units END) AS units
FROM trade_orders o
GROUP BY o.user_id, o.preferred_region, o.symbol_id
  );

CREATE OR REPLACE VIEW stock_period_info_v AS
(
SELECT trade_symbol_id,
       high_price,
       price_time,
       interval_period
FROM trade_symbol_price_historic tsph,
     current_stocks_v c
WHERE tsph.trade_symbol_id = c.symbol_id
  );

TRUNCATE TABLE REF_DATA;

INSERT INTO REF_DATA(KEY_NAME, KEY_VALUE)
VALUES ('TRAFFIC_LOCATIONS',
        '{
          "TRAFFIC_LOCATIONS": [
            {
              "id": 1,
              "name": "Boston",
              "country": "United States",
              "latitude": "42.36",
              "longitude": "-71.05"
            },
            {
              "id": 2,
              "name": "Washington",
              "country": "United States",
              "latitude": "47.751",
              "longitude": "-120.740"
            },
            {
              "id": 3,
              "name": "London",
              "country": "United Kingdom",
              "latitude": "51.509",
              "longitude": "-0.118"
            },
            {
              "id": 4,
              "name": "Mumbai",
              "country": "India",
              "latitude": "19.076",
              "longitude": "72.877"
            },
            {
              "id": 5,
              "name": "Sydney",
              "country": "Australia",
              "latitude": "-33.865",
              "longitude": "151.209"
            }
          ]
        }');

insert into REF_DATA(KEY_NAME, KEY_VALUE)
values ('DB_CLUSTER_TYPES',
        '{
          "DB_CLUSTER_TYPES": [
            {
              "id": 0,
              "title": "Single-region, multi-zone",
              "subtitle": "3 node deployed in US West"
            },
            {
              "id": 1,
              "title": " Multi-region",
              "subtitle": "3 nodes deployed in US West, US Central and US East"
            },
            {
              "id": 2,
              "title": " Multi-region, multi-zone with Read Replicas",
              "subtitle": "3 nodes deployed in US East, with read replicas in Europe and Asia"
            },
            {
              "id": 3,
              "title": " Geo-partitioned",
              "subtitle": "3 nodes deployed in US East, with 2 nodes in Europe and Asia"
            }
          ]
        }');

insert into REF_DATA(KEY_NAME, KEY_VALUE)
values ('DEFAULT_NODE_LOCATIONS',
        '{
          "DEFAULT_NODE_LOCATIONS": [
            {
              "id": 0,
              "name": "useast1",
              "country": "USA",
              "latitude": "38.13",
              "longitude": "-78.45"
            },
            {
              "id": 1,
              "name": "useast2",
              "country": "USA",
              "latitude": "39.96",
              "longitude": "-83"
            },
            {
              "id": 2,
              "name": "uswest1",
              "country": "USA",
              "latitude": "37.35",
              "longitude": "-121.96"
            },
            {
              "id": 3,
              "name": "uswest2",
              "country": "USA",
              "latitude": "46.15",
              "longitude": "-123.88"
            },
            {
              "id": 4,
              "name": "euwest1",
              "country": "Ireland",
              "latitude": "53",
              "longitude": "-8"
            },
            {
              "id": 5,
              "name": "euwest2",
              "country": "UK",
              "latitude": "51",
              "longitude": "-0.1"
            },
            {
              "id": 6,
              "name": "euwest3",
              "country": "FRANCE",
              "latitude": "48.86",
              "longitude": "2.35"
            },
            {
              "id": 7,
              "name": "eucentral1",
              "country": "GERMANY",
              "latitude": "50",
              "longitude": "8"
            },
            {
              "id": 8,
              "name": "apsoutheast1",
              "country": "SINGAPORE",
              "latitude": "1.32",
              "longitude": "103.69"
            },
            {
              "id": 9,
              "name": "apsoutheast2",
              "country": "AUSTRALIA",
              "latitude": "-33.91",
              "longitude": "151.19"
            },
            {
              "id": 10,
              "name": "apsouth1",
              "country": "INDIA",
              "latitude": "19.242",
              "longitude": "72.96"
            },
            {
              "id": 11,
              "name": "apnortheast1",
              "country": "JAPAN",
              "latitude": "35.617",
              "longitude": "139.74"
            },
            {
              "id": 12,
              "name": "datacenter1",
              "country": "USA",
              "latitude": "38.13",
              "longitude": "-78.45"
            }
          ]
        }');


INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('INTC', 'Intel', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('MS', 'Morgan Stanley', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BHP', 'BHP Group', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BRK.A', 'Berkshire Hathaway', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('GOOGL', 'Alphabet', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('FMX', 'Fomento Economico Mexicano', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('JPM', 'JPMorgan Chase & Co.', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('UL', 'Unilever', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('QCOM', 'Qualcomm', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('WFC', 'Wells Fargo & Company', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('ORCL', 'Oracle', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('CAT', 'Caterpillar', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TXN', 'Texas Instruments', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AMGN', 'Amgen', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('CVX', 'Chevron', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('ABBV', 'AbbVie', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('NVS', 'Novartis AG', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TMUS', 'T-Mobile US', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AXP', 'American Express Company', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('ADP', 'Automatic Data Processing', 'NYSE', true, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('SPGI', 'S&P Global', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('NVDA', 'NVIDIA', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('PM', 'Philip Morris International', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('META', 'Meta Platforms', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('IBM', 'International Business Machines', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('ABT', 'Abbott Laboratories', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AMD', 'Advanced Micro Devices', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TSM', 'Taiwan Semiconductor Manufacturing Company', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('BABA', 'Alibaba Group Holding', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('UNH', 'UnitedHealth Group', 'NYSE', true, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('ADBE', 'Adobe', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('JNJ', 'Johnson & Johnson', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('PBR.A', 'Petroleo Brasileiro', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AZN', 'AstraZeneca', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('VZ', 'Verizon Communications', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('MCD', 'McDonalds', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BMY', 'Bristol-Myers Squibb Company', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('COP', 'ConocoPhillips', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('V', 'Visa Inc.', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('TD', 'The Toronto-Dominion Bank', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('SBUX', 'Starbucks', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('ELV', 'Elevance Health', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('HSBC', 'HSBC Holdings', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('LMT', 'Lockheed Martin', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TTE', 'TotalEnergies SE', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('BP', 'BP plc', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('PG', 'The Procter & Gamble Company', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('WMT', 'Walmart', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('UNP', 'Union Pacific', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('HD', 'The Home Depot', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('DHR', 'Danaher', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('CMCSA', 'Comcast', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('CVS', 'CVS Health', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AVGO', 'Broadcom', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('UPS', 'United Parcel Service', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('NEE', 'NextEra Energy', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('KO', 'The Coca-Cola Company', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('T', 'AT&T', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BX', 'Blackstone', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BRK.B', 'Berkshire Hathaway', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('MA', 'Mastercard', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('HON', 'Honeywell International', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('SHEL', 'Shell', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('RY', 'Royal Bank of Canada', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('NKE', 'Nike', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('SNY', 'Sanofi', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('CSCO', 'Cisco Systems', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('DE', 'Deere & Company', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('EQNR', 'Equinor ASA', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('RTX', 'Raytheon Technologies', 'NYSE', true, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('NFLX', 'Netflix', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('COST', 'Costco Wholesale', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('HDB', 'HDFC Bank', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('AMZN', 'Amazon.com', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('XOM', 'Exxon Mobil', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('CRM', 'Salesforce.com', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('GS', 'The Goldman Sachs Group', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('LLY', 'Eli Lilly and Company', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TM', 'Toyota Motor', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('LIN', 'Linde', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('PYPL', 'PayPal Holdings', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('PFE', 'Pfizer', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('MRK', 'Merck & Co.', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('NVO', 'Novo Nordisk', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('MDT', 'Medtronic plc.', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('ACN', 'Accenture', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('SCHW', 'The Charles Schwab Corporation', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TMO', 'Thermo Fisher Scientific', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('INTU', 'Intuit', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('DIS', 'The Walt Disney Company', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO trade_symbol (symbol, company, exchange, enabled, created_date)
VALUES ('ASML', 'ASML Holding', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('LOW', 'Lowes Companies', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('SAP', 'SAP SE', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('PBR', 'Petroleo Brasileiro', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('TSLA', 'Tesla', 'NYSE', true, '2022-11-01 12:52:45.944782'),
       ('PEP', 'PepsiCo', 'NYSE', false, '2022-11-01 12:52:45.944782'),
       ('BAC', 'Bank of America', 'NYSE', false, '2022-11-01 12:52:45.944782');

INSERT INTO app_user (email, passkey, enabled, personal_details, user_notifications, preferred_region, SECURITY_PIN, created_date, updated_date)
VALUES ('mickey@tradex.com', '$2a$10$.F2QPGfG8YzHRqQ1o5uuLeHiWPxLwinmFz67TIEg.4VS8PHITiHxy', true, '{"fullName":"mickey mouse", "address":"wallstreet", "phone":"+10000007", "country":"USA", "gender":"MALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED" }', 'boston', 9999, '2022-10-26 11:30:47.624492', '2022-10-26 11:30:47.624492'),
       ('donald@tradex.com', '$2a$10$wK4JTnG6H02BkTBpyqbfi.O1YyMC.81FM1biSEtrvqRbA005/mR.m', true, '{"fullName":"donald duck", "address":"wallstreet", "phone":"+10000009", "country":"USA", "gender":"MALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED"}', 'sydney', 8888, '2022-10-26 11:31:41.758602', '2022-10-26 11:31:41.758602');

delete
from trade_symbol
where enabled = true;

insert into trade_symbol (trade_symbol_id, symbol, company, exchange, price_time, open_price, low_price, high_price, close_price, market_cap, volume, avg_volume, enabled, created_date)
values (45, 'TTE', 'TotalEnergies SE', 'NYSE', '2023-01-24 04:48:10.912648', 64.0100, 63.9100, 64.5300, 63.8000, 158704058368.0000, 1134656.0000, 1644714.0000, true, '2022-11-01 12:52:45.944782'),
       (5, 'GOOGL', 'Alphabet', 'NYSE', '2023-01-24 04:47:44.696781', 97.9500, 97.5000, 100.0400, 98.0200, 1299385548800.0000, 40005055.0000, 33779885.0000, true, '2022-11-01 12:52:45.944782'),
       (43, 'HSBC', 'HSBC Holdings', 'NYSE', '2023-01-24 04:47:47.141401', 36.6100, 36.5900, 36.8200, 36.8400, 147046924288.0000, 2078983.0000, 2433242.0000, true, '2022-11-01 12:52:45.944782'),
       (86, 'ACN', 'Accenture', 'NYSE', '2023-01-24 04:47:24.180433', 278.5600, 277.4330, 282.8700, 280.4700, 176053469184.0000, 2584085.0000, 2180232.0000, true, '2022-11-01 12:52:45.944782'),
       (91, 'ASML', 'ASML Holding', 'NYSE', '2023-01-24 04:47:30.902759', 658.0500, 656.9300, 676.2800, 648.8500, 272532717568.0000, 1780878.0000, 1194713.0000, true, '2022-11-01 12:52:45.944782'),
       (16, 'ABBV', 'AbbVie', 'NYSE', '2023-01-24 04:47:20.165289', 149.8600, 148.0540, 150.4300, 149.5900, 262707707904.0000, 7562370.0000, 5574046.0000, true, '2022-11-01 12:52:45.944782'),
       (15, 'CVX', 'Chevron', 'NYSE', '2023-01-24 04:47:37.925931', 181.2100, 180.0300, 182.5460, 180.9000, 349331390464.0000, 7635682.0000, 7833039.0000, true, '2022-11-01 12:52:45.944782'),
       (6, 'FMX', 'Fomento Economico Mexicano', 'NYSE', '2023-01-24 04:47:42.364776', 84.6100, 84.3200, 86.4100, 84.1300, 149177925632.0000, 376308.0000, 594436.0000, true, '2022-11-01 12:52:45.944782'),
       (51, 'DHR', 'Danaher', 'NYSE', '2023-01-24 04:47:40.157442', 273.9500, 272.3800, 279.3200, 274.4000, 201645752320.0000, 2005546.0000, 2566947.0000, true, '2022-11-01 12:52:45.944782'),
       (7, 'JPM', 'JPMorgan Chase & Co.', 'NYSE', '2023-01-24 04:47:52.06264', 135.1150, 134.8200, 137.9600, 135.0800, 402791366656.0000, 10394887.0000, 10676009.0000, true, '2022-11-01 12:52:45.944782');

insert into trade_symbol (trade_symbol_id, symbol, company, exchange, price_time, open_price, low_price, high_price, close_price, market_cap, volume, avg_volume, enabled, created_date)
values (32, 'JNJ', 'Johnson & Johnson', 'NYSE', '2023-01-24 04:47:49.639672', 169.1000, 167.9470, 169.6300, 168.7400, 440043110400.0000, 8398461.0000, 6461881.0000, true, '2022-11-01 12:52:45.944782'),
       (48, 'WMT', 'Walmart', 'NYSE', '2023-01-24 04:48:18.101977', 140.4600, 140.2000, 143.0100, 140.5400, 384671547392.0000, 4377999.0000, 6335368.0000, true, '2022-11-01 12:52:45.944782'),
       (19, 'AXP', 'American Express Company', 'NYSE', '2023-01-24 04:47:33.205083', 152.0000, 151.5500, 155.0600, 151.6000, 115073884160.0000, 3281304.0000, 2819185.0000, true, '2022-11-01 12:52:45.944782'),
       (70, 'RTX', 'Raytheon Technologies', 'NYSE', '2023-01-24 04:47:56.718921', 94.8000, 94.5100, 96.9300, 94.3600, 141493272576.0000, 5595639.0000, 4559396.0000, true, '2022-11-01 12:52:45.944782'),
       (66, 'SNY', 'Sanofi', 'NYSE', '2023-01-24 04:48:03.80486', 48.8300, 48.5600, 48.9500, 49.0800, 122288898048.0000, 2880556.0000, 2583342.0000, true, '2022-11-01 12:52:45.944782'),
       (30, 'UNH', 'UnitedHealth Group', 'NYSE', '2023-01-24 04:48:13.486651', 486.6800, 481.3900, 490.1000, 486.7200, 453916065792.0000, 3395268.0000, 3336737.0000, true, '2022-11-01 12:52:45.944782'),
       (75, 'XOM', 'Exxon Mobil', 'NYSE', '2023-01-24 04:48:20.530362', 113.6400, 112.6400, 114.5900, 113.3500, 464378363904.0000, 16440556.0000, 17573627.0000, true, '2022-11-01 12:52:45.944782'),
       (63, 'SHEL', 'Shell', 'NYSE', '2023-01-24 04:48:01.285215', 58.3650, 58.3500, 58.8300, 58.9000, 204680855552.0000, 3212385.0000, 4497627.0000, true, '2022-11-01 12:52:45.944782'),
       (67, 'CSCO', 'Cisco Systems', 'NYSE', '2023-01-24 04:47:35.481168', 46.8950, 46.8100, 47.8650, 46.7800, 195134750720.0000, 15180548.0000, 18502655.0000, true, '2022-11-01 12:52:45.944782'),
       (93, 'SAP', 'SAP SE', 'NYSE', '2023-01-24 04:47:58.964997', 115.6400, 115.5950, 117.0200, 117.1200, 136504016896.0000, 1056277.0000, 1058232.0000, true, '2022-11-01 12:52:45.944782');
insert into trade_symbol (trade_symbol_id, symbol, company, exchange, price_time, open_price, low_price, high_price, close_price, market_cap, volume, avg_volume, enabled, created_date)
values (22, 'NVDA', 'NVIDIA', 'NYSE', '2023-01-24 04:47:54.262921', 180.6400, 178.1750, 192.4500, 178.3900, 478289526784.0000, 65516259.0000, 46990262.0000, true, '2022-11-01 12:52:45.944782'),
       (39, 'V', 'Visa Inc.', 'NYSE', '2023-01-24 04:48:15.720844', 224.6000, 223.1540, 226.2000, 224.3100, 474728038400.0000, 5492244.0000, 7037685.0000, true, '2022-11-01 12:52:45.944782'),
       (28, 'TSM', 'Taiwan Semiconductor Manufacturing Company', 'NYSE', '2023-01-24 04:48:08.391189', 91.7600, 91.7200, 95.7300, 91.0300, 496092774400.0000, 22763780.0000, 15194103.0000, true, '2022-11-01 12:52:45.944782'),
       (74, 'AMZN', 'Amazon.com', 'NYSE', '2023-01-24 04:47:28.716995', 97.5600, 95.8700, 97.7450, 97.2500, 994869772288.0000, 76501103.0000, 85649260.0000, true, '2022-11-01 12:52:45.944782'),
       (95, 'TSLA', 'Tesla', 'NYSE', '2023-01-24 04:48:06.060002', 135.8700, 134.2700, 145.3790, 133.4200, 453926551552.0000, 201802953.0000, 127514222.0000, true, '2022-11-01 12:52:45.944782'),
       (20, 'ADP', 'Automatic Data Processing', 'NYSE', '2023-01-24 04:47:26.463123', 236.4800, 235.4400, 241.8600, 237.1700, 99956957184.0000, 1654699.0000, 1667572.0000, true, '2022-11-01 12:52:45.944782');

update trade_symbol
set price_time   = now(),
    created_date = now()
where enabled = true;
commit;
truncate table trade_symbol_price_historic;

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2023-01-23 14:30:00', 278.7700, 278.2000, 279.7000, 278.7700, '1H'),
       (6, '2023-01-23 13:30:00', 85.6700, 85.5500, 85.7400, 85.6700, '1H'),
       (32, '2023-01-18 00:00:00', 169.7600, 169.7100, 172.0000, 169.7600, '1DAY'),
       (74, '2023-01-16 00:00:00', 97.2500, 92.8600, 99.3200, 97.2500, '1WEEK'),
       (93, '2023-01-23 09:30:00', 116.2000, 115.5950, 23.2680, 116.2000, '1H'),
       (70, '2023-01-23 09:30:00', 95.9160, 94.5100, 95.9200, 95.9160, '1H'),
       (15, '2023-01-18 00:00:00', 177.2300, 176.9400, 182.3800, 177.2300, '1DAY'),
       (30, '2022-12-26 00:00:00', 530.1800, 524.8400, 538.1500, 530.1800, '1WEEK'),
       (66, '2023-01-02 00:00:00', 48.1400, 47.3800, 49.3000, 48.1400, '1WEEK'),
       (86, '2023-01-23 09:30:00', 280.2400, 277.4330, 280.7150, 280.2400, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (20, '2023-01-23 14:00:00', 239.9000, 239.2500, 241.3000, 239.9000, '90MIN'),
       (86, '2023-01-23 00:00:00', 279.3400, 277.4300, 282.8700, 279.3400, '1WEEK'),
       (75, '2023-01-02 00:00:00', 110.5300, 104.7600, 111.9900, 110.5300, '1WEEK'),
       (32, '2022-04-01 00:00:00', 174.0760, 164.0240, 183.0780, 174.0760, '3MONTH'),
       (45, '2022-04-01 00:00:00', 49.8150, 44.3450, 57.8690, 49.8150, '3MONTH'),
       (63, '2023-01-19 00:00:00', 59.1300, 58.1550, 59.2100, 59.1300, '1DAY'),
       (86, '2022-01-01 00:00:00', 331.3360, 292.1630, 353.8060, 331.3360, '3MONTH'),
       (86, '2022-12-26 00:00:00', 265.7530, 261.5300, 269.7670, 265.7530, '1WEEK'),
       (67, '2023-01-23 00:00:00', 47.5000, 46.8100, 28.7220, 47.5000, '1WEEK'),
       (45, '2023-01-23 09:30:00', 64.1300, 63.9400, 64.2200, 64.1300, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (7, '2022-04-01 00:00:00', 109.0140, 107.3870, 133.0210, 109.0140, '3MONTH'),
       (20, '2022-12-26 00:00:00', 238.8600, 236.0500, 242.7400, 238.8600, '1WEEK'),
       (6, '2023-01-23 00:00:00', 85.5800, 84.3200, 86.4100, 85.5800, '1DAY'),
       (15, '2022-12-26 00:00:00', 179.4900, 175.9300, 180.2300, 179.4900, '1WEEK'),
       (28, '2023-01-01 00:00:00', 95.6600, 73.7700, 95.7300, 95.6600, '3MONTH'),
       (48, '2023-01-02 00:00:00', 146.7800, 142.0700, 147.5500, 146.7800, '1WEEK'),
       (66, '2023-01-23 12:30:00', 48.7530, 48.7000, 48.7950, 48.7530, '1H'),
       (19, '2022-10-01 00:00:00', 146.6800, 129.7040, 160.3800, 146.6800, '3MONTH'),
       (51, '2023-01-23 12:30:00', 278.3700, 278.2000, 279.2000, 278.3700, '1H'),
       (91, '2022-12-26 00:00:00', 546.4000, 529.0100, 555.3700, 546.4000, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (6, '2023-01-16 00:00:00', 84.1300, 82.7300, 87.8800, 84.1300, '1WEEK'),
       (86, '2023-01-23 14:00:00', 278.7700, 278.2000, 280.3700, 278.7700, '90MIN'),
       (43, '2022-07-01 00:00:00', 25.7150, 25.2810, 9.9990, 25.7150, '3MONTH'),
       (28, '2023-01-09 00:00:00', 86.8000, 80.0600, 88.8300, 86.8000, '1WEEK'),
       (95, '2023-01-23 15:30:00', 143.7500, 141.8500, 86.3400, 143.7500, '90MIN'),
       (30, '2023-01-02 00:00:00', 490.0000, 487.5400, 525.6300, 490.0000, '1WEEK'),
       (43, '2023-01-23 15:30:00', 36.8200, 36.7000, 22.0920, 36.8200, '90MIN'),
       (93, '2023-01-23 11:00:00', 116.5400, 116.4500, 23.4040, 116.5400, '90MIN'),
       (86, '2023-01-18 00:00:00', 277.9000, 276.9800, 287.2800, 277.9000, '1DAY'),
       (66, '2023-01-18 00:00:00', 49.1600, 49.1600, 49.7900, 49.1600, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (32, '2023-01-19 00:00:00', 169.5300, 168.7100, 171.2800, 169.5300, '1DAY'),
       (28, '2023-01-23 11:00:00', 95.2160, 94.5100, 95.7300, 95.2160, '90MIN'),
       (66, '2023-01-20 00:00:00', 49.0800, 48.9300, 49.2900, 49.0800, '1DAY'),
       (16, '2022-07-01 00:00:00', 130.3870, 130.2710, 150.8280, 130.3870, '3MONTH'),
       (5, '2023-01-23 09:30:00', 99.0600, 97.5000, 99.2000, 99.0600, '1H'),
       (19, '2023-01-23 12:30:00', 154.3150, 153.6900, 154.3200, 154.3150, '1H'),
       (39, '2022-10-01 00:00:00', 207.2970, 174.2110, 219.4890, 207.2970, '3MONTH'),
       (7, '2023-01-09 00:00:00', 143.0100, 134.8000, 143.4900, 143.0100, '1WEEK'),
       (6, '2023-01-02 00:00:00', 80.2300, 77.2100, 80.8500, 80.2300, '1WEEK'),
       (95, '2023-01-23 09:30:00', 139.9580, 134.2700, 70.1750, 139.9580, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2023-01-23 13:30:00', 279.4360, 279.2200, 281.1900, 279.4360, '1H'),
       (74, '2022-07-01 00:00:00', 113.0000, 105.8500, 146.5700, 113.0000, '3MONTH'),
       (15, '2023-01-23 15:30:00', 180.6600, 180.0300, 180.8500, 180.6600, '1H'),
       (70, '2023-01-23 15:30:00', 96.2500, 96.0150, 96.4700, 96.2500, '90MIN'),
       (5, '2023-01-16 00:00:00', 98.0200, 90.0500, 98.3000, 98.0200, '1WEEK'),
       (43, '2023-01-23 15:30:00', 36.8200, 36.7000, 18.4100, 36.8200, '1H'),
       (32, '2023-01-23 13:30:00', 168.5500, 168.4500, 169.1850, 168.5500, '1H'),
       (15, '2022-12-19 00:00:00', 177.4000, 173.6900, 177.5800, 177.4000, '1WEEK'),
       (51, '2023-01-01 00:00:00', 277.0000, 242.4800, 279.3200, 277.0000, '3MONTH'),
       (67, '2023-01-18 00:00:00', 46.9000, 46.8900, 19.3080, 46.9000, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2022-12-19 00:00:00', 265.0060, 262.2370, 265.6230, 265.0060, '1WEEK'),
       (7, '2023-01-17 00:00:00', 140.8000, 139.2300, 141.5400, 140.8000, '1DAY'),
       (86, '2023-01-19 00:00:00', 272.8900, 272.5900, 278.9500, 272.8900, '1DAY'),
       (22, '2022-04-01 00:00:00', 151.4740, 148.5060, 275.3690, 151.4740, '3MONTH'),
       (16, '2023-01-23 09:30:00', 149.1000, 148.6400, 150.4300, 149.1000, '90MIN'),
       (67, '2023-01-01 00:00:00', 47.5000, 45.6700, 14.8290, 47.5000, '3MONTH'),
       (15, '2023-01-01 00:00:00', 180.6600, 170.8500, 182.5500, 180.6600, '3MONTH'),
       (45, '2023-01-16 00:00:00', 63.8000, 62.8200, 65.0500, 63.8000, '1WEEK'),
       (66, '2023-01-23 12:30:00', 48.8200, 48.7000, 48.8500, 48.8200, '90MIN'),
       (45, '2023-01-23 12:30:00', 64.4000, 64.1700, 64.4000, 64.4000, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (15, '2023-01-09 00:00:00', 177.5600, 173.7200, 178.3700, 177.5600, '1WEEK'),
       (16, '2023-01-23 10:30:00', 149.1500, 149.0950, 149.9100, 149.1500, '1H'),
       (5, '2023-01-01 00:00:00', 99.7900, 84.8600, 100.0400, 99.7900, '3MONTH'),
       (43, '2023-01-17 00:00:00', 36.3000, 36.1000, 21.9300, 36.3000, '1DAY'),
       (51, '2023-01-23 00:00:00', 277.0000, 272.3800, 279.3200, 277.0000, '1WEEK'),
       (95, '2023-01-23 09:30:00', 138.7900, 134.2700, 27.9400, 138.7900, '1H'),
       (30, '2023-01-23 15:30:00', 485.8100, 485.0500, 486.9800, 485.8100, '90MIN'),
       (63, '2022-04-01 00:00:00', 50.8980, 47.3450, 60.0330, 50.8980, '3MONTH'),
       (48, '2023-01-20 00:00:00', 140.5400, 138.1700, 140.7800, 140.5400, '1DAY'),
       (91, '2022-10-01 00:00:00', 544.7600, 362.0600, 641.4280, 544.7600, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (32, '2023-01-23 12:30:00', 168.5500, 168.3000, 168.7400, 168.5500, '1H'),
       (20, '2023-01-09 00:00:00', 245.3600, 237.4600, 246.6700, 245.3600, '1WEEK'),
       (5, '2023-01-23 14:30:00', 99.2190, 98.6000, 99.3300, 99.2190, '1H'),
       (48, '2023-01-23 13:30:00', 142.3200, 142.2900, 143.0100, 142.3200, '1H'),
       (28, '2023-01-23 12:30:00', 95.3150, 95.0600, 95.5000, 95.3150, '1H'),
       (39, '2023-01-16 00:00:00', 224.3100, 217.5000, 224.9900, 224.3100, '1WEEK'),
       (6, '2023-01-23 14:30:00', 85.7100, 85.5900, 85.9000, 85.7100, '1H'),
       (45, '2022-12-19 00:00:00', 62.4830, 61.7710, 62.5120, 62.4830, '1WEEK'),
       (51, '2023-01-23 15:30:00', 277.0000, 276.0100, 277.4300, 277.0000, '1H'),
       (95, '2023-01-23 14:00:00', 142.4600, 141.3300, 58.1080, 142.4600, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (15, '2023-01-16 00:00:00', 180.9000, 176.1600, 182.3800, 180.9000, '1WEEK'),
       (15, '2023-01-23 14:30:00', 180.4600, 180.1200, 181.0500, 180.4600, '1H'),
       (45, '2022-12-26 00:00:00', 61.3660, 61.0000, 63.2830, 61.3660, '1WEEK'),
       (15, '2023-01-23 15:30:00', 180.6600, 180.0300, 180.8500, 180.6600, '90MIN'),
       (20, '2023-01-23 09:30:00', 238.2350, 235.4400, 238.2900, 238.2350, '1H'),
       (48, '2023-01-23 15:30:00', 142.6400, 142.4300, 142.8350, 142.6400, '90MIN'),
       (43, '2023-01-23 00:00:00', 36.8200, 36.5900, 7.3640, 36.8200, '1DAY'),
       (19, '2023-01-17 00:00:00', 153.7500, 153.3700, 156.1000, 153.7500, '1DAY'),
       (74, '2023-01-18 00:00:00', 95.4600, 95.3800, 99.3200, 95.4600, '1DAY'),
       (45, '2023-01-23 09:30:00', 64.5150, 63.9400, 64.5300, 64.5150, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (5, '2023-01-23 15:30:00', 99.7900, 99.1950, 100.0000, 99.7900, '90MIN'),
       (63, '2023-01-23 12:30:00', 58.7290, 58.6200, 58.7400, 58.7290, '1H'),
       (28, '2022-12-19 00:00:00', 74.8900, 74.1700, 75.3500, 74.8900, '1WEEK'),
       (74, '2023-01-23 14:30:00', 97.0910, 96.7100, 97.3300, 97.0910, '1H'),
       (7, '2023-01-23 13:30:00', 137.4400, 137.3150, 137.9600, 137.4400, '1H'),
       (30, '2023-01-01 00:00:00', 485.8100, 474.7500, 525.6300, 485.8100, '3MONTH'),
       (67, '2023-01-17 00:00:00', 48.0800, 47.9500, 29.3640, 48.0800, '1DAY'),
       (45, '2023-01-23 11:30:00', 64.2200, 64.0580, 64.3300, 64.2200, '1H'),
       (16, '2023-01-18 00:00:00', 149.2000, 149.0700, 152.4200, 149.2000, '1DAY'),
       (45, '2023-01-23 12:30:00', 64.3050, 64.1700, 64.4000, 64.3050, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (15, '2022-01-01 00:00:00', 157.1690, 125.9720, 168.6840, 157.1690, '3MONTH'),
       (74, '2023-01-20 00:00:00', 97.2500, 93.2000, 97.3500, 97.2500, '1DAY'),
       (63, '2023-01-23 11:00:00', 58.6200, 58.5450, 58.8250, 58.6200, '90MIN'),
       (39, '2023-01-23 11:00:00', 225.2150, 224.7790, 226.2000, 225.2150, '90MIN'),
       (30, '2023-01-23 14:00:00', 486.0500, 485.2400, 488.2900, 486.0500, '90MIN'),
       (51, '2023-01-23 09:30:00', 276.7500, 272.3800, 277.3400, 276.7500, '1H'),
       (20, '2023-01-23 10:30:00', 239.3200, 237.5600, 240.6700, 239.3200, '1H'),
       (51, '2022-12-26 00:00:00', 265.1640, 256.5320, 268.2710, 265.1640, '1WEEK'),
       (51, '2022-07-01 00:00:00', 257.7990, 242.3880, 303.2420, 257.7990, '3MONTH'),
       (70, '2023-01-23 12:30:00', 96.8300, 96.1600, 96.9300, 96.8300, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (16, '2023-01-23 14:30:00', 148.4260, 148.4000, 149.2620, 148.4260, '1H'),
       (19, '2023-01-23 15:30:00', 154.0000, 153.6200, 154.2700, 154.0000, '90MIN'),
       (93, '2022-04-01 00:00:00', 88.1320, 86.4220, 98.4402, 88.1320, '3MONTH'),
       (86, '2022-10-01 00:00:00', 264.5760, 240.8890, 302.5420, 264.5760, '3MONTH'),
       (39, '2023-01-18 00:00:00', 219.4600, 219.4400, 224.9900, 219.4600, '1DAY'),
       (67, '2023-01-23 14:30:00', 47.4470, 47.3350, 19.0620, 47.4470, '1H'),
       (19, '2023-01-23 10:30:00', 153.9200, 153.2400, 154.6700, 153.9200, '1H'),
       (51, '2023-01-23 11:30:00', 278.4700, 277.5000, 278.7600, 278.4700, '1H'),
       (16, '2023-01-09 00:00:00', 152.1540, 149.9150, 164.7140, 152.1540, '1WEEK'),
       (30, '2023-01-19 00:00:00', 484.3600, 477.3900, 486.3900, 484.3600, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (91, '2023-01-18 00:00:00', 652.2900, 651.5400, 676.1100, 652.2900, '1DAY'),
       (6, '2022-07-01 00:00:00', 62.0140, 58.0410, 68.0420, 62.0140, '3MONTH'),
       (51, '2023-01-09 00:00:00', 273.6200, 253.5500, 275.0700, 273.6200, '1WEEK'),
       (32, '2023-01-23 09:30:00', 169.0500, 167.9470, 169.2800, 169.0500, '1H'),
       (75, '2023-01-23 14:30:00', 112.8650, 112.7300, 113.6300, 112.8650, '1H'),
       (39, '2023-01-23 14:00:00', 223.8000, 223.3800, 224.9900, 223.8000, '90MIN'),
       (30, '2022-12-19 00:00:00', 531.3100, 522.9000, 531.3100, 531.3100, '1WEEK'),
       (7, '2023-01-19 00:00:00', 134.7500, 133.5900, 135.9000, 134.7500, '1DAY'),
       (5, '2022-01-01 00:00:00', 139.0680, 124.9530, 151.5460, 139.0680, '3MONTH'),
       (48, '2023-01-01 00:00:00', 142.6400, 138.1700, 147.8600, 142.6400, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (20, '2023-01-23 00:00:00', 240.9600, 235.4400, 241.8600, 240.9600, '1WEEK'),
       (19, '2023-01-23 00:00:00', 154.0000, 151.5500, 155.0600, 154.0000, '1DAY'),
       (66, '2023-01-23 15:30:00', 48.6300, 48.5900, 48.7300, 48.6300, '1H'),
       (75, '2023-01-23 10:30:00', 114.3900, 113.7500, 114.5900, 114.3900, '1H'),
       (95, '2023-01-16 00:00:00', 133.4200, 124.3100, 41.0040, 133.4200, '1WEEK'),
       (51, '2023-01-23 12:30:00', 278.6300, 278.2000, 279.3200, 278.6300, '90MIN'),
       (43, '2023-01-23 11:00:00', 36.6650, 36.6200, 7.3560, 36.6650, '90MIN'),
       (32, '2023-01-23 00:00:00', 168.3100, 167.9500, 169.6300, 168.3100, '1DAY'),
       (86, '2023-01-23 12:30:00', 280.4600, 280.1100, 281.2150, 280.4600, '90MIN'),
       (67, '2023-01-23 12:30:00', 47.8300, 47.5600, 38.2640, 47.8300, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (91, '2023-01-23 12:30:00', 672.4700, 668.0720, 674.5000, 672.4700, '90MIN'),
       (43, '2023-01-02 00:00:00', 34.3100, 31.5100, 6.8680, 34.3100, '1WEEK'),
       (39, '2022-12-19 00:00:00', 205.8300, 203.5200, 206.2400, 205.8300, '1WEEK'),
       (70, '2023-01-09 00:00:00', 98.6800, 97.5100, 102.7800, 98.6800, '1WEEK'),
       (67, '2023-01-23 00:00:00', 47.5000, 46.8100, 9.5740, 47.5000, '1DAY'),
       (39, '2022-07-01 00:00:00', 176.9400, 174.1320, 217.1990, 176.9400, '3MONTH'),
       (22, '2023-01-18 00:00:00', 173.7700, 172.8200, 178.7300, 173.7700, '1DAY'),
       (15, '2023-01-23 00:00:00', 180.6600, 180.0300, 182.5500, 180.6600, '1WEEK'),
       (45, '2023-01-20 00:00:00', 63.8000, 63.3900, 63.8900, 63.8000, '1DAY'),
       (67, '2023-01-23 10:30:00', 47.4850, 47.2400, 4.7620, 47.4850, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2023-01-01 00:00:00', 279.3400, 261.3700, 287.9700, 279.3400, '3MONTH'),
       (43, '2023-01-23 12:30:00', 36.6850, 36.6500, 29.3480, 36.6850, '1H'),
       (66, '2022-07-01 00:00:00', 38.0200, 36.9100, 51.7000, 38.0200, '3MONTH'),
       (95, '2023-01-23 13:30:00', 144.0100, 143.6100, 130.8411, 144.0100, '1H'),
       (74, '2023-01-23 11:30:00', 97.2500, 97.0300, 97.5700, 97.2500, '1H'),
       (67, '2022-10-01 00:00:00', 46.8270, 37.9420, 34.8915, 46.8270, '3MONTH'),
       (93, '2023-01-23 12:30:00', 116.5520, 116.4550, 93.4960, 116.5520, '90MIN'),
       (48, '2023-01-17 00:00:00', 144.4100, 144.2000, 145.7100, 144.4100, '1DAY'),
       (91, '2023-01-23 10:30:00', 668.3800, 664.7100, 670.3000, 668.3800, '1H'),
       (93, '2023-01-23 14:00:00', 116.6900, 116.1650, 46.7720, 116.6900, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (95, '2022-12-26 00:00:00', 123.1800, 108.2400, 124.4800, 123.1800, '1WEEK'),
       (7, '2023-01-23 12:30:00', 137.8450, 137.0500, 137.9600, 137.8450, '90MIN'),
       (66, '2023-01-23 10:30:00', 48.7850, 48.7250, 48.9500, 48.7850, '1H'),
       (45, '2023-01-23 10:30:00', 64.3000, 64.0310, 64.5300, 64.3000, '1H'),
       (63, '2022-12-26 00:00:00', 56.9500, 56.3900, 58.0400, 56.9500, '1WEEK'),
       (7, '2023-01-23 11:30:00', 137.0700, 136.9300, 137.3300, 137.0700, '1H'),
       (67, '2023-01-16 00:00:00', 46.7800, 45.6700, 14.6820, 46.7800, '1WEEK'),
       (43, '2023-01-23 14:00:00', 36.7150, 36.6250, 14.6960, 36.7150, '90MIN'),
       (16, '2023-01-23 11:00:00', 149.1250, 148.5750, 149.9100, 149.1250, '90MIN'),
       (86, '2023-01-09 00:00:00', 280.9910, 270.1650, 286.7970, 280.9910, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (63, '2023-01-17 00:00:00', 59.4000, 59.2450, 60.1200, 59.4000, '1DAY'),
       (7, '2022-07-01 00:00:00', 101.9120, 101.8150, 121.1640, 101.9120, '3MONTH'),
       (7, '2022-12-26 00:00:00', 133.1170, 129.5930, 133.3450, 133.1170, '1WEEK'),
       (43, '2023-01-23 09:30:00', 36.7050, 36.5900, 18.3750, 36.7050, '90MIN'),
       (20, '2023-01-23 11:30:00', 239.4250, 238.7150, 239.8600, 239.4250, '1H'),
       (20, '2023-01-23 13:30:00', 239.9800, 239.7700, 241.8600, 239.9800, '1H'),
       (63, '2023-01-18 00:00:00', 58.3900, 58.3250, 60.1400, 58.3900, '1DAY'),
       (66, '2023-01-23 14:00:00', 48.6600, 48.5600, 48.8150, 48.6600, '90MIN'),
       (7, '2023-01-23 10:30:00', 137.1700, 136.0600, 137.7950, 137.1700, '1H'),
       (6, '2022-10-01 00:00:00', 77.2040, 61.4010, 79.5850, 77.2040, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (67, '2023-01-23 14:00:00', 47.4470, 47.3350, 19.1180, 47.4470, '90MIN'),
       (70, '2023-01-23 11:30:00', 96.2070, 95.8200, 96.5300, 96.2070, '1H'),
       (15, '2023-01-23 11:00:00', 181.8500, 181.5800, 182.5460, 181.8500, '90MIN'),
       (15, '2023-01-17 00:00:00', 180.4900, 178.1800, 181.3200, 180.4900, '1DAY'),
       (51, '2023-01-18 00:00:00', 270.6200, 269.7500, 278.7900, 270.6200, '1DAY'),
       (5, '2023-01-23 00:00:00', 99.7900, 97.5000, 100.0400, 99.7900, '1WEEK'),
       (91, '2023-01-23 13:30:00', 670.4050, 670.1500, 674.5000, 670.4050, '1H'),
       (95, '2023-01-23 11:00:00', 142.0400, 139.9500, 28.4840, 142.0400, '90MIN'),
       (5, '2023-01-23 15:30:00', 99.7900, 99.1950, 100.0000, 99.7900, '1H'),
       (93, '2023-01-16 00:00:00', 117.1200, 114.6500, 35.6400, 117.1200, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (43, '2022-10-01 00:00:00', 31.1600, 24.7700, 22.0500, 31.1600, '3MONTH'),
       (20, '2023-01-17 00:00:00', 236.7800, 236.1400, 244.3500, 236.7800, '1DAY'),
       (6, '2022-12-26 00:00:00', 78.1200, 77.7500, 80.2000, 78.1200, '1WEEK'),
       (20, '2023-01-23 12:30:00', 241.3450, 239.2250, 241.8600, 241.3450, '90MIN'),
       (32, '2023-01-23 11:00:00', 168.4400, 168.1300, 169.6300, 168.4400, '90MIN'),
       (63, '2023-01-09 00:00:00', 59.6100, 57.6900, 59.9500, 59.6100, '1WEEK'),
       (28, '2023-01-23 15:30:00', 95.6600, 94.7600, 95.7100, 95.6600, '90MIN'),
       (43, '2023-01-20 00:00:00', 36.8400, 36.4100, 14.7400, 36.8400, '1DAY'),
       (70, '2023-01-23 12:30:00', 96.5400, 96.1600, 96.7000, 96.5400, '1H'),
       (48, '2022-10-01 00:00:00', 141.2570, 127.5890, 154.0590, 141.2570, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (75, '2023-01-23 11:30:00', 113.9300, 113.7500, 114.5000, 113.9300, '1H'),
       (19, '2023-01-01 00:00:00', 154.0000, 144.2000, 156.2500, 154.0000, '3MONTH'),
       (66, '2023-01-23 09:30:00', 48.8500, 48.7400, 48.9200, 48.8500, '1H'),
       (91, '2023-01-23 15:30:00', 676.0300, 672.2600, 676.2800, 676.0300, '90MIN'),
       (93, '2023-01-23 09:30:00', 116.4200, 115.5950, 58.2450, 116.4200, '90MIN'),
       (63, '2023-01-16 00:00:00', 58.9000, 58.1550, 60.1400, 58.9000, '1WEEK'),
       (5, '2023-01-23 00:00:00', 99.7900, 97.5000, 100.0400, 99.7900, '1DAY'),
       (30, '2023-01-23 15:30:00', 485.8100, 485.0500, 486.9800, 485.8100, '1H'),
       (39, '2023-01-23 11:30:00', 225.2150, 225.1000, 226.2000, 225.2150, '1H'),
       (28, '2023-01-23 11:30:00', 95.2160, 94.9100, 95.7300, 95.2160, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (22, '2022-10-01 00:00:00', 146.1030, 108.1020, 187.8520, 146.1030, '3MONTH'),
       (5, '2023-01-19 00:00:00', 93.0500, 90.6300, 93.6100, 93.0500, '1DAY'),
       (45, '2023-01-01 00:00:00', 64.1200, 60.6600, 65.0500, 64.1200, '3MONTH'),
       (7, '2023-01-23 00:00:00', 137.2700, 134.8200, 137.9600, 137.2700, '1DAY'),
       (48, '2022-07-01 00:00:00', 128.6530, 119.0900, 141.5670, 128.6530, '3MONTH'),
       (75, '2023-01-23 13:30:00', 113.4800, 113.4400, 113.9950, 113.4800, '1H'),
       (32, '2023-01-20 00:00:00', 168.7400, 167.4800, 170.1100, 168.7400, '1DAY'),
       (70, '2023-01-23 11:00:00', 96.2070, 95.8200, 96.5300, 96.2070, '90MIN'),
       (20, '2022-01-01 00:00:00', 223.2900, 188.6690, 227.7350, 223.2900, '3MONTH'),
       (20, '2023-01-23 14:30:00', 239.9000, 239.2500, 240.3950, 239.9000, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (48, '2023-01-23 15:30:00', 142.6400, 142.4300, 142.8350, 142.6400, '1H'),
       (32, '2023-01-23 10:30:00', 169.1300, 168.9400, 169.6300, 169.1300, '1H'),
       (7, '2023-01-20 00:00:00', 135.0800, 133.5500, 135.4900, 135.0800, '1DAY'),
       (91, '2023-01-17 00:00:00', 658.1900, 651.1000, 662.9800, 658.1900, '1DAY'),
       (5, '2023-01-09 00:00:00', 92.1200, 85.8300, 92.1900, 92.1200, '1WEEK'),
       (45, '2023-01-17 00:00:00', 63.9100, 63.8600, 64.9700, 63.9100, '1DAY'),
       (39, '2022-01-01 00:00:00', 220.1050, 185.2690, 234.0790, 220.1050, '3MONTH'),
       (95, '2022-04-01 00:00:00', 224.4730, 206.8570, 345.8610, 224.4730, '3MONTH'),
       (91, '2023-01-23 09:30:00', 666.4000, 656.8500, 666.9800, 666.4000, '1H'),
       (28, '2023-01-02 00:00:00', 78.0700, 73.7700, 78.7400, 78.0700, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (22, '2023-01-23 15:30:00', 191.9300, 190.4700, 192.2900, 191.9300, '90MIN'),
       (22, '2023-01-23 09:30:00', 188.0700, 178.1750, 188.8000, 188.0700, '90MIN'),
       (20, '2023-01-19 00:00:00', 233.3200, 232.2000, 234.9300, 233.3200, '1DAY'),
       (16, '2023-01-23 12:30:00', 149.6250, 148.8500, 149.6800, 149.6250, '90MIN'),
       (48, '2023-01-09 00:00:00', 145.2900, 143.7300, 147.8600, 145.2900, '1WEEK'),
       (43, '2023-01-23 09:30:00', 36.7400, 36.5900, 7.3480, 36.7400, '1H'),
       (74, '2023-01-02 00:00:00', 86.0800, 81.4300, 86.9800, 86.0800, '1WEEK'),
       (48, '2023-01-23 11:30:00', 142.5300, 142.0900, 142.6500, 142.5300, '1H'),
       (75, '2023-01-19 00:00:00', 111.3200, 109.5800, 112.1300, 111.3200, '1DAY'),
       (30, '2023-01-23 12:30:00', 488.2620, 485.2600, 488.9120, 488.2620, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (30, '2023-01-23 10:30:00', 488.0200, 487.1300, 490.1000, 488.0200, '1H'),
       (70, '2022-12-26 00:00:00', 100.9200, 99.0800, 101.0100, 100.9200, '1WEEK'),
       (66, '2023-01-23 13:30:00', 48.7200, 48.7120, 48.8500, 48.7200, '1H'),
       (66, '2023-01-23 09:30:00', 48.7300, 48.7250, 48.9200, 48.7300, '90MIN'),
       (67, '2023-01-09 00:00:00', 48.8800, 48.2200, 44.4870, 48.8800, '1WEEK'),
       (20, '2023-01-01 00:00:00', 240.9600, 232.1800, 246.6700, 240.9600, '3MONTH'),
       (15, '2023-01-23 09:30:00', 181.8000, 180.4700, 181.9900, 181.8000, '90MIN'),
       (19, '2023-01-23 11:00:00', 153.7500, 153.3220, 154.6700, 153.7500, '90MIN'),
       (74, '2022-01-01 00:00:00', 162.9970, 133.5720, 170.8310, 162.9970, '3MONTH'),
       (95, '2023-01-18 00:00:00', 128.7800, 127.0100, 54.6720, 128.7800, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (39, '2023-01-23 13:30:00', 224.0800, 224.0400, 225.3950, 224.0800, '1H'),
       (20, '2023-01-02 00:00:00', 240.1600, 232.1800, 242.5100, 240.1600, '1WEEK'),
       (7, '2023-01-18 00:00:00', 136.5700, 136.4100, 140.7300, 136.5700, '1DAY'),
       (32, '2023-01-01 00:00:00', 168.3100, 167.4800, 180.9300, 168.3100, '3MONTH'),
       (63, '2023-01-23 15:30:00', 58.6300, 58.4550, 58.6700, 58.6300, '90MIN'),
       (95, '2023-01-23 12:30:00', 145.2300, 141.6300, 116.3032, 145.2300, '90MIN'),
       (5, '2023-01-23 09:30:00', 99.3300, 97.5000, 99.4500, 99.3300, '90MIN'),
       (70, '2022-10-01 00:00:00', 100.3320, 80.5180, 101.3660, 100.3320, '3MONTH'),
       (28, '2022-04-01 00:00:00', 80.4090, 78.8050, 104.2110, 80.4090, '3MONTH'),
       (32, '2023-01-23 09:30:00', 168.9900, 167.9470, 169.2850, 168.9900, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (6, '2023-01-09 00:00:00', 85.0100, 79.7700, 86.5800, 85.0100, '1WEEK'),
       (28, '2023-01-18 00:00:00', 89.4700, 89.3600, 91.2200, 89.4700, '1DAY'),
       (66, '2023-01-09 00:00:00', 49.2700, 48.0500, 49.6400, 49.2700, '1WEEK'),
       (74, '2023-01-23 09:30:00', 97.4500, 95.8600, 97.7800, 97.4500, '1H'),
       (93, '2022-12-26 00:00:00', 103.1900, 101.7800, 104.3500, 103.1900, '1WEEK'),
       (75, '2023-01-20 00:00:00', 113.3500, 110.6100, 113.4400, 113.3500, '1DAY'),
       (5, '2023-01-18 00:00:00', 91.1200, 90.6400, 92.8000, 91.1200, '1DAY'),
       (48, '2023-01-23 09:30:00', 142.1100, 140.2000, 142.2500, 142.1100, '90MIN'),
       (43, '2022-12-26 00:00:00', 31.1600, 30.9300, 31.5000, 31.1600, '1WEEK'),
       (39, '2023-01-23 00:00:00', 224.1800, 223.1500, 226.2000, 224.1800, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (16, '2022-12-19 00:00:00', 161.5640, 160.8010, 162.2770, 161.5640, '1WEEK'),
       (5, '2022-10-01 00:00:00', 88.2300, 83.3400, 104.8200, 88.2300, '3MONTH'),
       (95, '2023-01-23 00:00:00', 143.7500, 134.2700, 29.0760, 143.7500, '1DAY'),
       (67, '2023-01-23 09:30:00', 47.2550, 46.8100, 9.4550, 47.2550, '1H'),
       (16, '2023-01-02 00:00:00', 164.9820, 159.2960, 166.5270, 164.9820, '1WEEK'),
       (91, '2023-01-19 00:00:00', 630.2500, 627.0200, 655.6800, 630.2500, '1DAY'),
       (20, '2023-01-23 11:00:00', 239.4250, 238.7150, 240.6700, 239.4250, '90MIN'),
       (93, '2023-01-23 15:30:00', 116.7100, 116.5000, 58.3900, 116.7100, '1H'),
       (74, '2023-01-23 00:00:00', 97.5200, 95.8600, 97.7800, 97.5200, '1DAY'),
       (22, '2023-01-23 15:30:00', 191.9300, 190.4700, 192.2900, 191.9300, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (45, '2023-01-23 11:00:00', 64.2200, 64.0580, 64.5220, 64.2200, '90MIN'),
       (32, '2023-01-23 14:00:00', 168.2310, 168.0600, 169.0960, 168.2310, '90MIN'),
       (22, '2023-01-23 10:30:00', 188.3800, 185.8500, 189.9600, 188.3800, '1H'),
       (32, '2023-01-23 15:30:00', 168.3100, 167.9700, 168.5600, 168.3100, '90MIN'),
       (75, '2023-01-23 15:30:00', 112.7600, 112.6400, 113.0000, 112.7600, '1H'),
       (22, '2022-07-01 00:00:00', 121.3230, 119.3940, 192.6330, 121.3230, '3MONTH'),
       (19, '2023-01-23 00:00:00', 154.0000, 151.5500, 155.0600, 154.0000, '1WEEK'),
       (74, '2022-10-01 00:00:00', 84.0000, 81.6900, 123.0000, 84.0000, '3MONTH'),
       (6, '2023-01-19 00:00:00', 83.6800, 83.4200, 85.1300, 83.6800, '1DAY'),
       (16, '2023-01-23 00:00:00', 148.5500, 148.0500, 150.4300, 148.5500, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (91, '2022-07-01 00:00:00', 413.1110, 410.4450, 589.5540, 413.1110, '3MONTH'),
       (7, '2023-01-16 00:00:00', 135.0800, 133.5500, 141.5400, 135.0800, '1WEEK'),
       (19, '2023-01-16 00:00:00', 151.6000, 144.2000, 156.1000, 151.6000, '1WEEK'),
       (19, '2023-01-23 09:30:00', 154.0900, 151.5500, 154.2400, 154.0900, '90MIN'),
       (39, '2023-01-23 09:30:00', 224.7900, 223.1540, 225.1400, 224.7900, '90MIN'),
       (95, '2023-01-19 00:00:00', 127.1700, 124.3100, 116.9910, 127.1700, '1DAY'),
       (32, '2023-01-23 12:30:00', 169.0800, 168.3000, 169.1850, 169.0800, '90MIN'),
       (28, '2023-01-23 12:30:00', 95.0100, 94.8400, 95.5000, 95.0100, '90MIN'),
       (95, '2022-10-01 00:00:00', 123.1800, 108.2400, 180.2500, 123.1800, '3MONTH'),
       (30, '2023-01-20 00:00:00', 486.7200, 479.0000, 486.9900, 486.7200, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2023-01-23 15:30:00', 279.3400, 278.5700, 279.5500, 279.3400, '1H'),
       (19, '2023-01-23 14:00:00', 153.9100, 153.4400, 154.7600, 153.9100, '90MIN'),
       (95, '2022-12-19 00:00:00', 123.1500, 121.0200, 128.6200, 123.1500, '1WEEK'),
       (95, '2023-01-23 10:30:00', 140.5700, 138.5400, 14.1550, 140.5700, '1H'),
       (6, '2023-01-23 09:30:00', 86.0400, 84.3200, 86.1100, 86.0400, '90MIN'),
       (74, '2023-01-23 14:00:00', 97.0910, 96.7100, 97.4700, 97.0910, '90MIN'),
       (75, '2023-01-23 14:00:00', 112.8650, 112.7300, 113.9950, 112.8650, '90MIN'),
       (67, '2023-01-02 00:00:00', 47.9370, 46.3890, 9.6410, 47.9370, '1WEEK'),
       (16, '2023-01-23 11:30:00', 149.1250, 148.5750, 149.3500, 149.1250, '1H'),
       (91, '2022-04-01 00:00:00', 469.8420, 455.9900, 672.8920, 469.8420, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2023-01-23 15:30:00', 279.3400, 278.5700, 279.5500, 279.3400, '90MIN'),
       (7, '2022-10-01 00:00:00', 131.9360, 99.6460, 136.4230, 131.9360, '3MONTH'),
       (6, '2022-12-19 00:00:00', 79.9300, 77.7600, 79.9400, 79.9300, '1WEEK'),
       (70, '2023-01-18 00:00:00', 95.7400, 95.6700, 98.6600, 95.7400, '1DAY'),
       (75, '2022-12-19 00:00:00', 108.6800, 106.9000, 108.7400, 108.6800, '1WEEK'),
       (48, '2023-01-16 00:00:00', 140.5400, 138.1700, 145.7100, 140.5400, '1WEEK'),
       (15, '2023-01-23 12:30:00', 181.6000, 181.4400, 181.9700, 181.6000, '1H'),
       (19, '2023-01-23 15:30:00', 154.0000, 153.6200, 154.2700, 154.0000, '1H'),
       (32, '2023-01-17 00:00:00', 172.3600, 172.0900, 174.5800, 172.3600, '1DAY'),
       (45, '2023-01-23 00:00:00', 64.1200, 63.9100, 64.5300, 64.1200, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (39, '2023-01-01 00:00:00', 224.1800, 206.1600, 226.2000, 224.1800, '3MONTH'),
       (20, '2022-04-01 00:00:00', 207.1440, 193.8990, 237.3120, 207.1440, '3MONTH'),
       (63, '2023-01-23 14:30:00', 58.4900, 58.3810, 58.6550, 58.4900, '1H'),
       (51, '2023-01-23 15:30:00', 277.0000, 276.0100, 277.4300, 277.0000, '90MIN'),
       (75, '2023-01-01 00:00:00', 112.7600, 104.7600, 114.5900, 112.7600, '3MONTH'),
       (28, '2023-01-23 09:30:00', 94.5700, 91.7200, 94.7900, 94.5700, '90MIN'),
       (74, '2022-04-01 00:00:00', 106.2100, 101.2600, 168.3950, 106.2100, '3MONTH'),
       (5, '2023-01-23 14:00:00', 99.2190, 98.6000, 99.5700, 99.2190, '90MIN'),
       (5, '2022-12-26 00:00:00', 88.2300, 85.9400, 88.9400, 88.2300, '1WEEK'),
       (51, '2022-01-01 00:00:00', 292.2190, 253.7050, 299.8200, 292.2190, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (74, '2023-01-23 12:30:00', 97.3300, 96.8100, 97.7500, 97.3300, '90MIN'),
       (16, '2023-01-20 00:00:00', 149.5900, 147.1500, 149.7100, 149.5900, '1DAY'),
       (86, '2023-01-17 00:00:00', 284.8800, 282.3400, 287.7500, 284.8800, '1DAY'),
       (74, '2023-01-09 00:00:00', 98.1200, 87.0800, 98.3700, 98.1200, '1WEEK'),
       (95, '2023-01-23 11:30:00', 142.0400, 140.2350, 85.4520, 142.0400, '1H'),
       (74, '2023-01-23 15:30:00', 97.5200, 96.9850, 97.6000, 97.5200, '90MIN'),
       (93, '2023-01-23 00:00:00', 116.7100, 115.6000, 23.4040, 116.7100, '1DAY'),
       (93, '2023-01-20 00:00:00', 117.1200, 115.2100, 46.8600, 117.1200, '1DAY'),
       (70, '2023-01-16 00:00:00', 94.3600, 93.3400, 99.6500, 94.3600, '1WEEK'),
       (93, '2023-01-23 10:30:00', 116.7500, 116.2450, 11.7020, 116.7500, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (66, '2023-01-23 11:30:00', 48.7600, 48.6500, 48.8400, 48.7600, '1H'),
       (32, '2023-01-23 15:30:00', 168.3100, 167.9700, 168.5600, 168.3100, '1H'),
       (86, '2023-01-02 00:00:00', 268.1130, 260.3050, 273.8600, 268.1130, '1WEEK'),
       (30, '2023-01-17 00:00:00', 485.0800, 483.7800, 492.9400, 485.0800, '1DAY'),
       (67, '2022-04-01 00:00:00', 41.2560, 39.6890, 48.9303, 41.2560, '3MONTH'),
       (66, '2023-01-01 00:00:00', 48.6300, 47.3800, 49.9300, 48.6300, '3MONTH'),
       (28, '2023-01-19 00:00:00', 88.4000, 88.2600, 90.0100, 88.4000, '1DAY'),
       (19, '2023-01-18 00:00:00', 150.4200, 150.3200, 154.7600, 150.4200, '1DAY'),
       (91, '2023-01-23 14:00:00', 673.2700, 666.8200, 674.4700, 673.2700, '90MIN'),
       (93, '2022-01-01 00:00:00', 107.7940, 101.1690, 37.3512, 107.7940, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (48, '2023-01-23 14:30:00', 142.4700, 141.9400, 142.5600, 142.4700, '1H'),
       (66, '2022-12-19 00:00:00', 48.5200, 47.9000, 48.5700, 48.5200, '1WEEK'),
       (70, '2023-01-17 00:00:00', 98.4300, 98.3600, 99.6500, 98.4300, '1DAY'),
       (22, '2023-01-23 09:30:00', 185.9180, 178.1750, 187.0000, 185.9180, '1H'),
       (19, '2023-01-02 00:00:00', 149.6510, 144.9770, 151.4350, 149.6510, '1WEEK'),
       (28, '2023-01-16 00:00:00', 91.0300, 86.1600, 91.2200, 91.0300, '1WEEK'),
       (39, '2023-01-20 00:00:00', 224.3100, 220.4100, 224.4300, 224.3100, '1DAY'),
       (70, '2023-01-23 14:30:00', 96.0600, 95.9300, 96.3300, 96.0600, '1H'),
       (74, '2022-12-19 00:00:00', 85.2500, 82.9300, 85.7800, 85.2500, '1WEEK'),
       (51, '2023-01-02 00:00:00', 252.4900, 242.4800, 268.2900, 252.4900, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (7, '2023-01-23 00:00:00', 137.2700, 134.8200, 137.9600, 137.2700, '1WEEK'),
       (70, '2022-01-01 00:00:00', 96.8020, 87.2270, 101.9520, 96.8020, '3MONTH'),
       (16, '2023-01-23 14:00:00', 148.4260, 148.4000, 149.6250, 148.4260, '90MIN'),
       (66, '2023-01-23 11:00:00', 48.7600, 48.6500, 48.9500, 48.7600, '90MIN'),
       (70, '2023-01-01 00:00:00', 96.2500, 93.3400, 103.9500, 96.2500, '3MONTH'),
       (93, '2022-10-01 00:00:00', 103.1900, 80.5000, 78.9740, 103.1900, '3MONTH'),
       (48, '2022-01-01 00:00:00', 146.6150, 129.9670, 148.2100, 146.6150, '3MONTH'),
       (70, '2023-01-23 15:30:00', 96.2500, 96.0150, 96.4700, 96.2500, '1H'),
       (16, '2023-01-23 13:30:00', 149.0400, 148.9600, 149.6800, 149.0400, '1H'),
       (20, '2023-01-23 12:30:00', 240.8200, 239.2250, 241.0400, 240.8200, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (91, '2023-01-23 00:00:00', 676.0300, 656.8500, 676.2800, 676.0300, '1DAY'),
       (86, '2023-01-16 00:00:00', 280.4700, 272.5900, 287.7500, 280.4700, '1WEEK'),
       (28, '2022-10-01 00:00:00', 74.0700, 59.0950, 83.1590, 74.0700, '3MONTH'),
       (39, '2023-01-23 12:30:00', 224.7600, 224.6500, 225.3950, 224.7600, '90MIN'),
       (30, '2023-01-16 00:00:00', 486.7200, 474.7500, 492.9400, 486.7200, '1WEEK'),
       (75, '2022-07-01 00:00:00', 85.7790, 79.2750, 99.7790, 85.7790, '3MONTH'),
       (95, '2023-01-09 00:00:00', 122.4000, 114.9200, 113.3550, 122.4000, '1WEEK'),
       (86, '2023-01-23 00:00:00', 279.3400, 277.4300, 282.8700, 279.3400, '1DAY'),
       (30, '2022-01-01 00:00:00', 503.5220, 440.1040, 515.2910, 503.5220, '3MONTH'),
       (67, '2023-01-23 12:30:00', 47.7950, 47.5600, 38.2960, 47.7950, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (70, '2023-01-02 00:00:00', 102.4600, 99.6600, 103.9500, 102.4600, '1WEEK'),
       (22, '2023-01-23 00:00:00', 191.9300, 178.1800, 192.4500, 191.9300, '1DAY'),
       (16, '2022-01-01 00:00:00', 154.5620, 126.8550, 156.9930, 154.5620, '3MONTH'),
       (15, '2023-01-19 00:00:00', 179.0000, 176.1600, 179.9500, 179.0000, '1DAY'),
       (51, '2023-01-23 14:30:00', 276.7000, 276.1500, 278.0300, 276.7000, '1H'),
       (67, '2023-01-23 11:00:00', 47.5950, 47.2900, 9.5240, 47.5950, '90MIN'),
       (43, '2023-01-23 14:30:00', 36.7150, 36.6250, 14.6880, 36.7150, '1H'),
       (32, '2023-01-09 00:00:00', 173.4300, 172.1100, 179.7600, 173.4300, '1WEEK'),
       (5, '2023-01-20 00:00:00', 98.0200, 95.0200, 98.3000, 98.0200, '1DAY'),
       (91, '2023-01-09 00:00:00', 659.6900, 612.8000, 660.7400, 659.6900, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (32, '2023-01-23 14:30:00', 168.2310, 168.0600, 168.6700, 168.2310, '1H'),
       (22, '2023-01-23 11:00:00', 189.5400, 187.9600, 190.0000, 189.5400, '90MIN'),
       (67, '2023-01-20 00:00:00', 46.7800, 45.6700, 18.7320, 46.7800, '1DAY'),
       (45, '2023-01-23 13:30:00', 64.3150, 64.2300, 64.4000, 64.3150, '1H'),
       (63, '2022-01-01 00:00:00', 53.4670, 46.9850, 54.6360, 53.4670, '3MONTH'),
       (30, '2023-01-23 14:30:00', 486.0500, 485.2400, 486.9500, 486.0500, '1H'),
       (91, '2023-01-23 15:30:00', 676.0300, 672.2600, 676.2800, 676.0300, '1H'),
       (39, '2023-01-23 14:30:00', 223.8000, 223.3800, 224.3500, 223.8000, '1H'),
       (51, '2023-01-23 10:30:00', 277.5700, 276.5200, 278.1600, 277.5700, '1H'),
       (51, '2023-01-23 13:30:00', 277.7900, 277.6500, 279.3200, 277.7900, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (45, '2023-01-23 14:30:00', 63.9700, 63.9100, 64.3600, 63.9700, '1H'),
       (45, '2023-01-23 15:30:00', 64.1200, 63.9250, 64.2000, 64.1200, '1H'),
       (15, '2023-01-20 00:00:00', 180.9000, 177.5200, 181.3600, 180.9000, '1DAY'),
       (16, '2023-01-23 15:30:00', 148.5500, 148.0540, 148.6100, 148.5500, '90MIN'),
       (74, '2023-01-23 12:30:00', 97.6710, 96.8100, 97.7500, 97.6710, '1H'),
       (93, '2022-07-01 00:00:00', 81.2500, 78.2200, 29.0460, 81.2500, '3MONTH'),
       (63, '2023-01-23 00:00:00', 58.6300, 58.3500, 58.8300, 58.6300, '1DAY'),
       (63, '2023-01-23 15:30:00', 58.6300, 58.4550, 58.6700, 58.6300, '1H'),
       (63, '2023-01-23 12:30:00', 58.6830, 58.6200, 58.7400, 58.6830, '90MIN'),
       (63, '2022-07-01 00:00:00', 48.8530, 44.0820, 55.1560, 48.8530, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (51, '2023-01-16 00:00:00', 274.4000, 267.2000, 278.7900, 274.4000, '1WEEK'),
       (43, '2023-01-23 00:00:00', 36.8200, 36.5900, 22.0920, 36.8200, '1WEEK'),
       (20, '2023-01-23 09:30:00', 239.1100, 235.4400, 239.8150, 239.1100, '90MIN'),
       (30, '2023-01-23 09:30:00', 487.9250, 481.3900, 488.4400, 487.9250, '90MIN'),
       (32, '2022-12-19 00:00:00', 177.4800, 175.8100, 177.5200, 177.4800, '1WEEK'),
       (43, '2023-01-18 00:00:00', 36.1800, 36.1800, 14.6600, 36.1800, '1DAY'),
       (93, '2023-01-17 00:00:00', 116.2100, 115.7400, 70.4640, 116.2100, '1DAY'),
       (22, '2022-01-01 00:00:00', 272.6050, 206.3070, 289.1900, 272.6050, '3MONTH'),
       (45, '2023-01-19 00:00:00', 63.5700, 62.8200, 63.7800, 63.5700, '1DAY'),
       (45, '2023-01-02 00:00:00', 61.8300, 60.6600, 63.1100, 61.8300, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (86, '2022-07-01 00:00:00', 254.2130, 251.2190, 319.0060, 254.2130, '3MONTH'),
       (7, '2023-01-23 15:30:00', 137.2700, 136.9000, 137.5800, 137.2700, '1H'),
       (95, '2022-07-01 00:00:00', 265.2500, 216.1670, 94.4001, 265.2500, '3MONTH'),
       (16, '2022-12-26 00:00:00', 160.0880, 158.3150, 163.1390, 160.0880, '1WEEK'),
       (30, '2022-10-01 00:00:00', 528.5510, 486.2410, 556.3850, 528.5510, '3MONTH'),
       (30, '2023-01-23 11:30:00', 485.5600, 484.8900, 488.5700, 485.5600, '1H'),
       (67, '2023-01-23 11:30:00', 47.5950, 47.4500, 28.5642, 47.5950, '1H'),
       (15, '2023-01-23 14:00:00', 180.4600, 180.1200, 181.6650, 180.4600, '90MIN'),
       (16, '2023-01-23 12:30:00', 149.0300, 148.8500, 149.2900, 149.0300, '1H'),
       (93, '2023-01-23 12:30:00', 116.7400, 116.4550, 93.4400, 116.7400, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (43, '2023-01-16 00:00:00', 36.8400, 35.8800, 11.0550, 36.8400, '1WEEK'),
       (86, '2023-01-23 10:30:00', 281.5700, 280.2700, 282.8700, 281.5700, '1H'),
       (51, '2022-04-01 00:00:00', 252.7810, 233.0280, 302.2360, 252.7810, '3MONTH'),
       (63, '2023-01-23 13:30:00', 58.6070, 58.5950, 58.7400, 58.6070, '1H'),
       (7, '2022-12-19 00:00:00', 130.3170, 128.6890, 130.4760, 130.3170, '1WEEK'),
       (91, '2022-01-01 00:00:00', 659.4550, 551.6800, 705.5530, 659.4550, '3MONTH'),
       (75, '2022-01-01 00:00:00', 79.4390, 71.2050, 88.0180, 79.4390, '3MONTH'),
       (63, '2023-01-23 09:30:00', 58.8000, 58.3500, 58.8300, 58.8000, '90MIN'),
       (95, '2023-01-23 12:30:00', 143.7400, 141.6300, 115.1040, 143.7400, '1H'),
       (48, '2022-12-19 00:00:00', 143.7700, 142.2800, 143.8000, 143.7700, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (74, '2023-01-01 00:00:00', 97.5200, 81.4300, 99.3200, 97.5200, '3MONTH'),
       (51, '2023-01-23 00:00:00', 277.0000, 272.3800, 279.3200, 277.0000, '1DAY'),
       (22, '2022-12-26 00:00:00', 146.1400, 138.8400, 151.0000, 146.1400, '1WEEK'),
       (45, '2023-01-23 00:00:00', 64.1200, 63.9100, 64.5300, 64.1200, '1WEEK'),
       (43, '2023-01-23 10:30:00', 36.7150, 36.6500, 3.6780, 36.7150, '1H'),
       (91, '2023-01-23 12:30:00', 672.2000, 668.0720, 672.5500, 672.2000, '1H'),
       (28, '2023-01-17 00:00:00', 88.9900, 86.1600, 89.7300, 88.9900, '1DAY'),
       (63, '2022-10-01 00:00:00', 56.4430, 49.2820, 58.4450, 56.4430, '3MONTH'),
       (30, '2022-04-01 00:00:00', 508.6360, 445.3280, 547.9100, 508.6360, '3MONTH'),
       (70, '2022-04-01 00:00:00', 94.4320, 86.0810, 104.1690, 94.4320, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (7, '2023-01-01 00:00:00', 137.2700, 133.5500, 143.4900, 137.2700, '3MONTH'),
       (32, '2022-12-26 00:00:00', 176.6500, 175.4000, 178.4500, 176.6500, '1WEEK'),
       (93, '2023-01-19 00:00:00', 115.6700, 114.6500, 104.5170, 115.6700, '1DAY'),
       (28, '2023-01-23 14:30:00', 94.8410, 93.9800, 94.9150, 94.8410, '1H'),
       (66, '2023-01-19 00:00:00', 49.4400, 48.9200, 49.8700, 49.4400, '1DAY'),
       (93, '2022-12-19 00:00:00', 103.5000, 102.6600, 103.7100, 103.5000, '1WEEK'),
       (66, '2022-12-26 00:00:00', 48.4300, 47.7100, 48.6200, 48.4300, '1WEEK'),
       (91, '2023-01-23 09:30:00', 665.5100, 656.8500, 668.2500, 665.5100, '90MIN'),
       (48, '2023-01-23 00:00:00', 142.6400, 140.2000, 143.0100, 142.6400, '1DAY'),
       (22, '2023-01-17 00:00:00', 177.0200, 168.9900, 177.2800, 177.0200, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (66, '2023-01-17 00:00:00', 49.8300, 49.5600, 49.9300, 49.8300, '1DAY'),
       (28, '2023-01-23 13:30:00', 94.6300, 94.6060, 95.3300, 94.6300, '1H'),
       (19, '2022-12-19 00:00:00', 146.5120, 144.0210, 146.6320, 146.5120, '1WEEK'),
       (22, '2023-01-23 12:30:00', 190.7600, 189.1400, 190.8500, 190.7600, '1H'),
       (39, '2023-01-17 00:00:00', 223.0000, 222.3700, 224.4000, 223.0000, '1DAY'),
       (28, '2023-01-23 14:00:00', 94.8410, 93.9800, 95.0700, 94.8410, '90MIN'),
       (91, '2023-01-16 00:00:00', 648.8500, 627.0200, 676.1100, 648.8500, '1WEEK'),
       (20, '2022-10-01 00:00:00', 237.7080, 217.7450, 273.5940, 237.7080, '3MONTH'),
       (5, '2023-01-17 00:00:00', 91.2900, 90.0500, 92.2500, 91.2900, '1DAY'),
       (20, '2023-01-20 00:00:00', 237.1700, 232.2600, 237.7100, 237.1700, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (20, '2023-01-23 15:30:00', 240.9600, 239.7050, 241.4900, 240.9600, '90MIN'),
       (45, '2023-01-18 00:00:00', 63.2500, 63.2300, 65.0500, 63.2500, '1DAY'),
       (74, '2022-12-26 00:00:00', 84.0000, 81.6900, 85.3500, 84.0000, '1WEEK'),
       (32, '2023-01-23 11:30:00', 168.4400, 168.1300, 169.2500, 168.4400, '1H'),
       (20, '2022-12-19 00:00:00', 240.9400, 236.5900, 241.6500, 240.9400, '1WEEK'),
       (15, '2023-01-23 12:30:00', 181.5400, 181.1000, 181.9700, 181.5400, '90MIN'),
       (6, '2023-01-23 12:30:00', 85.6000, 85.4300, 85.9200, 85.6000, '1H'),
       (48, '2023-01-23 12:30:00', 142.8700, 142.4300, 143.0100, 142.8700, '90MIN'),
       (74, '2023-01-23 09:30:00', 97.0700, 95.8600, 97.7800, 97.0700, '90MIN'),
       (19, '2022-12-26 00:00:00', 147.2400, 143.4430, 147.4190, 147.2400, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (91, '2023-01-23 11:30:00', 668.1800, 667.7500, 672.6900, 668.1800, '1H'),
       (95, '2023-01-17 00:00:00', 131.4900, 125.0200, 79.0200, 131.4900, '1DAY'),
       (63, '2023-01-23 09:30:00', 58.5200, 58.3500, 58.6200, 58.5200, '1H'),
       (22, '2023-01-20 00:00:00', 178.3900, 168.2500, 178.5600, 178.3900, '1DAY'),
       (6, '2023-01-23 15:30:00', 85.5800, 85.5500, 85.7100, 85.5800, '90MIN'),
       (16, '2023-01-23 15:30:00', 148.5500, 148.0540, 148.6100, 148.5500, '1H'),
       (75, '2023-01-23 11:00:00', 113.9300, 113.7500, 114.5900, 113.9300, '90MIN'),
       (51, '2023-01-19 00:00:00', 270.7800, 267.2000, 272.4900, 270.7800, '1DAY'),
       (22, '2023-01-23 12:30:00', 191.8450, 189.1400, 192.4500, 191.8450, '90MIN'),
       (32, '2023-01-16 00:00:00', 168.7400, 167.4800, 174.5800, 168.7400, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (48, '2023-01-23 00:00:00', 142.6400, 140.2000, 143.0100, 142.6400, '1WEEK'),
       (91, '2023-01-01 00:00:00', 676.0300, 545.7700, 676.2800, 676.0300, '3MONTH'),
       (19, '2023-01-23 13:30:00', 154.1200, 153.9800, 155.0600, 154.1200, '1H'),
       (39, '2023-01-23 10:30:00', 225.1000, 224.2100, 225.7700, 225.1000, '1H'),
       (39, '2023-01-23 15:30:00', 224.1800, 223.5600, 224.3000, 224.1800, '90MIN'),
       (86, '2023-01-23 11:00:00', 280.5400, 280.4100, 282.8700, 280.5400, '90MIN'),
       (39, '2022-12-26 00:00:00', 207.7600, 204.6000, 208.5300, 207.7600, '1WEEK'),
       (28, '2023-01-23 10:30:00', 94.9800, 93.9100, 95.3400, 94.9800, '1H'),
       (66, '2023-01-23 00:00:00', 48.6300, 48.5600, 48.9500, 48.6300, '1WEEK'),
       (19, '2023-01-20 00:00:00', 151.6000, 147.7200, 151.7800, 151.6000, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (75, '2023-01-16 00:00:00', 113.3500, 109.5800, 114.1200, 113.3500, '1WEEK'),
       (19, '2023-01-23 14:30:00', 153.9100, 153.4400, 154.3000, 153.9100, '1H'),
       (16, '2022-10-01 00:00:00', 158.4760, 132.6860, 164.4970, 158.4760, '3MONTH'),
       (74, '2023-01-23 11:00:00', 97.2500, 97.0300, 97.7200, 97.2500, '90MIN'),
       (7, '2022-01-01 00:00:00', 131.1800, 122.4710, 153.0340, 131.1800, '3MONTH'),
       (22, '2023-01-23 00:00:00', 191.9300, 178.1800, 192.4500, 191.9300, '1WEEK'),
       (63, '2023-01-23 00:00:00', 58.6300, 58.3500, 58.8300, 58.6300, '1WEEK'),
       (51, '2022-12-19 00:00:00', 259.6090, 257.4720, 261.2880, 259.6090, '1WEEK'),
       (6, '2022-04-01 00:00:00', 65.9310, 64.2990, 81.7470, 65.9310, '3MONTH'),
       (75, '2023-01-23 12:30:00', 113.9400, 113.7700, 114.2220, 113.9400, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (30, '2022-07-01 00:00:00', 501.9130, 489.2020, 549.7050, 501.9130, '3MONTH'),
       (67, '2022-01-01 00:00:00', 53.6340, 50.6910, 16.6473, 53.6340, '3MONTH'),
       (43, '2022-01-01 00:00:00', 32.8100, 29.1950, 11.1057, 32.8100, '3MONTH'),
       (28, '2023-01-23 15:30:00', 95.6600, 94.7600, 95.7100, 95.6600, '1H'),
       (15, '2023-01-23 11:30:00', 181.8500, 181.6900, 182.2600, 181.8500, '1H'),
       (5, '2022-07-01 00:00:00', 95.6500, 95.5600, 122.4300, 95.6500, '3MONTH'),
       (6, '2023-01-23 10:30:00', 86.1100, 85.6100, 86.4100, 86.1100, '1H'),
       (74, '2023-01-23 10:30:00', 97.3450, 96.9600, 97.7500, 97.3450, '1H'),
       (95, '2023-01-01 00:00:00', 143.7500, 101.8100, 43.6140, 143.7500, '3MONTH'),
       (70, '2023-01-23 13:30:00', 96.2650, 96.2400, 96.9300, 96.2650, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (5, '2022-04-01 00:00:00', 108.9630, 101.8840, 143.7120, 108.9630, '3MONTH'),
       (63, '2023-01-02 00:00:00', 57.2500, 54.9700, 57.7950, 57.2500, '1WEEK'),
       (15, '2023-01-23 10:30:00', 181.9800, 180.5300, 182.5460, 181.9800, '1H'),
       (86, '2023-01-20 00:00:00', 280.4700, 272.8100, 281.0700, 280.4700, '1DAY'),
       (93, '2023-01-23 00:00:00', 116.7100, 115.6000, 70.2120, 116.7100, '1WEEK'),
       (22, '2023-01-23 13:30:00', 191.2200, 190.7600, 192.4500, 191.2200, '1H'),
       (6, '2023-01-18 00:00:00', 84.7800, 84.6900, 87.8800, 84.7800, '1DAY'),
       (91, '2023-01-23 14:30:00', 673.2700, 666.8200, 674.4700, 673.2700, '1H'),
       (75, '2023-01-09 00:00:00', 113.1500, 107.8100, 113.7400, 113.1500, '1WEEK'),
       (86, '2023-01-23 09:30:00', 280.9000, 277.4330, 281.3900, 280.9000, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (39, '2023-01-02 00:00:00', 217.7500, 206.1600, 218.1400, 217.7500, '1WEEK'),
       (15, '2023-01-02 00:00:00', 176.5600, 170.8500, 179.3900, 176.5600, '1WEEK'),
       (70, '2022-07-01 00:00:00', 80.9120, 79.3400, 96.1920, 80.9120, '3MONTH'),
       (63, '2023-01-20 00:00:00', 58.9000, 58.1550, 58.9900, 58.9000, '1DAY'),
       (28, '2023-01-23 00:00:00', 95.6600, 91.7200, 95.7300, 95.6600, '1WEEK'),
       (39, '2023-01-23 15:30:00', 224.1800, 223.5600, 224.3000, 224.1800, '1H'),
       (43, '2022-12-19 00:00:00', 30.9400, 30.6400, 30.9400, 30.9400, '1WEEK'),
       (48, '2022-04-01 00:00:00', 120.1610, 115.9020, 158.8940, 120.1610, '3MONTH'),
       (67, '2023-01-23 13:30:00', 47.6200, 47.6000, 43.0830, 47.6200, '1H'),
       (48, '2022-12-26 00:00:00', 141.7900, 140.8100, 144.4500, 141.7900, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (70, '2023-01-23 10:30:00', 96.0800, 95.9000, 96.3600, 96.0800, '1H'),
       (20, '2023-01-23 15:30:00', 240.9600, 240.9600, 240.9600, 240.9600, '1H'),
       (19, '2023-01-19 00:00:00', 146.8500, 144.2000, 148.0000, 146.8500, '1DAY'),
       (45, '2022-07-01 00:00:00', 44.5760, 42.7460, 52.6820, 44.5760, '3MONTH'),
       (5, '2023-01-23 12:30:00', 99.5300, 99.5300, 100.0400, 99.5300, '90MIN'),
       (93, '2023-01-23 14:30:00', 116.6900, 116.1650, 46.7720, 116.6900, '1H'),
       (7, '2023-01-23 09:30:00', 136.9000, 134.8200, 137.1100, 136.9000, '90MIN'),
       (15, '2022-04-01 00:00:00', 141.2130, 136.6970, 177.9060, 141.2130, '3MONTH'),
       (63, '2022-12-19 00:00:00', 57.7800, 56.9550, 57.7800, 57.7800, '1WEEK'),
       (43, '2022-04-01 00:00:00', 32.2250, 28.7730, 31.2840, 32.2250, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (15, '2023-01-23 09:30:00', 180.8700, 180.4700, 181.9900, 180.8700, '1H'),
       (22, '2023-01-02 00:00:00', 148.5900, 140.3400, 150.1000, 148.5900, '1WEEK'),
       (95, '2023-01-23 00:00:00', 143.7500, 134.2700, 87.2280, 143.7500, '1WEEK'),
       (28, '2022-12-26 00:00:00', 74.4900, 72.8400, 76.0400, 74.4900, '1WEEK'),
       (75, '2023-01-23 09:30:00', 114.4400, 113.3000, 114.5300, 114.4400, '90MIN'),
       (51, '2023-01-23 09:30:00', 276.7900, 272.3800, 277.7500, 276.7900, '90MIN'),
       (28, '2022-01-01 00:00:00', 102.0570, 95.5570, 125.9410, 102.0570, '3MONTH'),
       (91, '2023-01-23 11:00:00', 668.1800, 665.6800, 672.6900, 668.1800, '90MIN'),
       (66, '2023-01-23 15:30:00', 48.6300, 48.5900, 48.7300, 48.6300, '90MIN'),
       (15, '2023-01-23 00:00:00', 180.6600, 180.0300, 182.5500, 180.6600, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (70, '2022-12-19 00:00:00', 99.3000, 98.0300, 99.4100, 99.3000, '1WEEK'),
       (32, '2022-01-01 00:00:00', 172.6930, 151.7330, 175.5960, 172.6930, '3MONTH'),
       (28, '2023-01-20 00:00:00', 91.0300, 89.2000, 91.0300, 91.0300, '1DAY'),
       (67, '2022-07-01 00:00:00', 38.9670, 38.9280, 14.6127, 38.9670, '3MONTH'),
       (30, '2023-01-23 09:30:00', 487.3500, 481.3900, 488.1200, 487.3500, '1H'),
       (6, '2023-01-23 14:00:00', 85.7100, 85.5900, 85.9000, 85.7100, '90MIN'),
       (39, '2023-01-23 09:30:00', 224.1800, 223.1540, 224.9800, 224.1800, '1H'),
       (30, '2023-01-09 00:00:00', 489.5700, 482.6800, 509.5000, 489.5700, '1WEEK'),
       (22, '2023-01-23 14:00:00', 191.1200, 189.0200, 192.2500, 191.1200, '90MIN'),
       (43, '2023-01-23 13:30:00', 36.7110, 36.6850, 33.0660, 36.7110, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (67, '2022-12-26 00:00:00', 47.2620, 46.5780, 47.3910, 47.2620, '1WEEK'),
       (74, '2023-01-23 13:30:00', 97.2370, 97.0750, 97.7100, 97.2370, '1H'),
       (19, '2022-01-01 00:00:00', 183.9680, 153.1950, 196.3150, 183.9680, '3MONTH'),
       (15, '2022-07-01 00:00:00', 141.2830, 130.3380, 164.0590, 141.2830, '3MONTH'),
       (7, '2023-01-23 14:30:00', 137.0000, 136.6400, 137.4650, 137.0000, '1H'),
       (32, '2022-10-01 00:00:00', 175.5170, 158.1490, 179.8790, 175.5170, '3MONTH'),
       (43, '2023-01-23 11:30:00', 36.6650, 36.6200, 22.0290, 36.6650, '1H'),
       (91, '2022-12-19 00:00:00', 551.3700, 542.5300, 552.0700, 551.3700, '1WEEK'),
       (5, '2023-01-23 10:30:00', 99.6350, 98.9200, 99.9200, 99.6350, '1H'),
       (5, '2023-01-23 12:30:00', 99.9500, 99.6600, 99.9550, 99.9500, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (93, '2023-01-01 00:00:00', 116.7100, 103.2800, 35.6400, 116.7100, '3MONTH'),
       (75, '2022-10-01 00:00:00', 109.4190, 89.0040, 113.7440, 109.4190, '3MONTH'),
       (95, '2023-01-02 00:00:00', 113.0600, 101.8100, 23.7600, 113.0600, '1WEEK'),
       (16, '2023-01-23 09:30:00', 149.5510, 148.6400, 150.4300, 149.5510, '1H'),
       (45, '2023-01-09 00:00:00', 64.6300, 62.0000, 64.9300, 64.6300, '1WEEK'),
       (95, '2023-01-23 15:30:00', 143.7500, 141.8500, 71.9500, 143.7500, '1H'),
       (22, '2023-01-23 11:30:00', 189.5400, 188.3600, 190.0000, 189.5400, '1H'),
       (86, '2023-01-23 11:30:00', 280.5400, 280.4100, 282.0800, 280.5400, '1H'),
       (67, '2023-01-23 15:30:00', 47.5000, 47.4300, 28.5420, 47.5000, '90MIN'),
       (7, '2023-01-23 09:30:00', 136.4000, 134.8200, 136.4900, 136.4000, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (70, '2023-01-20 00:00:00', 94.3600, 93.3400, 94.4400, 94.3600, '1DAY'),
       (75, '2023-01-23 00:00:00', 112.7600, 112.6400, 114.5900, 112.7600, '1WEEK'),
       (6, '2023-01-01 00:00:00', 85.5800, 77.2100, 87.8800, 85.5800, '3MONTH'),
       (91, '2023-01-23 00:00:00', 676.0300, 656.8500, 676.2800, 676.0300, '1WEEK'),
       (51, '2023-01-23 14:00:00', 276.7000, 276.1500, 278.5300, 276.7000, '90MIN'),
       (30, '2023-01-23 11:00:00', 485.5600, 484.8900, 490.1000, 485.5600, '90MIN'),
       (5, '2022-12-19 00:00:00', 89.2300, 87.0700, 89.5500, 89.2300, '1WEEK'),
       (74, '2023-01-23 00:00:00', 97.5200, 95.8600, 97.7800, 97.5200, '1WEEK'),
       (93, '2023-01-23 11:30:00', 116.5400, 116.5020, 70.0920, 116.5400, '1H'),
       (51, '2023-01-23 11:00:00', 278.4700, 276.7500, 278.7600, 278.4700, '90MIN');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (30, '2023-01-18 00:00:00', 476.2400, 474.7500, 489.5000, 476.2400, '1DAY'),
       (22, '2023-01-01 00:00:00', 191.9300, 140.3400, 192.4500, 191.9300, '3MONTH'),
       (51, '2022-10-01 00:00:00', 265.1640, 234.5940, 283.3570, 265.1640, '3MONTH'),
       (75, '2023-01-18 00:00:00', 110.6100, 110.4000, 114.1200, 110.6100, '1DAY'),
       (66, '2023-01-16 00:00:00', 49.0800, 48.9200, 49.9300, 49.0800, '1WEEK'),
       (48, '2023-01-23 14:00:00', 142.4700, 141.9400, 142.8800, 142.4700, '90MIN'),
       (45, '2023-01-23 14:00:00', 63.9700, 63.9100, 64.3900, 63.9700, '90MIN'),
       (48, '2023-01-23 12:30:00', 142.8500, 142.4300, 142.9410, 142.8500, '1H'),
       (30, '2023-01-23 00:00:00', 485.8100, 481.3900, 490.1000, 485.8100, '1DAY'),
       (93, '2023-01-09 00:00:00', 117.0100, 110.7000, 105.4350, 117.0100, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (51, '2023-01-20 00:00:00', 274.4000, 268.9500, 274.4200, 274.4000, '1DAY'),
       (39, '2023-01-23 00:00:00', 224.1800, 223.1500, 226.2000, 224.1800, '1WEEK'),
       (16, '2023-01-17 00:00:00', 152.8300, 152.5800, 155.1000, 152.8300, '1DAY'),
       (66, '2022-10-01 00:00:00', 48.4300, 38.4900, 49.7500, 48.4300, '3MONTH'),
       (19, '2022-07-01 00:00:00', 133.9330, 133.1480, 164.8570, 133.9330, '3MONTH'),
       (74, '2023-01-19 00:00:00', 93.6800, 92.8600, 95.4400, 93.6800, '1DAY'),
       (19, '2023-01-09 00:00:00', 155.7600, 149.2800, 156.2500, 155.7600, '1WEEK'),
       (67, '2023-01-23 15:30:00', 47.5000, 47.4300, 23.7850, 47.5000, '1H'),
       (75, '2023-01-23 00:00:00', 112.7600, 112.6400, 114.5900, 112.7600, '1DAY'),
       (91, '2023-01-02 00:00:00', 595.8500, 545.7700, 598.6800, 595.8500, '1WEEK');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (95, '2023-01-20 00:00:00', 133.4200, 127.3500, 53.4040, 133.4200, '1DAY'),
       (20, '2023-01-23 00:00:00', 240.9600, 235.4400, 241.8600, 240.9600, '1DAY'),
       (6, '2023-01-23 15:30:00', 85.5800, 85.5500, 85.7100, 85.5800, '1H'),
       (48, '2023-01-23 10:30:00', 142.3100, 141.7800, 142.8600, 142.3100, '1H'),
       (32, '2022-07-01 00:00:00', 161.2290, 158.7120, 177.6420, 161.2290, '3MONTH'),
       (30, '2023-01-23 00:00:00', 485.8100, 481.3900, 490.1000, 485.8100, '1WEEK'),
       (22, '2023-01-19 00:00:00', 167.6500, 167.3100, 171.9700, 167.6500, '1DAY'),
       (93, '2023-01-23 13:30:00', 116.4700, 116.4300, 105.1830, 116.4700, '1H'),
       (6, '2023-01-17 00:00:00', 86.1700, 84.4300, 86.3400, 86.1700, '1DAY'),
       (86, '2022-04-01 00:00:00', 273.5050, 264.1660, 340.1440, 273.5050, '3MONTH');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (22, '2023-01-09 00:00:00', 168.9900, 151.4100, 169.2200, 168.9900, '1WEEK'),
       (70, '2023-01-23 09:30:00', 96.0500, 94.5100, 96.3600, 96.0500, '90MIN'),
       (48, '2023-01-23 09:30:00', 141.8400, 140.2000, 141.8600, 141.8400, '1H'),
       (7, '2023-01-23 11:00:00', 137.0700, 136.9100, 137.7950, 137.0700, '90MIN'),
       (19, '2023-01-23 12:30:00', 154.7500, 153.6900, 155.0600, 154.7500, '90MIN'),
       (86, '2023-01-23 12:30:00', 280.8050, 280.1100, 281.2150, 280.8050, '1H'),
       (66, '2022-04-01 00:00:00', 48.1370, 46.5680, 55.9010, 48.1370, '3MONTH'),
       (51, '2023-01-17 00:00:00', 274.9200, 272.5800, 277.2900, 274.9200, '1DAY'),
       (22, '2022-12-19 00:00:00', 152.0600, 148.8300, 153.3900, 152.0600, '1WEEK'),
       (70, '2023-01-23 00:00:00', 96.2500, 94.5100, 96.9300, 96.2500, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (63, '2023-01-23 10:30:00', 58.7350, 58.4650, 58.8300, 58.7350, '1H'),
       (48, '2023-01-19 00:00:00', 138.8300, 138.7900, 141.5300, 138.8300, '1DAY'),
       (16, '2023-01-16 00:00:00', 149.5900, 147.1500, 155.1000, 149.5900, '1WEEK'),
       (63, '2023-01-01 00:00:00', 58.6300, 54.9700, 60.1400, 58.6300, '3MONTH'),
       (6, '2022-01-01 00:00:00', 80.9360, 71.3620, 82.3820, 80.9360, '3MONTH'),
       (20, '2023-01-16 00:00:00', 237.1700, 232.2000, 244.3500, 237.1700, '1WEEK'),
       (91, '2023-01-20 00:00:00', 648.8500, 634.2000, 650.6000, 648.8500, '1DAY'),
       (43, '2023-01-01 00:00:00', 36.8200, 31.5100, 11.0550, 36.8200, '3MONTH'),
       (32, '2023-01-23 00:00:00', 168.3100, 167.9500, 169.6300, 168.3100, '1WEEK'),
       (5, '2023-01-02 00:00:00', 87.3400, 84.8600, 91.0500, 87.3400, '1WEEK');
INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (7, '2023-01-23 12:30:00', 137.3150, 137.0500, 137.3950, 137.3150, '1H'),
       (19, '2023-01-23 11:30:00', 153.7500, 153.3220, 154.1700, 153.7500, '1H'),
       (7, '2023-01-23 14:00:00', 137.0000, 136.6400, 137.8650, 137.0000, '90MIN'),
       (66, '2023-01-23 14:30:00', 48.6600, 48.5600, 48.7450, 48.6600, '1H'),
       (6, '2023-01-23 09:30:00', 85.5850, 84.3200, 85.6300, 85.5850, '1H'),
       (93, '2023-01-18 00:00:00', 117.0800, 116.9200, 47.5200, 117.0800, '1DAY'),
       (45, '2022-10-01 00:00:00', 60.3110, 46.4770, 62.1960, 60.3110, '3MONTH'),
       (28, '2023-01-23 09:30:00', 94.2750, 91.7200, 94.7900, 94.2750, '1H'),
       (6, '2023-01-23 00:00:00', 85.5800, 84.3200, 86.4100, 85.5800, '1WEEK'),
       (74, '2023-01-17 00:00:00', 96.0500, 95.7300, 98.8900, 96.0500, '1DAY');
INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (75, '2022-04-01 00:00:00', 83.2850, 77.1100, 102.6680, 83.2850, '3MONTH'),
       (22, '2023-01-16 00:00:00', 178.3900, 167.3100, 178.7300, 178.3900, '1WEEK'),
       (15, '2022-10-01 00:00:00', 178.1120, 147.1620, 188.2240, 178.1120, '3MONTH'),
       (28, '2022-07-01 00:00:00', 67.7900, 66.9790, 90.9080, 67.7900, '3MONTH'),
       (67, '2022-12-19 00:00:00', 47.1040, 46.6370, 47.1140, 47.1040, '1WEEK'),
       (39, '2022-04-01 00:00:00', 195.7300, 184.8140, 227.8890, 195.7300, '3MONTH'),
       (63, '2023-01-23 14:00:00', 58.4900, 58.3810, 58.7250, 58.4900, '90MIN'),
       (66, '2022-01-01 00:00:00', 49.3970, 45.9240, 51.8990, 49.3970, '3MONTH'),
       (75, '2023-01-23 15:30:00', 112.7600, 112.6400, 113.0000, 112.7600, '90MIN'),
       (5, '2023-01-23 13:30:00', 99.2300, 99.1400, 100.0400, 99.2300, '1H');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (16, '2023-01-23 00:00:00', 148.5500, 148.0500, 150.4300, 148.5500, '1DAY'),
       (6, '2023-01-23 11:30:00', 85.7980, 85.2550, 86.1300, 85.7980, '1H'),
       (5, '2023-01-23 11:00:00', 99.9530, 99.3300, 99.9900, 99.9530, '90MIN'),
       (15, '2023-01-23 13:30:00', 180.8100, 180.7700, 181.6650, 180.8100, '1H'),
       (7, '2023-01-23 15:30:00', 137.2700, 136.9000, 137.5800, 137.2700, '90MIN'),
       (93, '2023-01-23 15:30:00', 116.7100, 116.5000, 70.0680, 116.7100, '90MIN'),
       (66, '2023-01-23 00:00:00', 48.6300, 48.5600, 48.9500, 48.6300, '1DAY'),
       (95, '2022-01-01 00:00:00', 359.2000, 233.3330, 111.4770, 359.2000, '3MONTH'),
       (19, '2023-01-23 09:30:00', 153.3000, 151.5500, 153.6500, 153.3000, '1H'),
       (20, '2023-01-18 00:00:00', 234.2500, 234.2100, 237.9000, 234.2500, '1DAY');

INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (39, '2023-01-19 00:00:00', 220.4100, 217.5000, 221.5200, 220.4100, '1DAY'),
       (93, '2023-01-02 00:00:00', 109.1100, 103.2800, 21.8760, 109.1100, '1WEEK'),
       (6, '2023-01-23 11:00:00', 85.7980, 85.2550, 86.4100, 85.7980, '90MIN'),
       (16, '2022-04-01 00:00:00', 147.5440, 130.7820, 169.4590, 147.5440, '3MONTH'),
       (67, '2023-01-19 00:00:00', 46.4600, 46.3900, 42.3450, 46.4600, '1DAY'),
       (48, '2023-01-18 00:00:00', 140.8400, 140.4800, 144.2500, 140.8400, '1DAY'),
       (20, '2022-07-01 00:00:00', 224.1310, 203.7380, 259.2080, 224.1310, '3MONTH'),
       (45, '2023-01-23 15:30:00', 64.1200, 63.9250, 64.2000, 64.1200, '90MIN'),
       (22, '2023-01-23 14:30:00', 191.1200, 189.0200, 191.4500, 191.1200, '1H'),
       (74, '2023-01-23 15:30:00', 97.5200, 96.9850, 97.6000, 97.5200, '1H');


INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (16, '2023-01-19 00:00:00', 148.7100, 147.3300, 150.5400, 148.7100, '1DAY'),
       (70, '2023-01-19 00:00:00', 94.2400, 94.1600, 95.5300, 94.2400, '1DAY'),
       (75, '2022-12-26 00:00:00', 110.3000, 107.9000, 110.5000, 110.3000, '1WEEK'),
       (67, '2023-01-23 09:30:00', 47.2900, 46.8100, 23.6925, 47.2900, '90MIN'),
       (43, '2023-01-23 12:30:00', 36.7150, 36.6500, 29.3800, 36.7150, '90MIN'),
       (45, '2022-01-01 00:00:00', 47.1320, 45.0710, 55.9920, 47.1320, '3MONTH'),
       (70, '2023-01-23 00:00:00', 96.2500, 94.5100, 96.9300, 96.2500, '1WEEK'),
       (39, '2023-01-09 00:00:00', 223.0600, 218.1800, 223.8100, 223.0600, '1WEEK'),
       (48, '2023-01-23 11:00:00', 142.5300, 142.0900, 142.8600, 142.5300, '90MIN'),
       (28, '2023-01-23 00:00:00', 95.6600, 91.7200, 95.7300, 95.6600, '1DAY');


INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (95, '2023-01-23 14:30:00', 142.4600, 141.3300, 57.7280, 142.4600, '1H'),
       (75, '2023-01-17 00:00:00', 112.9300, 112.6700, 114.0800, 112.9300, '1DAY'),
       (19, '2022-04-01 00:00:00', 136.7150, 132.4540, 189.7750, 136.7150, '3MONTH'),
       (43, '2023-01-09 00:00:00', 36.1500, 34.1600, 32.5800, 36.1500, '1WEEK'),
       (39, '2023-01-23 12:30:00', 225.0500, 224.7900, 225.2400, 225.0500, '1H'),
       (75, '2023-01-23 09:30:00', 113.9800, 113.3000, 114.2800, 113.9800, '1H'),
       (70, '2023-01-23 14:00:00', 96.0600, 95.9300, 96.8200, 96.0600, '90MIN'),
       (7, '2023-01-02 00:00:00', 136.9290, 132.7200, 137.3650, 136.9290, '1WEEK'),
       (16, '2023-01-01 00:00:00', 148.5500, 147.1500, 168.1100, 148.5500, '3MONTH'),
       (32, '2023-01-02 00:00:00', 180.2500, 176.0100, 180.9300, 180.2500, '1WEEK');


INSERT INTO trade_symbol_price_historic (trade_symbol_id, price_time, open_price, low_price, high_price, price, interval_period)
VALUES (43, '2023-01-19 00:00:00', 36.3900, 35.8800, 32.7780, 36.3900, '1DAY'),
       (5, '2023-01-23 11:30:00', 99.9530, 99.5200, 99.9900, 99.9530, '1H'),
       (6, '2023-01-23 12:30:00', 85.6650, 85.4300, 85.9200, 85.6650, '90MIN'),
       (30, '2023-01-23 13:30:00', 486.4500, 486.3300, 488.9120, 486.4500, '1H'),
       (6, '2023-01-20 00:00:00', 84.1300, 82.7300, 84.5200, 84.1300, '1DAY'),
       (75, '2023-01-23 12:30:00', 113.8800, 113.6600, 114.2220, 113.8800, '90MIN'),
       (63, '2023-01-23 11:30:00', 58.6200, 58.5450, 58.7600, 58.6200, '1H'),
       (30, '2023-01-23 12:30:00', 487.1600, 485.2600, 488.0400, 487.1600, '1H');


INSERT INTO app_user (email, passkey, enabled, personal_details, user_notifications, preferred_region, SECURITY_PIN, created_date, updated_date)
VALUES ('sally@tradex.com', '$2a$10$4IVKu1DVnBp9KSptIm8ywe3ewCE2OZD4g040Vth01ddGKRP9V5eM2', true, '{"fullName":"sally rose", "address":"sa jones", "phone":"+10000678", "country":"AUSTRALIA", "gender":"FEMALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED" }', 'sydney', 9999, now(), now()),
       ('molly@tradex.com', '$2a$10$ZP4jvGcjlgVyl5XcStQox.wO6mR/kDb0L5ubGniOOvLT6OfYebG8G', true, '{"fullName":"molly moe", "address":"dalal street", "phone":"+9178900009", "country":"INDIA", "gender":"FEMALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED"}', 'mumbai', 8888, now(), now()),
       ('leo@tradex.com', '$2a$10$kb/IL57Tb8MJsgzTk07egOgs4u/dATz717Ku1rpWFz1EstHfXZZqy', true, '{"fullName":"leo lance", "address":"10 Downing Street", "phone":"+4478900009", "country":"UK", "gender":"MALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED"}', 'london', 8888, now(), now()),
       ('scrooge@tradex.com', '$2a$10$8Vo.rS0D5zUt8m7QdiTN4.TQ01Rcw2FWQH1eQ1OH/94ivpcfL9Kue', true, '{"fullName":"uncle scrooge", "address":"Vault Street", "phone":"+178900009", "country":"USA", "gender":"MALE"}', '{"generalNotification":"ENABLED", "sound":"DISABLED", "vibrate":"ENABLED", "appUpdates":"DISABLED", "billReminder":"DISABLED", "promotion":"DISABLED", "discountAvailable":"DISABLED", "paymentReminder":"DISABLED", "newServiceAvailable":"DISABLED", "newTipsAvailable":"DISABLED"}', 'washington', 9999, now(), now());
