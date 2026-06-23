CREATE TABLE countries (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY
);

CREATE TABLE cities (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    country_id UUID NOT NULL,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE city_translations (
    city_id UUID NOT NULL,
    lang VARCHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (city_id, lang),
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

CREATE TABLE country_translations (
    country_id UUID NOT NULL,
    lang VARCHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (country_id, lang),
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE
);

CREATE TABLE users (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    is_locked BOOLEAN DEFAULT FALSE
);

CREATE TABLE user_translations (
    user_id UUID NOT NULL,
    lang VARCHAR(5) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, lang),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE roles (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE refresh_tokens (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tours (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    price DECIMAL(10, 2) NOT NULL,
    city_id UUID NOT NULL,
    arrival_date TIMESTAMP NOT NULL,
    eviction_date TIMESTAMP NOT NULL,
    is_hot BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (city_id) REFERENCES cities(id)
);

CREATE TABLE tours_translations (
    tours_id UUID NOT NULL,
    lang VARCHAR(5) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    tour_type VARCHAR(50) NOT NULL,
    transfer_type VARCHAR(50) NOT NULL,
    hotel_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (tours_id, lang),
    FOREIGN KEY (tours_id) REFERENCES tours(id) ON DELETE CASCADE
);

CREATE TABLE user_tours (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id UUID NOT NULL,
    tour_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tour_id) REFERENCES tours(id)
);
