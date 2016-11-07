package com.akartkam.inShop.domain.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import com.akartkam.inShop.domain.AbstractDomainObjectOrdering;
import com.akartkam.inShop.domain.product.attribute.AbstractAttribute;
import com.akartkam.inShop.domain.product.attribute.AbstractAttributeValue;
import com.akartkam.inShop.domain.product.attribute.SimpleAttributeFactory;
import com.akartkam.inShop.domain.product.option.ProductOption;
import com.akartkam.inShop.presentation.admin.AdminPresentation;
import com.akartkam.inShop.presentation.admin.EditTab;

@Entity
@Table(name = "Product")
@SuppressWarnings("rawtypes")
public class Product extends AbstractDomainObjectOrdering {

	/**
	 * 
	 */
	private static final long serialVersionUID = -583044339566068826L;
	private Category category;
	private Brand brand;
	private String model;
	private List<AbstractAttributeValue> attributeValues = new ArrayList<AbstractAttributeValue>();
	private String url;
	private Sku defaultSku;
    private List<Sku> additionalSku = new ArrayList<Sku>();	
    private Set<ProductOption> productOptions = new HashSet<ProductOption>();
    private boolean canSellWithoutOptions = true;
	
	@Column(name = "can_sell_without_options")
	public boolean isCanSellWithoutOptions() {
		return canSellWithoutOptions;
	}
	public void setCanSellWithoutOptions(boolean canSellWithoutOptions) {
		this.canSellWithoutOptions = canSellWithoutOptions;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(nullable=false)
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
    @Column(name = "url")
    @Index(name="product_url_index", columnNames={"url"})	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
		
	@ManyToOne
	@JoinColumn
	public Brand getBrand() {
		return brand;
	}
	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	@Column(name = "model")
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	public boolean canRemove() {
		return additionalSku.isEmpty();
	};
		
	@OneToMany(mappedBy="product", cascade = CascadeType.ALL, orphanRemoval=true)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@BatchSize(size = 20)
	public List<AbstractAttributeValue> getAttributeValues() {
		return attributeValues;
	}
	public void setAttributeValues(List<AbstractAttributeValue> attributeValues) {
		this.attributeValues = attributeValues;
		Collections.sort(attributeValues, new Product.AVComparer());
	}	
	
	public void addAttributeValue (AbstractAttributeValue attributeValue) {
		if (attributeValue == null) throw new IllegalArgumentException("Null attributeValue!");
		attributeValue.setProduct(this);
		getAttributeValues().add(attributeValue);		
	}
	
	public void addAttributeValue (AbstractAttributeValue attributeValue, AbstractAttribute attribute) {
		if (attributeValue == null) throw new IllegalArgumentException("Null attributeValue!");
		if (attribute == null) throw new IllegalArgumentException("Null attribute!");
		if (attribute.getAttributeType() != attributeValue.getAttributeValueType()) throw new IllegalArgumentException("The type of attribute and attributeValue is different!"); 
		attributeValue.setAttribute(attribute);
		addAttributeValue(attributeValue);
	}
	
	public void addAttributeValue (AbstractAttribute attribute) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (attribute == null) throw new IllegalArgumentException("Null attribute!");
		AbstractAttributeValue attributeValue = SimpleAttributeFactory.createAttributeValue(attribute.getAttributeType());
		addAttributeValue(attributeValue, attribute);
	}
	
	public void removeAttributeValue (AbstractAttributeValue attributeValue) {
		List<AbstractAttributeValue> lav =  getAttributeValues();
		if (lav!=null) {
			lav.remove(attributeValue);
		}
	}
	
	public void addAdditionalSku(Sku sku) {
		if (sku == null) throw new IllegalArgumentException("Null sku!");
		sku.setProduct(this);
		getAdditionalSku().add(sku);		
	}
	
	@AdminPresentation(tab=EditTab.LINKS)
	@Valid
	@ManyToMany
	@JoinTable(name = "lnk_product_option", joinColumns = { 
			@JoinColumn(name = "product_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "product_option_id", 
					nullable = false, updatable = false) })
	public Set<ProductOption> getProductOptions() {
		return productOptions;
	}
	
	public void setProductOptions(Set<ProductOption> productOption) {
		this.productOptions = productOption;
	}
	
    public void addProductOption(ProductOption productOption) {
        if (productOption == null) throw new IllegalArgumentException("Null productOption!");
        productOptions.add(productOption);
    }
    public void removeProductOption(ProductOption productOption) {
        if (productOption == null) throw new IllegalArgumentException("Null productOption!");
        productOptions.remove(productOption);
    }
    
    @Valid
    @OneToOne(cascade={CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name="default_sku_id")
	public Sku getDefaultSku() {
		return defaultSku;
	}

    public void setDefaultSku(Sku defaultSku) {
	       if (defaultSku != null) {
	            defaultSku.setDefaultProduct(this);

	        }
	        this.defaultSku = defaultSku;
	}
	
	@OneToMany(mappedBy="product")
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@BatchSize(size = 10)
	public List<Sku> getAdditionalSku() {
		return additionalSku;
	}
	public void setAdditionalSku(List<Sku> additionalSku) {
		this.additionalSku = additionalSku;
	}
	
    @Transient
    public List<Sku> getSkus() {
        List<Sku> skus = new ArrayList<Sku>();
        for (Sku sku : getAdditionalSku()) {
            if (sku.isEnabled()) {
                skus.add(sku);
            }
        }
        return skus;
    }	
	
	public void buildFullLink(String shortUrl) {
		if (shortUrl == null || "".equals(shortUrl)) return;
		if (shortUrl.startsWith("/")) shortUrl = shortUrl.substring(1);     
        StringBuilder linkBuffer = new StringBuilder(50);
        linkBuffer.append(this.getCategory().getUrl()).append("/").append(shortUrl);
        setUrl(linkBuffer.toString());		
	}    
    
	@Override
	@Transient
	public Product clone() throws CloneNotSupportedException {
		Product product = (Product) super.clone();
		product.setId(UUID.randomUUID());
		product.setUrl(new String(getUrl()));
		product.setModel(new String(getModel()));
		product.setProductOptions(new HashSet<ProductOption>());
		product.setAttributeValues(new ArrayList<AbstractAttributeValue>());
		product.setDefaultSku(getDefaultSku().clone());
		product.setCreatedBy(null);
		product.setCreatedDate(null);
		product.setUpdatedBy(null);
		product.setUpdatedDate(null);
		return product;
	}  
	
	public static class AVComparer implements Comparator<AbstractAttributeValue> {

		@Override
		public int compare(AbstractAttributeValue arg0, AbstractAttributeValue arg1) {
			AbstractAttribute at0 = (arg0!=null?arg0.getAttribute():null);
			AbstractAttribute at1 = (arg1!=null?arg1.getAttribute():null);
		    if (at0 == null ^ at1 == null) {
		        return (at0 == null) ? -1 : 1;
		    }

		    if (at0 == null && at1 == null) {
		        return 0;
		    }

			return at0.compareTo(at1) ;
		}
		  
	}
	

}
