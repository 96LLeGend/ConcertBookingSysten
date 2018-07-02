package nz.ac.auckland.concert.service. domain.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;
import nz.ac.auckland.concert.common.types.PriceBand;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain to represent concerts. 
 * 
 * A Concert describes a concert in terms of:
 * _id           the unique identifier for a concert.
 * _title        the concert's title.
 * _dates        the concert's scheduled dates and times (represented as a 
 *               Set of LocalDateTime instances).
 * _tariff       concert pricing - the cost of a ticket for each price band 
 *               (A, B and C) is set individually for each concert. 
 * _performerIds identification of each performer playing at a concert 
 *               (represented as a set of performer identifiers).
 */


@Entity(name="Concerts")
public class Concert {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long _id;
	
	@Column(name="Title")
	private String _title;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "Concert_Dates")
	@Column(name="Date")
	@Convert(converter = LocalDateTimeConverter.class)
	private Set<LocalDateTime> _dates;
	
	@ElementCollection
	@CollectionTable(name = "Concert_Tarifs")
	@MapKeyColumn(name = "PriceBand")
	@Column(name = "CostPerTicket")
	@MapKeyEnumerated(EnumType.STRING)
	private Map<PriceBand, BigDecimal> _tariff;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(
			name = "Concert_Performer",
			joinColumns = @JoinColumn(name = "Concert_ID"),
			inverseJoinColumns = @JoinColumn(name = "Performer_ID")
	)
	private Set<Performer> _performerIds;
	
	public Concert() {}

	public Concert(Long id, String title, Set<LocalDateTime> dates,
			Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performerIds) {
		_id = id;
		_title = title;
		_dates = new HashSet<LocalDateTime>(dates);
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performerIds = new HashSet<Performer>(performerIds);
	}

	public Long getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public Set<LocalDateTime> getDates() {
		return Collections.unmodifiableSet(_dates);
	}

	public Map<PriceBand, BigDecimal> getTariff() {
		return Collections.unmodifiableMap(_tariff);
	}

	public Set<Long> getPerformerIds() {
		Set<Long> idSet = new HashSet();
		 for (Performer currentPerformer : _performerIds) {
		        idSet.add(currentPerformer.getId());
		     }
		
		return idSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
            append(_title, rhs._title).
            append(_dates, rhs._dates).
            append(_tariff, rhs._tariff).
            append(_performerIds, rhs._performerIds).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            toHashCode();
	}
}
