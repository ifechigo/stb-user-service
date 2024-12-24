-- USERS Table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
BEGIN
    CREATE TABLE users (
        id NVARCHAR(50) PRIMARY KEY,
        country_code NVARCHAR(5) NOT NULL,
        phone_number NVARCHAR(15) NOT NULL UNIQUE,
        first_name NVARCHAR(50) NULL,
        last_name NVARCHAR(50) NULL,
        email NVARCHAR(50) NULL UNIQUE,
        address NVARCHAR(150) NULL,
        state NVARCHAR(30) NULL,
        lga NVARCHAR(50) NULL,
        alt_country_code NVARCHAR(5) NULL,
        alt_phone_number NVARCHAR(15) NULL,
        dob NVARCHAR(15) NULL,
        role NVARCHAR(50) NOT NULL,
        profile_photo NVARCHAR(MAX) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    CREATE INDEX idx_users_email ON users (email);
    CREATE INDEX idx_users_phone_number ON users (phone_number);
END;

-- ORGANIZATIONS Table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='organizations' AND xtype='U')
BEGIN
    CREATE TABLE organizations (
        id NVARCHAR(50) PRIMARY KEY,
        creator_id NVARCHAR(50) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (creator_id) REFERENCES users (id)
    );
    CREATE INDEX idx_organizations_creator_id ON organizations (creator_id);
END;

-- BUSINESSES Table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='businesses' AND xtype='U')
BEGIN
    CREATE TABLE businesses (
        id NVARCHAR(50) PRIMARY KEY,
        organization_id NVARCHAR(50) NOT NULL,
        name NVARCHAR(50) NULL,
        email NVARCHAR(50) NULL,
        address NVARCHAR(150) NULL,
        cac_number NVARCHAR(30) NULL,
        logo_image NVARCHAR(MAX) NULL,
        country_code NVARCHAR(5) NULL,
        phone_number NVARCHAR(15) NULL,
        business_type NVARCHAR(50) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (organization_id) REFERENCES organizations (id)
    );
    CREATE INDEX idx_businesses_email ON businesses (email);
    CREATE INDEX idx_businesses_phone_number ON businesses (phone_number);
    CREATE INDEX idx_businesses_organization_id ON businesses (organization_id);
END;

-- CASHPOINT Table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='cash_points' AND xtype='U')
BEGIN
    CREATE TABLE cash_points (
        id NVARCHAR(50) PRIMARY KEY,
        business_id NVARCHAR(50) NOT NULL,
        virtual_account_no NVARCHAR(15) NULL,
        wallet_id NVARCHAR(15) NULL,
        status NVARCHAR(15) NOT NULL,
        is_main BIT NOT NULL DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (business_id) REFERENCES businesses (id)
    );
    CREATE INDEX idx_cash_points_business_id ON cash_points (business_id);
    CREATE INDEX idx_cash_points_virtual_account_no ON cash_points (virtual_account_no);
    CREATE INDEX idx_cash_points_wallet_id ON cash_points (wallet_id);
    CREATE INDEX idx_cash_points_status ON cash_points (status);
    CREATE INDEX idx_cash_points_is_main ON cash_points (is_main);
END;

-- ONBOARDINGS Table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='onboardings' AND xtype='U')
BEGIN
    CREATE TABLE onboardings (
        id NVARCHAR(50) PRIMARY KEY,
        country_code NVARCHAR(5) NOT NULL,
        phone_number NVARCHAR(15) NOT NULL UNIQUE ,
        status NVARCHAR(50) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()

        CONSTRAINT idx_onboardings_country_code_phone_number UNIQUE(country_code, phone_number)
    );
END;

-- Separate batch for triggers
GO

-- Trigger for email uniqueness between users and businesses
CREATE TRIGGER trigger_user_business_email
ON businesses
AFTER INSERT, UPDATE
AS
BEGIN
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN users u ON i.email = u.email
    )
    BEGIN
        RAISERROR('email provided already exists.', 16, 1);
        ROLLBACK;
    END;
END;
GO
CREATE TRIGGER trigger_business_user_email
ON users
AFTER INSERT, UPDATE
AS
BEGIN
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN businesses b ON i.email = b.email
    )
    BEGIN
        RAISERROR('email provided already exists.', 16, 1);
        ROLLBACK;
    END;
END;
GO

-- Trigger to ensure the phone number (and altPhoneNumber) in the users and businesses tables are unique

-- Trigger for phoneNumber uniqueness between users and businesses
CREATE TRIGGER trigger_user_business_phone_number
ON businesses
AFTER INSERT, UPDATE
AS
BEGIN
    -- Check if the phone number is already in use by a user
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN users u ON i.phone_number = u.phone_number
    )
    BEGIN
        RAISERROR('Phone number already exists.', 16, 1);
        ROLLBACK;
    END;

    -- Check if the altPhoneNumber is already in use by a user
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN users u ON i.phone_number = u.alt_phone_number
    )
    BEGIN
        RAISERROR('Phone number already exists.', 16, 1);
        ROLLBACK;
    END;
END;
GO

CREATE TRIGGER trigger_business_user_phone_number
ON users
AFTER INSERT, UPDATE
AS
BEGIN
    -- Check if the phone number is already in use by a business
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN businesses b ON i.phone_number = b.phone_number
    )
    BEGIN
        RAISERROR('Phone number already exists.', 16, 1);
        ROLLBACK;
    END;

    -- Check if the altPhoneNumber is already in use by a business
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN businesses b ON i.alt_phone_number = b.phone_number
    )
    BEGIN
        RAISERROR('Phone number already exists.', 16, 1);
        ROLLBACK;
    END;
END;
GO
