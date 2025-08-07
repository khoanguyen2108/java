package com.podbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Pod Booking System Application
 * @author KhoaHong-dev
 * @since 2025-08-07 12:35:37 UTC
 */
@SpringBootApplication
public class PodBookingApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Pod Booking System - KhoaHong-dev");
        SpringApplication.run(PodBookingApplication.class, args);
        System.out.println("✅ Pod Booking System started successfully!");
    }
}