package com.smis.common.core.dto;

public record ApiResponse<T>(String message,T data) {
}
