package com.seowon.coding.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private int stockQuantity;
    
    private String category;
    
    // Business logic
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public void updatePrice(double percentage, boolean includeTax) {
        BigDecimal base;

        if (this.price == null) {
            base = BigDecimal.ZERO;
        } else {
            base = this.price;
        }

        BigDecimal rate = BigDecimal.valueOf(percentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal changed = base.multiply(BigDecimal.ONE.add(rate));
        // Product 객체 내 switch 문으로 지역/카테고리별 규칙으로 changed 재할당
        if (includeTax) {
            BigDecimal taxRate = getTaxRate();
            changed = changed.multiply(taxRate);
        }
        // 임의 반올림: 일관되지 않은 스케일/반올림 모드
        BigDecimal newPrice = changed.setScale(2, RoundingMode.HALF_UP);
        this.setPrice(newPrice);
    }

    private BigDecimal getTaxRate() {
        if (this.category == null) {
            return BigDecimal.valueOf(1.1);
        }
        return switch (this.category.toUpperCase()) {
            case "FOOD" -> BigDecimal.valueOf(1.05);
            case "CAR" -> BigDecimal.valueOf(1.3);
            case "BOOK" -> BigDecimal.valueOf(1.2);
            default -> BigDecimal.valueOf(1.1);
        };
    }
    
    public void decreaseStock(int quantity) {
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        stockQuantity -= quantity;
    }
    
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity += quantity;
    }
}