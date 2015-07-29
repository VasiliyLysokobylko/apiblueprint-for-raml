package com.mulesoft.apib2raml.builder;


import java.util.HashMap;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;

import com.mulesoft.apib2raml.model.ParameterModel;

public class URIParameterBuilder extends AbstractBuilder<ParameterModel> {
    
    public URIParameterBuilder(Class<ParameterModel> modelClass) {
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
    	//+ <parameter name>: `<example value>` (<type> | enum[<type>], required | optional) - <description>
    	//+ <parameter name> (<type> | enum[<type>], required | optional) - <description>
    	//+ <parameter name>: `<example value>` (<type> | enum[<type>]) - <description>
        
    	String rawParam = Utils.getTextContent(node);
    	int indStartExample = rawParam.indexOf(':');
    	int indEndExample = rawParam.indexOf('(');
    	int indEndOptions = rawParam.indexOf(')');
    	int indBeginDescription = rawParam.indexOf('-');
    	int indDefault = rawParam.toLowerCase().indexOf("default:");
    	int indMembers = rawParam.toLowerCase().indexOf("members");

    	String name = "";
    	String example = "";
    	String type = "";
    	boolean required = false;
    	String description = "";
    	String defaultValue = "";
    	
    	if (indStartExample > 0 && (indStartExample < indEndExample)){
    		name = rawParam.substring(0,indStartExample).trim();
    	}else if (indEndExample > 0){
    		name = rawParam.substring(0,indEndExample).trim();
    	}
    	
    	if (indStartExample > 0 & indEndExample > 0 && (indEndExample > indStartExample)){
    		example = rawParam.substring(indStartExample + ":".length(),indEndExample).trim();
    	}
    	if (indEndExample > 0 & indEndOptions > 0 && (indEndOptions > indEndExample)){
    		String rawOptions = rawParam.substring(indEndExample + "(".length(), indEndOptions);
    		int indCom = rawOptions.indexOf(",");
    		type = rawOptions;
    		if (indCom > 0){
    			type = rawOptions.substring(0, indCom).trim();
    	        String mapped = typeMap.get(type);
    	        if(mapped!=null){
    	            type= mapped;
    	        }
    		}
    		required = rawOptions.contains("required"); 
    	}
    	if (indBeginDescription > 0){
    		int endDescription = rawParam.length();
    		if (indDefault > 0 & indMembers > 0)
    			endDescription = indDefault>indMembers?indDefault:indMembers;
    			else if (indDefault > 0 & indMembers < 0)
    				endDescription = indDefault;
    			else if (indDefault < 0 & indMembers > 0)
    				endDescription = indMembers;
    		if (indBeginDescription < endDescription)
    			description = rawParam.substring(indBeginDescription + "-".length(), endDescription).trim();
    	}
    	
    	if (indDefault > 0){
    		int endDefault = rawParam.length();
    		if (indMembers > 0 & indMembers > indDefault)
    			endDefault = indMembers;
    		defaultValue = rawParam.substring(indDefault + "default:".length(), endDefault);
    	}
    	
    	param.setName(name);
    	param.setExample(example);
    	param.setType(type);
    	param.setRequired(required);
    	param.setDescription(description);
    	if (!Utils.isEmptyString(defaultValue))
    		param.setDefaultValue(defaultValue);
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
//    }
}
