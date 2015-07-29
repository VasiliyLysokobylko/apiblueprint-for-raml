package com.mulesoft.apib2raml.builder;

import java.util.HashMap;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;

import com.mulesoft.apib2raml.model.ParameterModel;

public class ParameterBuilder extends AbstractBuilder<ParameterModel> {
    
    public ParameterBuilder(Class<ParameterModel> modelClass) {
		super(modelClass);
	}

	private static final HashMap<String,String> typeMap = new HashMap<String, String>();
    {
        typeMap.put("int", "integer");
        typeMap.put("short", "number");
        typeMap.put("double", "number");
        typeMap.put("float", "number");
        typeMap.put("real", "number");        
        typeMap.put("bool", "boolean");
    }

    public void fillModel(ParameterModel param, Node node) throws Exception{
        
    	String rawHeader = Utils.getTextContent(node);
        
    	int ind = rawHeader.indexOf(':');
        if(ind>=0){
        	String headerName = rawHeader.substring(0, ind);
        	String headerValue = rawHeader.substring(headerName.length() + ":".length()).trim();
        	param.setName(headerName.trim());
        	if (!Utils.isEmptyString(headerValue))
        		param.setExample(headerValue);
        }
//        extractDocumentation(element, param);
//        
//        String id = element.getAttribute("id");
//        param.setId(id);
//        
//        String name = element.getAttribute("name");
//        param.setName(name);
//        
//        String style = element.getAttribute("style");
//        param.setStyle(style);
//        
//        String defaultValue = element.getAttribute("default");
//        param.setDefaultValue(defaultValue);
//        
//        String path = element.getAttribute("path");
//        param.setPath(path);
//        
//        String fixedValue = element.getAttribute("fixed");
//        param.setFixedValue(fixedValue);
//        
//        String type = element.getAttribute("type");
//        String refinedType = refineType(type);
//        param.setType(refinedType);
//        
//        String requiredString = element.getAttribute("required");
//        boolean isRequired = Boolean.parseBoolean(requiredString);
//        param.setRequired(isRequired);
//        
//        String repeatingString = element.getAttribute("repeating");
//        boolean isRepeating = Boolean.parseBoolean(repeatingString);
//        param.setRepeating(isRepeating);
//        
//        List<Element> optionElements = Utils.extractElements(element, "option");
//        for(Element optionElement: optionElements){
//            String optionValue = optionElement.getAttribute("value");
//            param.addOption(optionValue);
//        }
    }
}
