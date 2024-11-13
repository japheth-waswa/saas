package com.smis.common.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Right {
    RIGHT_GROUP_READ("Fetch right groups"),
    RIGHT_GROUP_CREATE("Create right groups"),
    RIGHT_GROUP_UPDATE("Update right groups"),
    RIGHT_GROUP_DELETE("Delete right groups"),
    USER_READ("Fetch users"),
    USER_CREATE("Create new user"),
    USER_UPDATE("Update user"),
    USER_DELETE("Delete user"),
    ACCOUNTING_PAYMENT_VOUCHER_CREATE("Create payment voucher");
    private final String description;
}
