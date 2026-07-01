CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    brand VARCHAR(255),
    price NUMERIC(10,2) NOT NULL,
    category VARCHAR(255),
    release_date DATE,
    available BOOLEAN NOT NULL,
    quantity INTEGER NOT NULL,
    image_name VARCHAR(255),
    image_type VARCHAR(100),
    image_data BYTEA
);