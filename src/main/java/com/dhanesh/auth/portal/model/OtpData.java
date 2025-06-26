package com.dhanesh.auth.portal.model;

import java.time.Instant;

public record OtpData(String otp, Instant expiry) {}
