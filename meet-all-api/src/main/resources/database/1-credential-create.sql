CREATE TABLE credential (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    credential VARCHAR(4095) NOT NULL,
    label VARCHAR(255) NOT NULL,
    provider VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL
);

-- drop table credential;
