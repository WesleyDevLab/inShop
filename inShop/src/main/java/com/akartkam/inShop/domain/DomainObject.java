package com.akartkam.inShop.domain;

import java.io.Serializable;

import org.joda.time.DateTime;


public interface DomainObject <ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);

    Integer getVersion();
    void setVersion(Integer version);
    
    boolean isEnabled();
    void setEnabled(boolean enabled);
    
    DateTime getCreatedDate();
    void setCreatedDate(DateTime createdDate);
    
    DateTime getUpdatedDate();
    void setUpdatedDate(DateTime updatedDate);
    
    Account getCreatedBy();
    void setCreatedBy(Account createdBy);
    
    Account getUpdatedBy();
    void setUpdatedBy(Account updatedBy);
    
    boolean isNew();
    
    boolean canRemove();
}
