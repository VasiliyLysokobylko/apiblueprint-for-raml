package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseModel extends HasRepresentation {

    private String status;

    private String schema;
    
    private String mediaType;

    private String example;
    
    private List<ParameterModel> headers = new ArrayList<ParameterModel>();
    
    public List<ParameterModel> getHeaders() {
        return headers;
    }

    public void addHeader(ParameterModel header) {
        this.headers.add(header);
    }
    
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
}
