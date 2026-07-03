package com.example.demo.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

    private int id;

    private String name;

    private String description;

    private String brand;

    private BigDecimal price;

    private String category;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;

    private boolean available;

    private int quantity;

    private String imageName;

    private String imageType;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] imageData;

    private UserSummaryDto createdBy;
}