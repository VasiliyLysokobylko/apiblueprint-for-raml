package com.mulesoft.apib2raml.model;

public class AbstractElement {

    private String id;
	
    private String description;

    public String getDescription() {
        return description;
    }

	public void setDescription(String content) {
		this.description = content;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}