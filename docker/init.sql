
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS merchants CASCADE;
DROP TABLE IF EXISTS locations CASCADE;


CREATE TABLE IF NOT EXISTS cards (
    card_id SERIAL PRIMARY KEY,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    cardholder_name VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    card_type VARCHAR(20),
    issuing_bank VARCHAR(50),
    cvv VARCHAR(3),
    email VARCHAR(50)
);

CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100),
    CONSTRAINT unique_location UNIQUE(city, state, country)
);


CREATE TABLE merchants (
    merchant_id SERIAL PRIMARY KEY,
    merchant_name VARCHAR(150) NOT NULL,
    merchant_category VARCHAR(100),
    CONSTRAINT unique_merchant_name UNIQUE(merchant_name)
);


CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    card_id INT NOT NULL REFERENCES cards(card_id),
    amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    transaction_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_channel VARCHAR(50),
    merchant_id INT NOT NULL REFERENCES merchants(merchant_id),
    location_id INT NOT NULL REFERENCES locations(location_id)
);


INSERT INTO merchants(merchant_name, merchant_category) VALUES ('Amazon', 'E-Commerce') ON CONFLICT (merchant_name) DO NOTHING;
INSERT INTO locations(city, state, country) VALUES ('India', 'Telangana', 'Hyderabad') ON CONFLICT (city, state, country) DO NOTHING;
