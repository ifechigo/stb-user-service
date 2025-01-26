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

-- ADMIN USERS Table (SunTrust's)
IF OBJECT_ID('admin_users', 'U') IS NULL
BEGIN
    CREATE TABLE admin_users (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(50) NOT NULL UNIQUE,
        first_name NVARCHAR(50),
        last_name NVARCHAR(50),
        email NVARCHAR(50) NOT NULL UNIQUE,
        role NVARCHAR(50) NOT NULL,
        is_team_lead BIT NOT NULL DEFAULT 0,
        status NVARCHAR(50) NOT NULL,
        country_code NVARCHAR(5),
        phone_number NVARCHAR(15),
        profile_photo NVARCHAR(MAX),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );

    -- Creating indexes
    CREATE INDEX idx_admin_users_reference ON admin_users (reference);
    CREATE INDEX idx_admin_users_email ON admin_users (email);
    CREATE INDEX idx_admin_users_role ON admin_users (role);
    CREATE INDEX idx_admin_users_is_team_lead ON admin_users (is_team_lead);
    CREATE INDEX idx_admin_users_status ON admin_users (status);
END;

-- Roles Table
IF OBJECT_ID('roles', 'U') IS NULL
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(40) DEFAULT LOWER(REPLACE(CONVERT(VARCHAR(36), NEWID()), '-', '')) NOT NULL UNIQUE,
        name NVARCHAR(50) NOT NULL,
        role_type NVARCHAR(50) NOT NULL,
        is_team_lead BIT NOT NULL DEFAULT 0,
        description NVARCHAR(300) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );

    CREATE INDEX idx_id ON roles (id);
    CREATE INDEX idx_reference ON roles (reference);
    CREATE INDEX idx_is_team_lead ON roles (is_team_lead);
    CREATE INDEX idx_name ON roles (name);

    SET IDENTITY_INSERT roles ON;

    -- Insert roles
    INSERT INTO roles (id, name, role_type, is_team_lead, description)
    VALUES
    (1, 'ADMIN', 'ADMIN', 0, 'Responsible for managing and overseeing all administrative and operational aspects.'),
    (2, 'BUSINESS', 'MEMBER', 0, 'Focuses on business strategy and partnerships.'),
    (3, 'BUSINESS', 'TEAM_LEAD', 1, 'Leads the business team and aligns goals with execution.'),
    (4, 'FINANCE', 'MEMBER', 0, 'Handles financial operations and regulatory compliance.'),
    (5, 'FINANCE', 'TEAM_LEAD', 1, 'Leads the finance team and ensures financial health.'),
    (6, 'SUPPORT', 'MEMBER', 0, 'Provides technical and customer support.'),
    (7, 'SUPPORT', 'TEAM_LEAD', 1, 'Leads the support team and maintains client satisfaction.');

    SET IDENTITY_INSERT roles OFF;
END;

-- ==========================
-- PERMISSIONS TABLE
-- ==========================
-- Purpose:
-- This table is used to store all permissions in the system.

-- NAMING CONVENTION:
-- Format: 'Company Acronym:Service(Operation):Category:Action'
-- - Company Acronym: A short code for the company or system (e.g., 'stb' for Super Tech Bank).
-- - Service(Operation): The specific service area or the specific operation in a service that been undertaken (e.g., 'transaction', terminal).
-- - Category: The type of operation or group (e.g., 'withdrawal').
-- - Action: The CRUD action or specific operation (e.g., 'read_transactions').

-- Example:
-- 'stb:transaction:withdrawal:read_transactions' - Permission to read withdrawal transactions.
-- 'stb:user:management:create_user' - Permission to create a user.

-- Notes:
-- - Try to ensure that names are concise and consistent.
-- - Avoid overly generic terms; be as specific as possible.

-- Permissions Table
IF OBJECT_ID('permissions', 'U') IS NULL
BEGIN
    CREATE TABLE permissions (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference NVARCHAR(40) DEFAULT LOWER(REPLACE(CONVERT(VARCHAR(36), NEWID()), '-', '')) NOT NULL UNIQUE,
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
    (1, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:create_admin'),
    (2, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:read_admin'),
    (3, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:update_admin_status'),
    (4, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:update_admin_role'),
    (5, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:read_permissions'),
    (6, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:update_admin_permission'),
    (7, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:read_admin_permission'),
    (8, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:delete_admin_permission'),
    (9, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:create_team_lead'),
    (10, 'ADMIN_MANAGEMENT', 'stb:account:admin_management:delete_team_lead'),

    (11, 'CUSTOMER_MANAGEMENT', 'stb:account:customer_management:read'),
    (12, 'CUSTOMER_MANAGEMENT', 'stb:account:customer_management:create'),
    (13, 'CUSTOMER_MANAGEMENT', 'stb:account:customer_management:update'),
    (14, 'CUSTOMER_MANAGEMENT', 'stb:account:customer_management:delete'),

    (15, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:read'),
    (16, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:create'),
    (17, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:update'),
    (18, 'CUSTOMER_WALLET_MANAGEMENT', 'stb:wallet:customer_wallet_management:delete'),

    (19, 'WITHDRAWAL_TRANSACTION_MANAGEMENT', 'stb:transaction:withdrawal_transaction_management:read'),
    (20, 'WITHDRAWAL_TRANSACTION_MANAGEMENT', 'stb:transaction:withdrawal_transaction_management:create'),
    (21, 'WITHDRAWAL_TRANSACTION_MANAGEMENT', 'stb:transaction:withdrawal_transaction_management:update'),
    (22, 'WITHDRAWAL_TRANSACTION_MANAGEMENT', 'stb:transaction:withdrawal_transaction_management:delete'),

    (23, 'TRANSFER_TRANSACTION_MANAGEMENT', 'stb:transaction:transfer_transaction_management:read'),
    (24, 'TRANSFER_TRANSACTION_MANAGEMENT', 'stb:transaction:transfer_transaction_management:create'),
    (25, 'TRANSFER_TRANSACTION_MANAGEMENT', 'stb:transaction:transfer_transaction_management:update'),
    (26, 'TRANSFER_TRANSACTION_MANAGEMENT', 'stb:transaction:transfer_transaction_management:delete'),

    (27, 'TERMINAL_MANAGEMENT', 'stb:transaction:terminal_management:read'),
    (28, 'TERMINAL_MANAGEMENT', 'stb:transaction:terminal_management:create'),
    (29, 'TERMINAL_MANAGEMENT', 'stb:transaction:terminal_management:update'),
    (30, 'TERMINAL_MANAGEMENT', 'stb:transaction:terminal_management:delete'),

    (31, 'TICKET_MANAGEMENT', 'stb:support:ticket_management:read'),
    (32, 'TICKET_MANAGEMENT', 'stb:support:ticket_management:create'),
    (33, 'TICKET_MANAGEMENT', 'stb:support:ticket_management:update'),
    (34, 'TICKET_MANAGEMENT', 'stb:support:ticket_management:delete');

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
    VALUES --[role_id :: ADMIN=1,BUSINESS=2,LEAD_BUSINESS=3,FINANCE=4,LEAD_FINANCE=5,SUPPORT=6,LEAD_SUPPORT=7]
        -- ADMIN: Full access to all permissions
        (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),
        (1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),
        -- BUSINESS: Access to customer and transaction-related permissions
        (2,5),(2,6),(2,7),(2,8),(2,13),(2,14),(2,17),(2,18),
        -- LEAD_BUSINESS: Extended access for business management
        (3,5),(3,6),(3,7),(3,8),(3,9),(3,10),(3,13),(3,14),(3,15),(3,16),(3,17),(3,18),(3,19),(3,20),
        -- FINANCE: Access to wallet and transaction permissions
        (4,9),(4,10),(4,11),(4,12),(4,13),(4,14),(4,15),(4,16),(4,17),(4,18),(4,19),(4,20),
        -- LEAD_FINANCE: Extended access for financial management
        (5,9),(5,10),(5,11),(5,12),(5,13),(5,14),(5,15),(5,16),(5,17),(5,18),(5,19),(5,20),(5,21),(5,22),(5,23),(5,24),
        -- SUPPORT: Can read wallets, transactions, and terminals; no creation or modification
        (6,9),(6,13),(6,17),(6,21),(6,25),(6,26),(6,27),(6,28),
        -- LEAD_SUPPORT: Extended support management with read access to wallets, transactions, and terminals
        (7,9),(7,10),(7,13),(7,14),(7,17),(7,18),(7,21),(7,25),(7,26),(7,27),(7,28);
END;

-- Account_Organization_Permissions Table creation and Table update
IF OBJECT_ID('admin_user_permissions', 'U') IS NULL
BEGIN
    CREATE TABLE admin_user_permissions (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        permission_id BIGINT NOT NULL,
        admin_user_id BIGINT NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_role_permission_id FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE,
        CONSTRAINT fk_admin_user_id FOREIGN KEY (admin_user_id) REFERENCES admin_users (id) ON DELETE CASCADE,
        CONSTRAINT uc_admin_user_permissions UNIQUE (admin_user_id, permission_id)
    );

    CREATE INDEX idx_admin_user_permissions_admin_user_id ON admin_user_permissions (admin_user_id);
    CREATE INDEX idx_admin_user_permissions_permission_id ON admin_user_permissions (permission_id);
END;

-- Separate batch for triggers
GO

-- Trigger for permission assignment on organization user creation
CREATE TRIGGER trigger_assign_permissions_after_insert
ON admin_users
AFTER INSERT
AS
BEGIN
    -- Insert permissions for the newly created user based on role and is_team_lead status
    INSERT INTO admin_user_permissions (permission_id, admin_user_id, enabled)
    SELECT
        rp.permission_id,
        i.id AS admin_user_id,
        1 AS enabled
    FROM inserted i
    -- Match the role and is_team_lead of the inserted user with the roles table
    JOIN roles r ON i.role = r.name AND i.is_team_lead = r.is_team_lead
    -- Join with role_permissions to get the correct permissions for the role
    JOIN role_permissions rp ON r.id = rp.role_id;
END;
GO

-- Trigger for permission assignment on organization user role change
CREATE TRIGGER trg_update_permissions_on_role_change
ON admin_users
AFTER UPDATE
AS
BEGIN
    -- Ensure the trigger runs only when the role or is_team_lead columns are updated
    IF UPDATE(role) OR UPDATE(is_team_lead)
    BEGIN
        -- Delete all permissions for the updated users
        DELETE FROM admin_user_permissions
        WHERE admin_user_id IN (SELECT id FROM inserted);

        -- Assign new permissions based on the updated role and is_team_lead status
        INSERT INTO admin_user_permissions (permission_id, admin_user_id, enabled)
        SELECT
            rp.permission_id,
            i.id AS admin_user_id,
            1 AS enabled
        FROM inserted i
        -- Match the role and is_team_lead of the inserted user with the roles table
        JOIN roles r ON i.role = r.name AND i.is_team_lead = r.is_team_lead
        -- Join with role_permissions to get the correct permissions for the role
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

BEGIN
    -- Create default admin user
    INSERT INTO admin_users (reference, first_name, last_name, email, role, status)
    VALUES
    ('00000000000000000000', 'admin', 'admin', 'admin@gmail.com', 'ADMIN', 'ACTIVE');
END;
GO