SET search_path TO public;

CREATE TRIGGER trg_generate_customer_code
BEFORE INSERT ON customers
FOR EACH ROW
WHEN (NEW.code IS NULL)
EXECUTE FUNCTION generate_customer_code();

CREATE TRIGGER trg_generate_restaurant_code
BEFORE INSERT ON restaurants
FOR EACH ROW
WHEN (NEW.code IS NULL)
EXECUTE FUNCTION generate_restaurant_code();

CREATE TRIGGER trg_generate_branch_code
BEFORE INSERT ON branches
FOR EACH ROW
WHEN (NEW.code IS NULL)
EXECUTE FUNCTION generate_branch_code();

CREATE TRIGGER trg_generate_order_code
BEFORE INSERT ON orders
FOR EACH ROW
WHEN (NEW.code IS NULL)
EXECUTE FUNCTION generate_order_code();

CREATE TRIGGER trg_generate_offer_code
BEFORE INSERT ON offers
FOR EACH ROW
WHEN (NEW.code IS NULL)
EXECUTE FUNCTION generate_offer_code();