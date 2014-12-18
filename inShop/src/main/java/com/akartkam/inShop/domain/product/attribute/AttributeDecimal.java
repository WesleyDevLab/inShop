package com.akartkam.inShop.domain.product.attribute;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Attribute_Decimal")
public class AttributeDecimal extends AbstractAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5596242771906778351L;
	private List<AttributeDecimalValue> attributeValues;
	
	@Override
	@Transient
	public AttribueType getAttribueType() {
		return AttribueType.DECIMAL;
	}

	//@Override
	@OneToMany(mappedBy="attributeDecimal", targetEntity=AttributeDecimalValue.class)
	public List<AttributeDecimalValue> getAttributeValues() {
		return attributeValues;
	}
	

}
