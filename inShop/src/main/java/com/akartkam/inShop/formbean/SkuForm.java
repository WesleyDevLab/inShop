package com.akartkam.inShop.formbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.akartkam.inShop.domain.product.Category;
import com.akartkam.inShop.domain.product.Sku;
import com.akartkam.inShop.domain.product.attribute.AbstractAttribute;
import com.akartkam.inShop.domain.product.attribute.AbstractAttributeValue;
import com.akartkam.inShop.domain.product.attribute.AttributeValuesHolderType;
import com.akartkam.inShop.domain.product.option.ProductOption;
import com.akartkam.inShop.domain.product.option.ProductOptionValue;
import com.akartkam.inShop.presentation.admin.AdminPresentation;
import com.akartkam.inShop.presentation.admin.EditTab;

public class SkuForm extends Sku {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4368976934463748993L;
    private List<ProductOptionValue> productOptionValuesList = new ArrayList<ProductOptionValue>();
    private List<ProductOption> productOptionsList = new ArrayList<ProductOption>();
	public SkuForm() {
		
	}

	public SkuForm(Sku sku) {
		if (sku != null) {
			this.setId(sku.getId());
			this.setActiveEndDate(sku.getActiveEndDate());
			this.setActiveStartDate(sku.getActiveStartDate());
			this.setCode(sku.getCode());
			this.setCostPrice(sku.getCostPrice());
			this.setDescription(sku.getDescription());
			this.setEnabled(sku.isEnabled());
			this.setImages(sku.getImages());
			this.setLongDescription(sku.getLongDescription());
			if (sku.getName() == null || "".equals(sku.getName())) {
				this.setName(sku.getProduct().getDefaultSku().getName());
			} else {
				this.setName(sku.getName());				
			}
			this.setOrdering(sku.getOrdering());
			this.setProduct(sku.getProduct());
			this.setCreatedDate(sku.getCreatedDate());
			this.setQuantityAvailable(sku.getQuantityAvailable());
			this.setRetailPrice(sku.getRetailPrice());
			this.setSalePrice(sku.getSalePrice());
			this.setInventoryType(sku.getInventoryType());
			this.setAttributeValues(sku.getAttributeValues());
			this.setProductOptionValues(sku.getProductOptionValues());
			this.setQuantityPerPackage(sku.getQuantityPerPackage());
			this.productOptionsList = new ArrayList<ProductOption>(sku.getProduct().getProductOptions());
			this.setProductOptionValuesList(new ArrayList<ProductOptionValue>(sku.getProductOptionValues())); 
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void complementNecessaryAttributes() throws ClassNotFoundException, InstantiationException, IllegalAccessException {			 
			 List<AbstractAttribute> at = new ArrayList<AbstractAttribute>();
			 
			 Category ct = isDefaultSku()? getDefaultProduct().getCategory(): getProduct().getCategory();
					 
			 if (ct == null) return;
			 at = ct.getAllAttributes(at, true, AttributeValuesHolderType.SKU);
			 List<AbstractAttributeValue> av = getAttributeValues();
			 boolean needAdd;
			 for (AbstractAttribute cat : at) {
				 needAdd = true;
				 for (AbstractAttributeValue cav: av) {
					 if (cat.equals(cav.getAttribute())) {
						needAdd = false;
						break;
					 }
				 }
				 if (needAdd) {
					 addAttributeValue(cat);
				 }
			 }
	  }	
	
	//For synhronize right order of optionValues to ProductOptions 
	private void setProductOptionValuesList(List<ProductOptionValue> skuProductOptionValues) {
		if (productOptionsList.isEmpty()) return;
		productOptionValuesList.clear();
		for (ProductOption po : productOptionsList) {
			for (ProductOptionValue pov :skuProductOptionValues) {
				if (po.equals(pov.getProductOption())) { 
					productOptionValuesList.add(pov);
					break;
				}
			}
		}
	}
	
	@NotEmpty
	@AdminPresentation(tab=EditTab.ADDITIONAL)
	public List<ProductOptionValue> getProductOptionValuesList() {
		productOptionValuesList.removeAll(Collections.singletonList(null));
		return productOptionValuesList;
	}
	
	
	public void setProductOptionValuesFromList() {
		setProductOptionValues(new HashSet<ProductOptionValue>(getProductOptionValuesList()));
	}

	public List<ProductOption> getProductOptionsList() {
		return productOptionsList;
	}	
	

}
