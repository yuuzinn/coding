package com.seowon.coding.dto;

import lombok.Getter;

import java.util.List;


@Getter
public class CreateOrderRequest {
    private String customerName;
    private String customerEmail;
    private List<ProductMetadataDto> products;
}
