package com.temp.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicResponse<T> {
    private String message;
    private T content;
    private boolean isSuccess;

    public void setSuccess(T content, String message) {
        this.content = content;
        this.message = message;
        this.isSuccess = Boolean.TRUE;
    }

    public void setFailed(String errorMessage) {
        this.content = null;
        this.message = errorMessage;
        this.isSuccess = Boolean.FALSE;
    }
}
