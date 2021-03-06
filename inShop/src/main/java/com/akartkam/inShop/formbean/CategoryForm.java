package com.akartkam.inShop.formbean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import com.akartkam.inShop.domain.product.Category;
import com.akartkam.inShop.domain.product.Product;
import com.akartkam.inShop.domain.product.attribute.AbstractAttribute;

public class CategoryForm extends Category {

	/**
	 * 
	 */
	private static final long serialVersionUID = -500394871795849096L;

	private List<AbstractAttribute> attributesForForm = new ArrayList<AbstractAttribute>();
	private String urlForForm;
	
	public CategoryForm() { }
	public CategoryForm(Category category) { 
		this.setId(category.getId());
		this.setCreatedDate(category.getCreatedDate());
		this.setAttributes(new HashSet<AbstractAttribute>(category.getAttributes()));
		this.setDescription(category.getDescription());
		this.setEnabled(category.isEnabled());
		this.setLongDescription(category.getLongDescription());
		this.setName(category.getName());
		this.setOrdering(category.getOrdering());
		this.setParent(category.getParent());
		this.setProducts(new ArrayList<Product>(category.getProducts()));
		this.setSubCategory(category.getSubCategory());
		this.setUrl(category.getUrl());
		this.setShowQuanPerPackOnProductHeader(category.getShowQuanPerPackOnProductHeader());
		this.attributesForForm = new ArrayList<AbstractAttribute>(getAttributes());
		this.setInstruction(category.getInstruction());
		buildShortUrl();
	}

	public List<AbstractAttribute> getAttributesForForm() {
		return attributesForForm;
	}
	
	@NotEmpty
	@Pattern(regexp="^[a-z0-9-]*$", message="{error.bad.urlForForm}")
	public String getUrlForForm() {
		return urlForForm;
	}	
	
	public void setAttributesForForm(List<AbstractAttribute> attributesForForm) {
		this.attributesForForm = attributesForForm;
	}
	public void setUrlForForm(String urlForForm) {
		this.urlForForm = urlForForm;
	}
	
	private void buildShortUrl() {
		if (getUrl() != null && !"".equals(getUrl())) {
			String[] splitedUrl = getUrl().split("/"); 
			urlForForm = splitedUrl[splitedUrl.length-1];
		}	
	}
	
	
}
