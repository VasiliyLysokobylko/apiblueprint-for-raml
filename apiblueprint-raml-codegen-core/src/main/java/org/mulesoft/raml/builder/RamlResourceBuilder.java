package org.mulesoft.raml.builder;

import java.util.LinkedHashMap;
import java.util.List;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;

import com.mulesoft.apib2raml.builder.Utils;
import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.ResourceModel;

public class RamlResourceBuilder {
    
    private RamlActionBuilder actionBuilder = new RamlActionBuilder();
    
    private RamlParameterBuilder paramBuilder = new RamlParameterBuilder();
    
    public Resource buildResource(ResourceModel resourceModel){
        
        Resource ramlResource = new Resource();
        
//        Utils.setDocumentation(resourceModel, ramlResource);
        
        String path = resourceModel.getPath();
        ramlResource.setRelativeUri(path);
        
        List<MethodModel> methodsList  = resourceModel.getMethods();
        if (resourceModel.getType() != null)
        	methodsList.addAll(resourceModel.getType().getMethods());
        for(MethodModel method : methodsList){
            Action action = actionBuilder.buildRamlAction(method);
            if (action.getType() != null)
            	ramlResource.getActions().put(action.getType(), action);
        }
        
        for (MethodModel methodModel : methodsList){
        	String actionName = methodModel.getName();
        	if (!Utils.isEmptyString(actionName)){
        		actionName = actionName.toUpperCase();
        		ActionType actionType = ActionType.valueOf(actionName);
        		Action action = ramlResource.getAction(actionType);
        		if (action != null){
        			for (ParameterModel paramModel : methodModel.getQueryParams()){
        				QueryParameter qParam = paramBuilder.buildQueryParameter(paramModel);
        				if (!Utils.isEmptyString(paramModel.getName()))
        					action.getQueryParameters().put(paramModel.getName(), qParam);
        			}
        		}
        	}
        }
        
        List<ParameterModel> queryParams = resourceModel.getQueryParams();
        
        if (resourceModel.getType() != null)
        	queryParams.addAll(resourceModel.getType().getQueryParams());
        
        for(ParameterModel paramModel : queryParams){
            String name = paramModel.getName();
            QueryParameter qParam = paramBuilder.buildQueryParameter(paramModel);
            for(Action action : ramlResource.getActions().values()){
            	if (action.getQueryParameters().get(name) == null)
            		if(!Utils.isEmptyString(name))
            			action.getQueryParameters().put(name, qParam);
            }
        }
        
        List<ParameterModel> headers = resourceModel.getHeaders();
        if (resourceModel.getType() != null)
        	headers.addAll(resourceModel.getType().getHeaders());
        for(ParameterModel paramModel: headers){
            String name = paramModel.getName();
            Header header = paramBuilder.buildHeader(paramModel);
            for(Action action : ramlResource.getActions().values()){
                action.getHeaders().put(name, header);
            }
        }
        
        LinkedHashMap<String, ResourceModel> resources = resourceModel.getResources();
        if (resourceModel.getType() != null)
        	resources.putAll(resourceModel.getType().getResources());
        for(ResourceModel resource: resources.values()){
            Resource res = buildResource(resource);
            res.setParentUri(path);
            String path0 = res.getRelativeUri();
            ramlResource.getResources().put(path0, res);
        }
        
        return ramlResource;
        
    }
}
