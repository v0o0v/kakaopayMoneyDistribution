package com.kakaopaycorp.moneydistribution.service.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {


    private String errorMessage;

    public ErrorResponse(String msg) {
        this.errorMessage = msg;
    }
}
