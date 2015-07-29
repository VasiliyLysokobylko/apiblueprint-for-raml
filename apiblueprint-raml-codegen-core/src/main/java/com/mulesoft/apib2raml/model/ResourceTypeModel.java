package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class ResourceTypeModel extends ResourceOwner {

    private String path;
    
    private String parenUri;
    
    private List<MethodModel> methods = new ArrayList<MethodModel>();
    
    private List<ParameterModel> queryParams = new ArrayList<ParameterModel>();
    
    private List<ParameterModel> headers = new ArrayList<ParameterModel>();

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public void addMethod(MethodModel method) {        
        methods.add(method);
    }

    public List<MethodModel> getMethods() {
        return methods;
    }
    
    public void setMethods(List<MethodModel> methods){
    	this.methods = methods;
    }
    
    @Override
    public String toString() {
        return path;
    }

    public String getParenUri() {
        return parenUri;
    }

    public void setParenUri(String parenUri) {
        this.parenUri = parenUri;
    }

}
