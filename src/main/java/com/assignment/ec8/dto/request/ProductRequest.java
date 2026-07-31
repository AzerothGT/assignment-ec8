package com.assignment.ec8.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    @NotNull(message = "Price wajib diisi")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price harus lebih dari 0")
    private BigDecimal price;

    private String description;

    @NotNull(message = "Stock wajib diisi")
    @Min(value = 0, message = "Stock harus 0 atau lebih dari 0 (≥0)")
    private Integer stock;
}
