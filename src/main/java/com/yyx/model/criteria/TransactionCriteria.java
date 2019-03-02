package com.yyx.model.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class TransactionCriteria {
    private String account;
    private Timestamp startTime;
    private Timestamp endTime;
    @Builder.Default
    private int pageStart = -1;
    @Builder.Default
    private int pageSize = -1;
}
