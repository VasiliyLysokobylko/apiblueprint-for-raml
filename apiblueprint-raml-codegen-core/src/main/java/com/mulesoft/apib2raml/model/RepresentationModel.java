package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class RepresentationModel extends AbstractElement{
    //TODO delete if unnecessary.
    private String mediaType;

    private String schema;
    
    private String example;
    
    private List<ParameterModel> formParameters = new ArrayList<ParameterModel>();

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public List<ParameterModel> getFormParameters() {
        return formParameters;
    }

    public void addFormParameter(ParameterModel formParameter) {
        this.formParameters.add(formParameter);
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
}
