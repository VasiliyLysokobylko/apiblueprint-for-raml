package com.mulesoft.apib2raml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceOwner extends AbstractElement {

    LinkedHashMap<String,ResourceModel> resources = new LinkedHashMap<String,ResourceModel>();

    public ResourceOwner() {
        super();
    }

    public LinkedHashMap<String,ResourceModel> getResources() {
        return this.resources;
    }

    public void addResource(ResourceModel res) {
    	ResourceModel existingResource = this.resources.get(res.getPath());
    	if (existingResource != null){
    		mergeResources(existingResource, res);
    	}
//    	else
    		this.resources.put(res.getPath(), res);
    }

	private void mergeResources(ResourceModel existingResource,
			ResourceModel res) {
		res.getHeaders().addAll(existingResource.getHeaders());
		res.getMethods().addAll(existingResource.getMethods());
		res.getQueryParams().addAll(existingResource.getQueryParams());
		
		for (String key : existingResource.getResources().keySet()){
			if (res.getResources().containsKey(key)){
				mergeResources(existingResource.getResources().get(key), res.getResources().get(key));
			}
			else{
				res.getResources().put(key, existingResource.getResources().get(key));
			}
		}
	}
}