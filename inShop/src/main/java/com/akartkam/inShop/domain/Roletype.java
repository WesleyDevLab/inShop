package com.akartkam.inShop.domain;

public enum Roletype {
	
	ADMIN("ADMIN"),
	USER("USER"), 
    MANAGER("MANAGER"),
    TEST("TEST");
    
    
    public static final Roletype[] ALL = { ADMIN, USER, MANAGER, TEST};
    
    
    private final String name;

    
    public static Roletype forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("ADMIN")) {
            return ADMIN;
        } else if (name.toUpperCase().equals("USER")) {
            return USER;
        } else if (name.toUpperCase().equals("MANAGER")) {
            return MANAGER;
	    } else if (name.toUpperCase().equals("TEST")) {
	        return TEST;
	    }
        throw new IllegalArgumentException("Name \"" + name + "\" does not correspond to any Roletype");
    }
    
    
    private Roletype(final String name) {
        this.name = name;
    }
    
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
