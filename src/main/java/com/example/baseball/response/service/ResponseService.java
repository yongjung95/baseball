package com.example.baseball.response.service;

import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.model.CommonResult;
import com.example.baseball.response.model.ListResult;
import com.example.baseball.response.model.SingleResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    public enum CommonResponse {
        SUCCESS(200,"200", "SUCCESS");

        int status;
        String code;
        String msg;

        CommonResponse(int status, String code, String msg) {
            this.status = status;
            this.code = code;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    private void setSuccessResult(CommonResult result) {
        result.setStatus(CommonResponse.SUCCESS.getStatus());
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMessage(CommonResponse.SUCCESS.getMsg());
    }

    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }

    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setData(list);
        setSuccessResult(result);
        return result;
    }

    public <T> SingleResult<T> getSuccessResult() {
        SingleResult<T> result = new SingleResult<>();
        setSuccessResult(result);
        return result;
    }

    public <T> SingleResult<T> getFailResult(ErrorCode errorCode) {
        SingleResult<T> result = new SingleResult<>();

        result.setStatus(errorCode.getStatus());
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage());

        return result;
    }

    public <T> SingleResult<T> getFailParameter(String message) {
        SingleResult<T> result = new SingleResult<>();

        result.setStatus(ErrorCode.PARAMETER_IS_EMPTY.getStatus());
        result.setCode(ErrorCode.PARAMETER_IS_EMPTY.getCode());
        result.setMessage(message);

        return result;
    }

}