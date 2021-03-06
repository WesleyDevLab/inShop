package com.akartkam.inShop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@NamedQueries({
@NamedQuery(name = "findRoleByName", query = "from Role where name= :name"),
@NamedQuery(name = "findRoleByRoletype", query = "from Role where role= :role")})
@Entity
public class Role extends AbstractDomainObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6220380899227195583L;
	
	private RoleType role;
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name="role", unique=true)
	@NotNull
	public RoleType getRole() {
		return role;
	}
	public void setRole(RoleType role) {
		this.role = role;
	}
	
	@Column(name="name", unique=true)
	@NotNull
	@Size(min = 1, max = 50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
