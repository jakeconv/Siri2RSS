package net.mtabuscis.siri2rss.model;

import java.util.Date;

public class SiriPayload {

	// This serves as the model for storing the important information from SIRI.
	// From here, the important information can be used to translate into RSS as
	// well as Twitter.

	// Begin modifying for my RSS feed.
	private String title;
	private String url;
	private String summary;
	private Date createdDate;

	// Variables for additional SIRI information.
	// String description; This automagically assings the summary to the
	// description. There is no
	// additional slot for anything as per the RSS specification.
	private String comments;
	private String source;
	private String category;

	// Add an area for the incident ID from Siri.
	private String identification;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}
	
}