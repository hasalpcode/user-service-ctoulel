package com.hasalp.ctoulel_user_service.exception;

public class TenantAccessDeniedException extends RuntimeException {
    public TenantAccessDeniedException(String msg) { super(msg); }
}
