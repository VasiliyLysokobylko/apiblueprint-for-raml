package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class RequestModel extends HasRepresentation {
    
    private List<ParameterModel> queryParams = new ArrayList<ParameterModel>();
    
    private List<ParameterModel> headers = new ArrayList<ParameterModel>();
    
    private String schema;
    
    private String example;
    
    private String mediaType;

    public List<ParameterModel> getQueryParams() {
        return queryParams;
    }

    public void addQueryParam(ParameterModel queryParam) {
        this.queryParams.add(queryParam);
    }

    public List<ParameterModel> getHeaders() {
        return headers;
    }

    public void addHeader(ParameterModel header) {
        this.headers.add(header);
    }

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
