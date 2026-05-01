package com.switchwon.fxordersystem.common;

public record ApiResponse<T>(String code, String message, T returnObject) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "정상적으로 처리되었습니다.", data);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
