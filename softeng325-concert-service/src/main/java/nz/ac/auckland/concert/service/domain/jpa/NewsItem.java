package nz.ac.auckland.concert.service.domain.jpa;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;

/**
 * NewsItem to represent news items. A news item typically reports that a
 * concert with particular performers is coming to town, that ticket sales for
 * a concert are open, that a concert has additional dates etc.
 * 
 * A NewsItem describes a new items in terms of:
 * _id        the unique identifier for the news item.
 * _timestamp the date and time that the news item was released.
 * _content   the news item context text.   
 *
 */
@Entity(name = "NewsItem")
public class NewsItem {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long _id;
	
	@Column(name="Timestamp")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _timestamp;
	
	@Column(name="Content")
	private String _content;
	
	public NewsItem() {}
	
	public NewsItem(LocalDateTime timestamp, String content) {
		_timestamp = timestamp;
		_content = content;
	}
	
	public Long getId() {
		return _id;
	}
	
	public LocalDateTime getTimetamp() {
		return _timestamp;
	}
	
	public String getContent() {
		return _content;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NewsItem))
            return false;
        if (obj == this)
            return true;

        NewsItem rhs = (NewsItem) obj;
        return new EqualsBuilder().
            append(_timestamp, rhs._timestamp).
            append(_content, rhs._content).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_timestamp).
	            append(_content).
	            toHashCode();
	}
}
