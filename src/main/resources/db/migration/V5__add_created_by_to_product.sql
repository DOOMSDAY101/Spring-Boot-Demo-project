ALTER TABLE product
ADD COLUMN created_by INT;

UPDATE product
SET created_by = (
    SELECT id
    FROM users
    WHERE username = 'admin'
);

ALTER TABLE product
ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE product
ADD CONSTRAINT fk_product_created_by
FOREIGN KEY (created_by)
REFERENCES users(id);