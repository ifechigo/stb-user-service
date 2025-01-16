package com.suntrustbank.user.core.dtos;

import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.BaseResponseStatus;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Generated
public class BasePagedResponse<T> {

    private BaseResponseStatus status;
    private T data;
    private BaseResponseMessage message;
    private PageData page;

    @Getter
    @Setter
    @Builder
    public static class PageData {
        private int page;
        private int size;
        private int totalPages;
        private int totalElements;
        private int numberOfElements;
    }
}
