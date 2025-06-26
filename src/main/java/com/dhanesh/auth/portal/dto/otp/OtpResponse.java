package com.dhanesh.auth.portal.dto.otp;

import java.time.Instant;

public record OtpResponse  (String message, Instant timestamp){}
