package org.mulesoft.raml.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.Raml2;
import org.raml.model.Resource;

import com.mulesoft.apib2raml.model.ApplicationModel;
import com.mulesoft.apib2raml.model.ResourceModel;
import com.mulesoft.jaxrs.raml.annotation.model.RAMLModelHelper;

public class RamlBuilder {
    
    private RamlResourceBuilder resourceBuilder = new RamlResourceBuilder();
    
    protected RAMLModelHelper spec = new RAMLModelHelper();
    
    public Raml2 buildRaml(ApplicationModel app){
    	
    	Raml2 raml = spec.getCoreRaml();
		raml.setBaseUri(app.getBaseUri());
        raml.setTitle(app.getTitle());
        
        LinkedHashMap<String, ResourceModel> resources = app.getResources();
        List<ResourceModel> resourceList = new ArrayList<ResourceModel>(resources.values());
        Collections.sort(resourceList,new Comparator<ResourceModel>(){

        	public int compare(ResourceModel o1, ResourceModel o2) {				
        		return o1.getPath().compareTo(o2.getPath());
        	}});
        
		for(ResourceModel resource: resourceList){
            Resource ramlResource = resourceBuilder.buildResource(resource);            
            ramlResource.setParentUri("");
            spec.addResource(ramlResource);
        }
        
		Map<String, String>  schemasBodys = app.getIncludedSchemas();
		if (schemasBodys != null){
			for (String schemaName : schemasBodys.keySet()){
				String body = schemasBodys.get(schemaName);
				raml.addGlobalSchema(schemaName, body, false, true);
			}
		}
        spec.optimize();
        return raml;
    }


}
