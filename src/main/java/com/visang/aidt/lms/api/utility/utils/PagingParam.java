package com.visang.aidt.lms.api.utility.utils;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Builder
@Data
public class PagingParam<T> {
    private T param;
    private Pageable pageable;
}
