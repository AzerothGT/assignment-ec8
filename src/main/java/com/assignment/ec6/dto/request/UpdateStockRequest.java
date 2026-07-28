package com.assignment.ec6.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStockRequest {

    @NotNull(message = "Jumlah stok yang terjual wajib diisi")
    @Min(value = 1, message = "Jumlah stok yang terjual tidak boleh 0 atau negatif")
    private Integer soldQuantity;
}
