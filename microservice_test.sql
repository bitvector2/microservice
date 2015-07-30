CREATE TABLE products (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);
INSERT INTO products (name) VALUES ('Foo');
INSERT INTO products (name) VALUES ('Bar');
INSERT INTO products (name) VALUES ('Baz');
