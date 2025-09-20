package com.devansh.exception;

import java.util.HashMap;

public record ValidationErrorResponse(
        HashMap<String, String> error
) {
}
