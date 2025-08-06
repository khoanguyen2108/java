-- ================================
-- DROP OLD DATABASE AND CREATE NEW ONE
-- ================================

-- 1. BACKUP DATABASE CŨ (QUAN TRỌNG!)
-- Chạy lệnh này trước khi drop để backup data
SHOW DATABASES;
-- Thay 'your_old_database_name' bằng tên database thực tế của bạn
-- mysqldump -u username -p your_old_database_name > backup_$(date +%Y%m%d_%H%M%S).sql

-- 2. DROP DATABASE CŨ
-- Thay 'your_old_database_name' bằng tên database của bạn
DROP DATABASE IF EXISTS your_old_database_name;

-- 3. TẠO DATABASE MỚI
CREATE DATABASE pod_booking_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 4. SỬ DỤNG DATABASE MỚI
USE pod_booking_system;

-- ================================
-- TẠO SCHEMA MỚI HOÀN CHỈNH
-- ================================

-- 1. ROLES TABLE
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. USERS TABLE
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    points INT DEFAULT 0 CHECK (points >= 0),
    is_vip BOOLEAN DEFAULT FALSE,
    vip_tier ENUM('bronze', 'silver', 'gold', 'platinum') NULL,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 3. LOCATIONS TABLE
CREATE TABLE locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100),
    phone VARCHAR(20),
    operating_hours JSON,
    amenities JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. PODS TABLE
CREATE TABLE pods (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    pod_type ENUM('individual', 'team', 'meeting_room', 'phone_booth') NOT NULL,
    status ENUM('available', 'occupied', 'maintenance', 'out_of_order') DEFAULT 'available',
    capacity INT NOT NULL CHECK (capacity > 0),
    hourly_rate DECIMAL(10,2) NOT NULL CHECK (hourly_rate > 0),
    daily_rate DECIMAL(10,2),
    amenities JSON,
    location_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES locations(id),
    UNIQUE KEY unique_pod_per_location (location_id, name)
);

-- 5. PACKAGES TABLE
CREATE TABLE packages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    duration_type ENUM('hour', 'day', 'week', 'month') NOT NULL,
    duration_value INT NOT NULL CHECK (duration_value > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    validity_days INT DEFAULT 30,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. USER PACKAGES TABLE
CREATE TABLE user_packages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    package_id INT NOT NULL,
    remaining_hours INT,
    remaining_days INT,
    expiry_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- 7. SERVICES TABLE
CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category ENUM('food', 'beverage', 'printing', 'equipment', 'meeting_room', 'other') DEFAULT 'other',
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    unit ENUM('item', 'hour', 'page', 'session') DEFAULT 'item',
    description TEXT,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8. BOOKINGS TABLE
CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    pod_id INT NOT NULL,
    user_package_id INT NULL,
    booking_code VARCHAR(20) UNIQUE NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    duration_hours DECIMAL(4,2) NOT NULL,
    base_amount DECIMAL(10,2) NOT NULL,
    service_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'checked_in', 'completed', 'cancelled', 'no_show') DEFAULT 'pending',
    payment_status ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending',
    payment_method ENUM('package', 'credit_card', 'paypal', 'momo', 'cash') NULL,
    check_in_time TIMESTAMP NULL,
    check_out_time TIMESTAMP NULL,
    special_requests TEXT,
    cancellation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pod_id) REFERENCES pods(id),
    FOREIGN KEY (user_package_id) REFERENCES user_packages(id),
    CHECK (end_time > start_time),
    CHECK (duration_hours > 0)
);

-- 9. BOOKING SERVICES TABLE
CREATE TABLE booking_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('ordered', 'preparing', 'delivered', 'cancelled') DEFAULT 'ordered',
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id),
    UNIQUE KEY unique_booking_service (booking_id, service_id)
);

-- 10. PAYMENTS TABLE
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    booking_id INT NULL,
    user_package_id INT NULL,
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'VND',
    method ENUM('credit_card', 'debit_card', 'paypal', 'momo', 'zalopay', 'bank_transfer', 'cash') NOT NULL,
    status ENUM('pending', 'processing', 'completed', 'failed', 'cancelled', 'refunded') DEFAULT 'pending',
    gateway ENUM('stripe', 'paypal', 'momo', 'zalopay', 'vnpay') NULL,
    transaction_id VARCHAR(255),
    gateway_response JSON,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (user_package_id) REFERENCES user_packages(id)
);

-- 11. FEEDBACK TABLE
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    booking_id INT NOT NULL,
    pod_id INT NOT NULL,
    overall_rating INT NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    cleanliness_rating INT CHECK (cleanliness_rating BETWEEN 1 AND 5),
    service_rating INT CHECK (service_rating BETWEEN 1 AND 5),
    amenities_rating INT CHECK (amenities_rating BETWEEN 1 AND 5),
    comment TEXT,
    photos JSON,
    admin_response TEXT,
    responded_by INT NULL,
    responded_at TIMESTAMP NULL,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (pod_id) REFERENCES pods(id),
    FOREIGN KEY (responded_by) REFERENCES users(id),
    UNIQUE KEY unique_feedback_per_booking (user_id, booking_id)
);

-- 12. NOTIFICATIONS TABLE
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    booking_id INT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('booking', 'payment', 'reminder', 'promotion', 'system') DEFAULT 'system',
    channel ENUM('email', 'sms', 'push', 'in_app') DEFAULT 'in_app',
    priority ENUM('low', 'normal', 'high', 'urgent') DEFAULT 'normal',
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    scheduled_for TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- 13. CALENDAR SYNC TABLE
CREATE TABLE calendar_sync (
    user_id INT PRIMARY KEY,
    provider ENUM('google', 'outlook', 'apple', 'other') NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    sync_status ENUM('active', 'inactive', 'error', 'expired') DEFAULT 'inactive',
    last_sync_at TIMESTAMP NULL,
    sync_frequency ENUM('real_time', 'hourly', 'daily') DEFAULT 'hourly',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 14. WAITLIST TABLE
CREATE TABLE waitlists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    pod_id INT NOT NULL,
    preferred_start_time TIMESTAMP NOT NULL,
    preferred_end_time TIMESTAMP NOT NULL,
    priority_score INT DEFAULT 0,
    max_wait_hours INT DEFAULT 24,
    status ENUM('waiting', 'notified', 'expired', 'cancelled', 'booked') DEFAULT 'waiting',
    notified_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pod_id) REFERENCES pods(id)
);

-- 15. ANALYTICS TABLE
CREATE TABLE daily_analytics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_recorded DATE NOT NULL,
    location_id INT NOT NULL,
    pod_id INT NULL,
    total_bookings INT DEFAULT 0,
    total_revenue DECIMAL(12,2) DEFAULT 0.00,
    total_hours_booked DECIMAL(8,2) DEFAULT 0.00,
    unique_customers INT DEFAULT 0,
    vip_customers INT DEFAULT 0,
    occupancy_rate DECIMAL(5,2) DEFAULT 0.00,
    average_duration DECIMAL(4,2) DEFAULT 0.00,
    cancellation_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (pod_id) REFERENCES pods(id),
    UNIQUE KEY unique_daily_record (date_recorded, location_id, pod_id)
);

-- ================================
-- CREATE INDEXES FOR PERFORMANCE
-- ================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_users_vip ON users(is_vip, vip_tier);
CREATE INDEX idx_users_active ON users(is_active);

-- Booking indexes
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_pod_time ON bookings(pod_id, start_time, end_time);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_payment_status ON bookings(payment_status);
CREATE INDEX idx_bookings_date_range ON bookings(start_time, end_time);
CREATE INDEX idx_bookings_code ON bookings(booking_code);

-- Pod indexes
CREATE INDEX idx_pods_location ON pods(location_id);
CREATE INDEX idx_pods_type ON pods(pod_type);
CREATE INDEX idx_pods_status ON pods(status);
CREATE INDEX idx_pods_active ON pods(is_active);

-- Package indexes
CREATE INDEX idx_user_packages_user ON user_packages(user_id, is_active);
CREATE INDEX idx_user_packages_expiry ON user_packages(expiry_date, is_active);

-- Payment indexes
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_method ON payments(method);
CREATE INDEX idx_payments_reference ON payments(payment_reference);

-- Notification indexes
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_for);

-- Analytics indexes
CREATE INDEX idx_analytics_date ON daily_analytics(date_recorded);
CREATE INDEX idx_analytics_location_date ON daily_analytics(location_id, date_recorded);

-- Waitlist indexes
CREATE INDEX idx_waitlists_user ON waitlists(user_id, status);
CREATE INDEX idx_waitlists_pod_time ON waitlists(pod_id, preferred_start_time);
CREATE INDEX idx_waitlists_expires ON waitlists(expires_at, status);

-- ================================
-- INSERT INITIAL DATA
-- ================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('admin', 'System Administrator with full access'),
('manager', 'Location Manager with management privileges'),
('staff', 'Staff member with limited access'),
('customer', 'Regular customer with booking privileges');

-- Insert admin user (password should be hashed in real application)
INSERT INTO users (name, email, password, role_id, is_active, email_verified) VALUES
('System Admin', 'admin@podbooking.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, TRUE, TRUE),
('KhoaHong Dev', 'khoahong.dev@podbooking.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, TRUE, TRUE);

-- Insert sample location
INSERT INTO locations (name, address, city, phone, operating_hours, amenities) VALUES
('POD Coworking District 1', '123 Nguyen Hue Street, District 1', 'Ho Chi Minh City', '+84 28 1234 5678', 
'{"monday": {"open": "08:00", "close": "22:00"}, "tuesday": {"open": "08:00", "close": "22:00"}}',
'["high_speed_wifi", "free_coffee", "parking", "printer", "meeting_rooms", "phone_booths"]');

-- Insert sample pods
INSERT INTO pods (name, pod_type, capacity, hourly_rate, daily_rate, amenities, location_id) VALUES
('Individual Pod A1', 'individual', 1, 50000, 350000, '["desk", "chair", "monitor", "power_outlets"]', 1),
('Individual Pod A2', 'individual', 1, 50000, 350000, '["desk", "chair", "monitor", "power_outlets"]', 1),
('Team Pod B1', 'team', 4, 150000, 1000000, '["large_table", "chairs", "whiteboard", "projector"]', 1),
('Meeting Room C1', 'meeting_room', 8, 300000, 2000000, '["conference_table", "projector", "whiteboard", "video_conference"]', 1),
('Phone Booth D1', 'phone_booth', 1, 30000, 200000, '["soundproof", "chair", "phone"]', 1);

-- Insert sample packages
INSERT INTO packages (name, duration_type, duration_value, price, discount_percentage, validity_days, description) VALUES
('Basic Hourly', 'hour', 10, 450000, 10.00, 30, '10 hours package with 10% discount'),
('Premium Daily', 'day', 5, 1500000, 15.00, 30, '5 days package with 15% discount'),
('Weekly Power', 'week', 4, 5000000, 20.00, 30, '4 weeks package with 20% discount'),
('Monthly Unlimited', 'month', 1, 8000000, 25.00, 30, '1 month unlimited access with 25% discount');

-- Insert sample services
INSERT INTO services (name, category, price, unit, description) VALUES
('Vietnamese Coffee', 'beverage', 25000, 'item', 'Authentic Vietnamese drip coffee'),
('Business Lunch', 'food', 85000, 'item', 'Healthy business lunch set'),
('Color Printing A4', 'printing', 5000, 'page', 'High quality color printing'),
('Black & White Printing', 'printing', 2000, 'page', 'Standard black and white printing'),
('External Monitor', 'equipment', 50000, 'hour', '24-inch external monitor rental'),
('Meeting Room Upgrade', 'meeting_room', 100000, 'hour', 'Upgrade to premium meeting room');

-- ================================
-- SHOW CREATED TABLES
-- ================================
SHOW TABLES;

-- ================================
-- VERIFICATION QUERIES
-- ================================
SELECT 'Database created successfully!' as status;
SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema = 'pod_booking_system';
SELECT table_name FROM information_schema.tables WHERE table_schema = 'pod_booking_system' ORDER BY table_name;