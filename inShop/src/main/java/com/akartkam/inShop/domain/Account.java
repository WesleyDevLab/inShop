package com.akartkam.inShop.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

@NamedQuery(
		name = "findAccountByUsername",
		query = "from Account where username = :username")
@Entity
@Table(name = "Account")
public class Account extends AbstractDomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4943474187321876921L;
	
	private String username, firstName, lastName, middleName, email;
	private Set<Role> roles = new HashSet<Role>();
	
	
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "username", unique=true)
	public String getUsername() { return username; }

	public void setUsername(String username) { this.username = username; }
	
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "first_name")
	public String getFirstName() { return firstName; }

	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "last_name")
	public String getLastName() { return lastName; }

	public void setLastName(String lastName) { this.lastName = lastName; }
	
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "middle_name")
	public String getMiddleName() { return middleName; }

	public void setMiddleName(String middleName) { this.middleName = middleName; }

	
	
	@Transient
	public String getFullName() { return firstName + " " + middleName + " " + lastName; }
	
	@NotNull
	@Size(min = 6, max = 50)
	@Email
	@Column(name = "email")
	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }
	
	
	@ManyToMany
	@JoinTable(
		name = "Account_role",
		joinColumns = { @JoinColumn(name = "account_id", referencedColumnName = "id") },
		inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	public Set<Role> getRoles() { return roles; }
	
	public void setRoles(Set<Role> roles) { this.roles = roles; }
	

}
