package com.mulesoft.apib2raml.model;

import java.util.Map;


public class ApplicationModel extends ResourceOwner{
    
	private Map <String, String> includedSchemas;
	
    private String title;
    
    private String baseUri;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

	public Map<String, String> getIncludedSchemas() {
		return includedSchemas;
	}

	public void setIncludedSchemas(Map<String, String> includedSchemas) {
		this.includedSchemas = includedSchemas;
	}
    
}
