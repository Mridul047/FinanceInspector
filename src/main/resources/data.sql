CREATE TABLE fip.sys_user
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    created_on TIMESTAMP(6) NOT NULL,
    updated_on TIMESTAMP(6) NOT NULL
);

CREATE TABLE fip.salary_income
(
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id                BIGINT         NOT NULL,
    currency_code          VARCHAR(3)     NOT NULL,
    basic_amount           DECIMAL(15, 4) NOT NULL,
    hra_amount             DECIMAL(15, 4) NOT NULL,
    other_allowance_amount DECIMAL(15, 4) NOT NULL,
    bonus_amount           DECIMAL(15, 4) NOT NULL,
    emp_pf_amount          DECIMAL(15, 4) NOT NULL,
    profession_tax_amount  DECIMAL(15, 4) NOT NULL,
    income_tax_amount      DECIMAL(15, 4) NOT NULL,
    created_on             TIMESTAMP(6)   NOT NULL,
    updated_on             TIMESTAMP(6)   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES fip.sys_user (id)
);

INSERT INTO fip.sys_user (username, password, first_name, last_name, email, created_on, updated_on)
SELECT *
FROM CSVREAD('classpath:/csv_data/user.csv');

INSERT INTO fip.salary_income (user_id, currency_code, basic_amount, hra_amount, other_allowance_amount, bonus_amount,
                               emp_pf_amount,
                               profession_tax_amount, income_tax_amount, created_on, updated_on)
SELECT *
FROM CSVREAD('classpath:/csv_data/salary_income.csv');