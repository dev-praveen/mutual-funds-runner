CREATE TABLE IF NOT EXISTS public.mutual_fund
(
    fund_symbol character varying COLLATE pg_catalog."default",
    quote_type character varying COLLATE pg_catalog."default",
    region character varying COLLATE pg_catalog."default",
    fund_short_name character varying COLLATE pg_catalog."default",
    fund_long_name character varying COLLATE pg_catalog."default",
    currency character varying COLLATE pg_catalog."default",
    initial_investment bigint,
    subsequent_investment bigint,
    fund_category character varying COLLATE pg_catalog."default",
    fund_family character varying COLLATE pg_catalog."default",
    exchange_code character varying COLLATE pg_catalog."default",
    exchange_name character varying COLLATE pg_catalog."default",
    exchange_timezone character varying COLLATE pg_catalog."default",
    management_name character varying COLLATE pg_catalog."default",
    management_bio text COLLATE pg_catalog."default",
    management_start_date date,
    total_net_assets bigint,
    year_to_date_return double precision,
    id integer NOT NULL DEFAULT nextval('mutual_fund_id_seq'::regclass),
    CONSTRAINT mutual_fund_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS public.top_mutual_fund
(
	id integer NOT NULL,
    fund_symbol character varying COLLATE pg_catalog."default",
    fund_short_name character varying COLLATE pg_catalog."default",
    fund_long_name character varying COLLATE pg_catalog."default",
    initial_investment bigint,
    subsequent_investment bigint,
    fund_category character varying COLLATE pg_catalog."default",
    fund_family character varying COLLATE pg_catalog."default",
    management_name character varying COLLATE pg_catalog."default",
    management_bio text COLLATE pg_catalog."default",
    management_start_date date,
    total_net_assets bigint,
    year_to_date_return double precision,
    CONSTRAINT top_mutual_fund_pkey PRIMARY KEY (id)
)
