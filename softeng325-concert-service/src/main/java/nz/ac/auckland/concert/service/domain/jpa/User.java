package nz.ac.auckland.concert.service.domain.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Entity(name="Users")
public class User {

	@Id
	@Column(name="UserName")
	private String _username;
	
	@Column(name="Password")
	private String _password;
	
	@Column(name="FirstName")
	private String _firstname;
	
	@Column(name="LastName")
	private String _lastname;
	
	@Column(name="Cookie")
	private String _cookieValue;
	
	@OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
	private CreditCard _creditCard;
	
	public User() {}
	
	public User(String username, String password, String lastname, String firstname) {
		_username = username;
		_password = password;
		_lastname = lastname;
		_firstname = firstname;
		_creditCard = null;
	}
	
	public User(String username, String password) {
		this(username, password, null, null);
	}
	
	public String getUsername() {
		return _username;
	}
	
	public String getPassword() {
		return _password;
	}
	
	public String getFirstname() {
		return _firstname;
	}
	
	public String getLastname() {
		return _lastname;
	}
	
	public CreditCard getCreditCard(){
		return _creditCard;
	}
	
	public void setCreditCard(CreditCard creditCard){
		_creditCard = creditCard;
	}
	
	public void setCookieValue(String newValue){
		_cookieValue = newValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return new EqualsBuilder().
            append(_username, rhs._username).
            append(_password, rhs._password).
            append(_firstname, rhs._firstname).
            append(_lastname, rhs._lastname).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_username).
	            append(_password).
	            append(_firstname).
	            append(_password).
	            toHashCode();
	}
}

