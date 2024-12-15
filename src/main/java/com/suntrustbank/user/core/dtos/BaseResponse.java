package com.suntrustbank.user.core.dtos;

import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.BaseResponseStatus;
import lombok.*;

@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private BaseResponseStatus status;
    private T data;
    private String message;

    public static <T> BaseResponse<T> success(final T data, final BaseResponseMessage baseResponseMessage) {
        return BaseResponse.<T>builder()
            .data(data)
            .message(baseResponseMessage.getValue())
            .status(BaseResponseStatus.SUCCESS)
            .build();
    }

    public static <T> BaseResponse<T> error(final BaseResponseMessage baseResponseMessage) {
        return BaseResponse.<T>builder()
            .message(baseResponseMessage.getValue())
            .status(BaseResponseStatus.ERROR)
            .build();
    }
}
