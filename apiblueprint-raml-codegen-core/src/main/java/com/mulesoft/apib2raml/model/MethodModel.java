package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MethodModel extends AbstractElement {
    
    private String name;
    
    private List<ParameterModel> queryParams = new ArrayList<ParameterModel>();
    
    private List<String> relation = new ArrayList<String>();
    
    private Map<String,List<ResponseModel>> responses = new LinkedHashMap<String, List<ResponseModel>>();
    
    private List<RequestModel> requests = new ArrayList<RequestModel>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addRequest(RequestModel request){
    	request.getQueryParams().addAll(this.queryParams);
        this.requests.add(request);
    }
    
    public void addResponse(ResponseModel response){
        String status = response.getStatus();
        List<ResponseModel> list = this.responses.get(status);
        if(list==null){
            list = new ArrayList<ResponseModel>();
            this.responses.put(status, list);
        }
        list.add(response);
    }
    

    public Map<String, List<ResponseModel>> getResponses() {
        return responses;
    }

    public List<RequestModel> getRequests() {
        return requests;
    }

	public List<String> getRelation() {
		return relation;
	}

	public void addRelation (String relation){
		this.relation.add(relation);
	}

	public List<ParameterModel> getQueryParams() {
		return queryParams;
	}

	public void addQueryParam(ParameterModel queryParam) {
		this.queryParams.add(queryParam);
	}
}
