-- ONBOARDINGS Table
IF OBJECT_ID('onboardings', 'U') IS NULL
BEGIN
    CREATE TABLE onboardings (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        country_code NVARCHAR(5) NOT NULL,
        phone_number NVARCHAR(15) NOT NULL UNIQUE ,
        status NVARCHAR(50) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()

        CONSTRAINT idx_onboardings_country_code_phone_number UNIQUE(country_code, phone_number)
    );
END;

-- USERS Table
IF OBJECT_ID('users', 'U') IS NULL
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        country_code NVARCHAR(5) NOT NULL,
        phone_number NVARCHAR(15) NOT NULL UNIQUE,
        first_name NVARCHAR(50),
        last_name NVARCHAR(50),
        email NVARCHAR(50),
        address NVARCHAR(150),
        state NVARCHAR(30),
        lga NVARCHAR(50),
        alt_country_code NVARCHAR(5),
        alt_phone_number NVARCHAR(15),
        dob NVARCHAR(15),
        role NVARCHAR(50) NOT NULL,
        profile_photo NVARCHAR(MAX),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    CREATE INDEX idx_users_email ON users (email);
    CREATE INDEX idx_users_phone_number ON users (phone_number);
END;

-- ORGANIZATIONS Table
IF OBJECT_ID('organizations', 'U') IS NULL
BEGIN
    CREATE TABLE organizations (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        creator_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (creator_id) REFERENCES users (id)
    );
    CREATE INDEX idx_organizations_creator_id ON organizations (creator_id);
END;

-- BUSINESSES Table
IF OBJECT_ID('businesses', 'U') IS NULL
BEGIN
    CREATE TABLE businesses (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        organization_id BIGINT NOT NULL,
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
IF OBJECT_ID('cash_points', 'U') IS NULL
BEGIN
    CREATE TABLE cash_points (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        business_id BIGINT NOT NULL,
        virtual_account_no NVARCHAR(15) NULL,
        wallet_reference NVARCHAR(50) NULL,
        status NVARCHAR(15) NOT NULL,
        is_main BIT NOT NULL DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (business_id) REFERENCES businesses (id)
    );
    CREATE INDEX idx_cash_points_business_id ON cash_points (business_id);
    CREATE INDEX idx_cash_points_virtual_account_no ON cash_points (virtual_account_no);
    CREATE INDEX idx_cash_points_wallet_reference ON cash_points (wallet_reference);
    CREATE INDEX idx_cash_points_status ON cash_points (status);
    CREATE INDEX idx_cash_points_is_main ON cash_points (is_main);
END;

-- ADMIN/ORGANIZATION USERS Table (SunTrust's)
IF OBJECT_ID('organization_users', 'U') IS NULL
BEGIN
    CREATE TABLE organization_users (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        first_name NVARCHAR(50),
        last_name NVARCHAR(50),
        email NVARCHAR(50) NOT NULL UNIQUE,
        role NVARCHAR(50) NOT NULL,
        status NVARCHAR(50) NOT NULL,
        country_code NVARCHAR(5),
        phone_number NVARCHAR(15),
        profile_photo NVARCHAR(MAX),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    CREATE INDEX idx_organization_users_email ON organization_users (email);
END;

-- Roles Table
IF OBJECT_ID('roles', 'U') IS NULL
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(40) DEFAULT REPLACE(CONVERT(VARCHAR(36), NEWID()), '-', '') NOT NULL UNIQUE,
        name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(300) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );

    CREATE INDEX idx_id ON roles (id);
    CREATE INDEX idx_reference ON roles (reference);

    SET IDENTITY_INSERT roles ON;

    INSERT INTO roles (id, name, description)
    VALUES
    (1, 'ADMIN', 'Responsible for managing and overseeing all administrative and operational aspects of the organization, including user management, system settings, and compliance.'),
    (2, 'BUSINESS', 'Focuses on business strategy, partnerships, and market growth. This role oversees client relationships, product offerings, and the alignment of business goals with operational execution.'),
    (3, 'FINANCE', 'Handles financial operations such as budgeting, accounting, transaction monitoring, and financial reporting. This role ensures regulatory compliance and the overall financial health of the organization.'),
    (4, 'SUPPORT', 'Provides technical and customer support to ensure seamless user experiences. Responsible for resolving client issues, answering queries, and maintaining satisfaction across all platforms.');

    SET IDENTITY_INSERT roles OFF;
END;

-- ==========================
-- PERMISSIONS TABLE
-- ==========================
-- Purpose:
-- This table is used to store all permissions in the system.

-- NAMING CONVENTION:
-- Format: 'Company Acronym:Service:Category:Action'
-- - Company Acronym: A short code for the company or system (e.g., 'stb' for Super Tech Bank).
-- - Service: The specific service area (e.g., 'transaction').
-- - Category: The type of operation or group (e.g., 'withdrawal').
-- - Action: The CRUD action or specific operation (e.g., 'read_transactions').

-- Example:
-- 'stb:transaction:withdrawal:read_transactions' - Permission to read withdrawal transactions.
-- 'stb:user:management:create_user' - Permission to create a user.

-- Notes:
-- - Try to ensure that names are concise and consistent.
-- - Avoid overly generic terms; be as specific as possible.

IF OBJECT_ID('permissions', 'U') IS NULL
BEGIN
    CREATE TABLE permissions (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(40) DEFAULT REPLACE(CONVERT(VARCHAR(36), NEWID()), '-', '') NOT NULL UNIQUE,
        name NVARCHAR(255) NOT NULL,
        category NVARCHAR(100) NOT NULL,
        description NVARCHAR(255),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );

    CREATE INDEX idx_permissions_id ON permissions (id);
    CREATE INDEX idx_permissions_reference ON permissions (reference);

    SET IDENTITY_INSERT permissions ON;

    INSERT INTO permissions (id, category, name)
    VALUES
    (1, 'ORGANIZATION_ACCOUNT_MANAGEMENT', 'stb:account:organization_account_management:read_admins'),
    (2, 'ORGANIZATION_ACCOUNT_MANAGEMENT', 'stb:account:organization_account_management:read_admin'),
    (3, 'ORGANIZATION_ACCOUNT_MANAGEMENT', 'stb:account:organization_account_management:create_admin'),
    (4, 'ORGANIZATION_ACCOUNT_MANAGEMENT', 'stb:account:organization_account_management:update_admin'),
    (5, 'ORGANIZATION_ACCOUNT_MANAGEMENT', 'stb:account:organization_account_management:delete_admin'),

    (6, 'ORGANIZATION_CUSTOMER_MANAGEMENT', 'stb:account:organization_customer_management:read_customers'),
    (7, 'ORGANIZATION_CUSTOMER_MANAGEMENT', 'stb:account:organization_customer_management:read_customer'),
    (8, 'ORGANIZATION_CUSTOMER_MANAGEMENT', 'stb:account:organization_customer_management:update_customer'),
    (9, 'ORGANIZATION_CUSTOMER_MANAGEMENT', 'stb:account:organization_customer_management:delete_customer'),

    (10, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read_wallets'),
    (11, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read_wallet'),
    (12, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read_wallet_credits'),
    (13, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read_wallet_debits'),
    (14, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read_wallet_balance');


    SET IDENTITY_INSERT permissions OFF;
END;

-- Role Permissions Table
IF OBJECT_ID('role_permissions', 'U') IS NULL
BEGIN
    CREATE TABLE role_permissions (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        role_id BIGINT NOT NULL,
        permission_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
        CONSTRAINT fk_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE,
        CONSTRAINT uc_role_permission UNIQUE (role_id, permission_id)
    );

    CREATE INDEX idx_role_permissions_role_id ON role_permissions (role_id);
    CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

    INSERT INTO role_permissions (role_id, permission_id)
    VALUES --[role_id :: ADMIN=1,BUSINESS=2,FINANCE=3,SUPPORT=4]
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),
    (2,6),(2,7),(2,10),(2,11),
    (3,6),(3,7),(3,10),(3,11),(3,12),(3,13),(3,14),
    (4,6), (4,7);
END;

-- Account_Organization_Permissions Table creation and Table update
IF OBJECT_ID('organization_user_permissions', 'U') IS NULL
BEGIN
    CREATE TABLE organization_user_permissions (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        permission_id BIGINT NOT NULL,
        organization_user_id BIGINT NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_role_permission_id FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE,
        CONSTRAINT fk_organization_user_id FOREIGN KEY (organization_user_id) REFERENCES organization_users (id) ON DELETE CASCADE,
        CONSTRAINT uc_organization_user_permissions UNIQUE (organization_user_id, permission_id)
    );

    CREATE INDEX idx_organization_user_permissions_organization_user_id ON organization_user_permissions (organization_user_id);
    CREATE INDEX idx_organization_user_permissions_permission_id ON organization_user_permissions (permission_id);
END;

-- Separate batch for triggers
GO

-- Trigger for permission assignment on organization user creation
CREATE TRIGGER trigger_assign_permissions_after_insert
ON organization_users
AFTER INSERT
AS
BEGIN
    INSERT INTO organization_user_permissions (permission_id, organization_user_id, enabled)
    SELECT
        rp.permission_id,
        i.id AS organization_user_id,
        1 AS enabled
    FROM inserted i
    JOIN roles r ON i.role = r.name
    JOIN role_permissions rp ON r.id = rp.role_id;
END;
GO
-- Trigger for permission assignment on organization user role change
CREATE TRIGGER trg_update_permissions_on_role_change
ON organization_users
AFTER UPDATE
AS
BEGIN
    -- Ensure the trigger runs only when the `role` column is updated
    IF UPDATE(role)
    BEGIN
        -- Delete all permissions for the updated users
        DELETE FROM organization_user_permissions
        WHERE organization_user_id IN (SELECT id FROM inserted);

        -- Assign new permissions based on the updated role
        INSERT INTO organization_user_permissions (permission_id, organization_user_id, enabled)
        SELECT
            rp.permission_id,
            i.id AS organization_user_id,
            1 AS enabled
        FROM inserted i
        JOIN roles r ON i.role = r.name
        JOIN role_permissions rp ON r.id = rp.role_id;
    END
END;
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
