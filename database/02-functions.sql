SET search_path TO public;

CREATE SEQUENCE IF NOT EXISTS seq_customer_code START WITH 1;
CREATE SEQUENCE IF NOT EXISTS seq_restaurant_code START WITH 1;
CREATE SEQUENCE IF NOT EXISTS seq_branch_code START WITH 1;
CREATE SEQUENCE IF NOT EXISTS seq_order_code START WITH 1;
CREATE SEQUENCE IF NOT EXISTS seq_offer_code START WITH 1;

CREATE OR REPLACE FUNCTION generate_customer_code()
RETURNS TRIGGER AS $$
DECLARE
    new_code TEXT;
    seq INT;
BEGIN
    seq := NEXTVAL('seq_customer_code');
    new_code := 'CLI-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || LPAD(seq::TEXT, 5, '0');
    NEW.code := new_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_restaurant_code()
RETURNS TRIGGER AS $$
DECLARE
    new_code TEXT;
    seq INT;
BEGIN
    seq := NEXTVAL('seq_restaurant_code');
    new_code := 'REST-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || LPAD(seq::TEXT, 5, '0');
    NEW.code := new_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_branch_code()
RETURNS TRIGGER AS $$
DECLARE
    new_code TEXT;
    seq INT;
BEGIN
    seq := NEXTVAL('seq_branch_code');
    new_code := 'SUC-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || LPAD(seq::TEXT, 5, '0');
    NEW.code := new_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_order_code()
RETURNS TRIGGER AS $$
DECLARE
    new_code TEXT;
    seq INT;
    date_part TEXT;
BEGIN
    date_part := TO_CHAR(NEW.created_at, 'YYYYMMDD');
    seq := NEXTVAL('seq_order_code');
    new_code := 'PED-' || date_part || '-' || LPAD(seq::TEXT, 5, '0');
    NEW.code := new_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_offer_code()
RETURNS TRIGGER AS $$
DECLARE
    new_code TEXT;
    seq INT;
BEGIN
    seq := NEXTVAL('seq_offer_code');
    new_code := 'OFE-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || LPAD(seq::TEXT, 5, '0');
    NEW.code := new_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
