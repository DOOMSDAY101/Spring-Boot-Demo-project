package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product entity")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Schema(example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(example = "iPhone 16")
    private String name;

    @Schema(example = "Latest Apple smartphone")
    private String description;

    @Schema(example = "Apple")
    private String brand;

    @Schema(example = "999.99")
    private BigDecimal price;

    @Schema(example = "Electronics")
    private String category;

    @Schema(example = "20-05-2025")
    @JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;

    @Schema(example = "true")
    private boolean available;

    @Schema(example = "50")
    private int quantity;

    @Schema(hidden = true)
    private String imageName;

    @Schema(hidden = true)
    private String imageType;

    @Schema(hidden = true)
    @Lob
    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] imageData;
}
