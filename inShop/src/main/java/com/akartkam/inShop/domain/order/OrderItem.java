/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.akartkam.inShop.domain.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.akartkam.inShop.domain.AbstractDomainObjectOrdering;
import com.akartkam.inShop.domain.product.Category;
import com.akartkam.inShop.domain.product.Product;
import com.akartkam.inShop.domain.product.Sku;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;


@Entity
@Table(name = "Customer_order_item")
public class OrderItem extends AbstractDomainObjectOrdering {
 
	private static final long serialVersionUID = -3021692296398817362L;

	private static final Log LOG = LogFactory.getLog(OrderItem.class);

    private Category category;
    private Order order;
    private BigDecimal price;
    private int quantity;
    private BigDecimal retailPrice;
    private BigDecimal salePrice;
    private String name;    
    private Boolean discountsAllowed;
    private Sku sku;
    private Product product; 

    @Column(name = "retail_price", precision=19, scale=5)
	@NumberFormat(style=Style.CURRENCY)
	@DecimalMin("0.01")	
	@Digits(fraction = 5, integer = 14)        
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    @Column(name = "sale_price", precision=19, scale=5)
	@NumberFormat(style=Style.CURRENCY)
	@DecimalMin("0.01")	
	@Digits(fraction = 5, integer = 14)    
    public BigDecimal getSalePrice() {
        if (salePrice != null) {
        	BigDecimal returnPrice = salePrice;
            if (retailPrice != null && returnPrice.compareTo(getRetailPrice())==1) {
                return getRetailPrice();
            } else {
                return returnPrice;
            }
        } else {
            return getRetailPrice();
        }
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @Column(name = "price", precision = 19, scale = 5)
	@NumberFormat(style=Style.CURRENCY)
	@DecimalMin("0.01")	
	@Digits(fraction = 5, integer = 14)    
    public BigDecimal getPrice() {
        return getAveragePrice();
    }

    public void setPrice(BigDecimal finalPrice) {
        setRetailPrice(finalPrice);
        setSalePrice(finalPrice);
        setDiscountingAllowed(false);
        this.price = finalPrice;
    }

    @Column(name = "quantity", nullable = false)
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    //@Index(name="orderitem_category_index", columnNames={"category_id"})
    @NotFound(action = NotFoundAction.IGNORE)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    //@Index(name="orderitem_order_index", columnNames={"order_id"})    
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public boolean getIsOnSale() {
        if (getSalePrice() != null) {
            return !getSalePrice().equals(getRetailPrice());
        } else {
            return false;
        }
    }

    @Transient
    public boolean getIsDiscounted() {
        if (getPrice() != null) {
            return !getPrice().equals(getRetailPrice());
        } else {
            return false;
        }
    }
 
    @Column(name = "discounts_allowed")
    public boolean isDiscountingAllowed() {
        if (discountsAllowed == null) {
            return true;
        } else {
            return discountsAllowed.booleanValue();
        }
    }

    public void setDiscountingAllowed(boolean discountsAllowed) {
        this.discountsAllowed = discountsAllowed;
    }

    @Transient
    public BigDecimal getAveragePrice() {
        if (quantity == 0) {
            return price;
        }
        BigDecimal value = BigDecimal.valueOf(quantity);
        return getTotalPrice().divide(value, RoundingMode.CEILING);
    }

    @Transient
    public BigDecimal getTotalPrice() {
    	BigDecimal returnValue = BigDecimal.ZERO;
    	BigDecimal value = BigDecimal.valueOf(quantity);
        if (price != null) {
            returnValue = price.multiply(value);
        } else {
            return getSalePrice().multiply(value);
        }
        return returnValue;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

    @ManyToOne
    @JoinColumn(name = "sku_id")
	public Sku getSku() {
		return sku;
	}

	public void setSku(Sku sku) {
		this.sku = sku;
	}

}