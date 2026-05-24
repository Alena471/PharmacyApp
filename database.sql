

-- 1. Производители
CREATE TABLE manufacturers (
    manufacturer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50)
);

-- 2. Лекарства
CREATE TABLE medicines (
    medicine_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dosage VARCHAR(50),
    release_form VARCHAR(50),
    manufacturer_id INT NOT NULL,
    prescription_required BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id)
);

-- 3. Поставщики
CREATE TABLE suppliers (
    supplier_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255)
);

-- 4. Партии
CREATE TABLE batches (
    batch_id SERIAL PRIMARY KEY,
    batch_number VARCHAR(50) NOT NULL,
    medicine_id INT NOT NULL,
    supplier_id INT NOT NULL,
    delivery_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    quantity INT NOT NULL,
    purchase_price NUMERIC(10,2) NOT NULL,
    sale_price NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- 5. Сотрудники
CREATE TABLE employees (
    employee_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(50),
    phone VARCHAR(20)
);

-- 6. Клиенты
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20)
);

-- 7. Продажи
CREATE TABLE sales (
    sale_id SERIAL PRIMARY KEY,
    sale_date TIMESTAMP NOT NULL,
    employee_id INT NOT NULL,
    customer_id INT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- 8. Позиции продаж
CREATE TABLE sale_items (
    sale_item_id SERIAL PRIMARY KEY,
    sale_id INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
);

-- =====================================================
-- ТЕСТОВЫЕ ДАННЫЕ
-- =====================================================

INSERT INTO manufacturers (name, country) VALUES
('Фармстандарт', 'Россия'),
('Bayer', 'Германия'),
('Novartis', 'Швейцария'),
('Pfizer', 'США'),
('Teva', 'Израиль');

INSERT INTO medicines (name, dosage, release_form, manufacturer_id, prescription_required) VALUES
('Парацетамол', '500 мг', 'таблетки', 1, FALSE),
('Ибупрофен', '200 мг', 'таблетки', 2, FALSE),
('Амоксиклав', '875 мг', 'таблетки', 3, TRUE),
('Но-шпа', '40 мг', 'таблетки', 4, FALSE),
('Анальгин', '500 мг', 'таблетки', 5, FALSE);

INSERT INTO suppliers (name, phone, email, address) VALUES
('ФармОпт', '1234567890', 'info@farmopt.ru', 'Москва, ул. Ленина, 1'),
('МедСнаб', '0987654321', 'info@medsnab.ru', 'Санкт-Петербург, Невский пр., 10'),
('АптекаЛогистик', '1112223334', 'log@apteka.ru', 'Казань, ул. Баумана, 5'),
('МедПоставка', '5556667778', 'post@med.ru', 'Екатеринбург, ул. Малышева, 20'),
('EuroPharm', '9998887776', 'eu@pharm.com', 'Берлин, унтер-ден-Линден, 1');

INSERT INTO batches (batch_number, medicine_id, supplier_id, delivery_date, expiration_date, quantity, purchase_price, sale_price) VALUES
('A001', 1, 1, '2025-01-10', '2027-12-31', 200, 50.00, 120.00),
('B002', 2, 2, '2025-01-15', '2027-12-31', 150, 80.00, 180.00),
('C003', 3, 3, '2025-02-01', '2027-12-31', 100, 120.00, 250.00),
('D004', 4, 4, '2025-02-10', '2027-12-31', 300, 30.00, 70.00),
('E005', 5, 5, '2025-02-20', '2027-12-31', 80, 200.00, 400.00);

INSERT INTO employees (first_name, last_name, position, phone) VALUES
('Иван', 'Иванов', 'Старший фармацевт', '+79161234567'),
('Мария', 'Петрова', 'Фармацевт', '+79162345678'),
('Алексей', 'Сидоров', 'Управляющий', '+79163456789'),
('Елена', 'Кузнецова', 'Фармацевт', '+79164567890'),
('Дмитрий', 'Смирнов', 'Кассир', '+79165678901');

INSERT INTO customers (first_name, last_name, phone) VALUES
('Анна', 'Морозова', '+79161234567'),
('Петр', 'Волков', '+79162345678'),
('Ольга', 'Соколова', '+79163456789'),
('Сергей', 'Новиков', '+79164567890'),
('Татьяна', 'Федорова', '+79165678901');

INSERT INTO sales (sale_date, employee_id, customer_id) VALUES
('2026-05-20 10:30:00', 1, 1),
('2026-05-20 11:45:00', 2, 2),
('2026-05-21 09:15:00', 3, 3),
('2026-05-21 14:20:00', 4, 4),
('2026-05-22 12:00:00', 5, 5);

INSERT INTO sale_items (sale_id, batch_id, quantity, price) VALUES
(1, 1, 2, 120.00),
(1, 2, 1, 180.00),
(2, 3, 1, 250.00),
(3, 4, 3, 70.00),
(4, 5, 2, 400.00);