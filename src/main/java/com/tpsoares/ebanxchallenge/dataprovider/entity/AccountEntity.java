package com.tpsoares.ebanxchallenge.dataprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccountEntity {
    private String id;
    private Integer balance;
}
