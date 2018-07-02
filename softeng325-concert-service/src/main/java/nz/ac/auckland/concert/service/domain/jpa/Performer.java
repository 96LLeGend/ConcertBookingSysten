package nz.ac.auckland.concert.service.domain.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import nz.ac.auckland.concert.common.types.Genre;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

/**
 * Domain model to represent performers. 
 * 
 * A Performer describes a performer in terms of:
 * _id         the unique identifier for a performer.
 * _name       the performer's name.
 * _imageName  the name of an image file for the performer.
 * _genre      the performer's genre.
 * _concertIds identification of each concert in which the performer is 
 *             playing. 
 *             
 */

@Entity(name = "Performers")
public class Performer {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long _id;
	
	@Column(name="Name")
	private String _name;
	
	@Column(name="ImageName")
	private String _imageName;
	
	@Enumerated(EnumType.STRING)
	@Column(name="Genre")
	private Genre _genre;
	
	@ManyToMany(mappedBy = "_performerIds")
	private Set<Concert> _concertIds;
	
	public Performer(){}
	
	public Performer(Long id, String name, String imageName, Genre genre, Set<Concert> concertIds) {
		_id = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concertIds = new HashSet<Concert>(concertIds);
	}
	
	public Long getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getImageName() {
		return _imageName;
	}
	
	public Genre getGenre(){
		return _genre;
	}
	
	public Set<Long> getConcertIds() {
		
		Set<Long> idSet = new HashSet();
		for (Concert currentConcert : _concertIds) {
		        idSet.add(currentConcert.getId());
		     }
			
		return idSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Performer))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
            append(_name, rhs._name).
            append(_imageName, rhs._imageName).
            append(_genre, rhs._genre).
            append(_concertIds, rhs._concertIds).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            toHashCode();
	}
}

