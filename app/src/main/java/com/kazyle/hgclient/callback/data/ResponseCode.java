package com.kazyle.hgclient.callback.data;

/**
 * Created by Kazyle on 2016/8/23.
 */
public enum ResponseCode {

    SUCCESS(1000),          // 成功

    VALIDATE_ERROR(1001),   // 数据验证失败

    FAILURE(1002),          // 失败

    NOT_FOUND(1003),        // 接口不存在

    SING_ERROR(1004),       // 签名错误

    REQUEST_TIMEOUT(1005),  // 请求时间失效

    ERROR(1006),            // 系统异常

    ;

    private int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
