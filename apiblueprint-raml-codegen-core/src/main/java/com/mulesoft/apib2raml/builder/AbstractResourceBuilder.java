package com.mulesoft.apib2raml.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.tree.ParameterNode;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.RootNode;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.ResourceModel;
import com.mulesoft.apib2raml.model.ResourceTypeModel;
import com.sun.tools.xjc.generator.bean.MethodWriter;

public class AbstractResourceBuilder<T extends ResourceTypeModel> extends AbstractBuilder<T> {
    
    public AbstractResourceBuilder(Class<T> modelClass) {
		super(modelClass);
	}

	void fillModel(T resourceType,Node node) throws Exception{
		ResourceTypeModel currentResource = resourceType;
		MethodBuilder methodBuilder = getBuildManager().getBuilder(MethodBuilder.class);
		
		RootNode methodNodes = new RootNode();
    	boolean isFirst = true;
    	boolean isMethodSection = false;
		for (int i = 0; i < node.getChildren().size(); i++){
			boolean isLast = i+1 == node.getChildren().size();
			org.pegdown.ast.Node childNode = node.getChildren().get(i);
			String childNodeContent = Utils.getTextContent(childNode);
			if (childNode instanceof HeaderNode && childNodeContent.contains("/")){
				String path = Utils.getPath(childNodeContent);
				int paramIndex = path.indexOf("{?");
				if (paramIndex > 0){//TODO parse parameters.
					path = path.substring(0, paramIndex);
				}
				getBuildManager().setCurrentResource(path);
				currentResource = getResourceTypeModel(resourceType, path);
				String resourseId = getResourceId(childNodeContent);
				currentResource.setId(resourseId);
			}
			
    		Node parameters = Utils.extractListElements(childNode, "parameters");
    		Node parametersNode = parameters;
    		if (parametersNode.getChildren().size() == 1 && parametersNode.getChildren().get(0).getChildren().size() > 1){
    			Node paramItems = Utils.getParamItems(parameters);
    			if (paramItems.getChildren().size() > 1){
    				parametersNode = paramItems;
    			} else{
    				parametersNode = new RootNode();
    				parametersNode.getChildren().addAll(parameters.getChildren().get(0).getChildren());
    			}
    		}
    		if(parametersNode.getChildren().size() > 0 & !isMethodSection){
    			URIParameterBuilder uriParameterBuilder = getBuildManager().getBuilder(URIParameterBuilder.class);
    			for (int k = 0; k < parametersNode.getChildren().size(); k++){
    				if (!Utils.isNextActionSection(Utils.getTextContent(parametersNode.getChildren().get(k)))){
    					ParameterModel uriParameter = uriParameterBuilder.build((AbstractNode) parametersNode.getChildren().get(k));
    					currentResource.addQueryParam(uriParameter);
    				}
    			}
    		}
			
			if ((childNode instanceof HeaderNode || isLast) & !isFirst){
    			if(isLast)
    				methodNodes.getChildren().add(childNode);
    			if(Utils.isMethod(childNodeContent) || isLast){//TODO should check for method instance.
    				MethodModel method = methodBuilder.build(methodNodes);
    				currentResource.addMethod(method);
    				methodNodes = new RootNode();
    				isMethodSection = true;
    			}
    			if (!isLast)
    				methodNodes.getChildren().add(childNode);
			}else{
				if (isFirst && (childNode instanceof HeaderNode)){
					if(Utils.isResource(childNode) || Utils.isMethod(childNodeContent))
						isFirst = false;
				}if(!isFirst)
					methodNodes.getChildren().add(childNode);
			}	
//			}else if ((childNode instanceof ParaNode) & (i >= 1) && (node.getChildren().get(i-1) instanceof HeaderNode)){
//				//TODO implement method/resource description setter.
//			}else if (childNode instanceof BulletListNode){
//				String attribute = (Utils.getTextContent(childNode)).trim();
//				if (attribute.toLowerCase().startsWith("attributes")){
//					//TODO implement resource parameter builder.
//				}else if (attribute.toLowerCase().startsWith("request")){
//					//TODO implement request builder.
//				}else if (attribute.toLowerCase().startsWith("response")){
//					//TODO implement response builder.
//				}
//			}else if (childNode instanceof HeaderNode){
//				//TODO implement child methods builder.
//			} 
		}
        
//        extractDocumentation(element, resourceType);
//
//        String idStr = element.getAttribute("id");
//        if(!idStr.isEmpty()){
//            resourceType.setId(idStr);
//        }        
//        
//        MethodBuilder methodBuilder = getBuildManager().getBuilder(MethodBuilder.class);
//        List<Element> methodElements = Utils.extractElements(element, "method");
//        for(Element methodElement : methodElements){
//			MethodModel method = methodBuilder.build(methodElement);
//            currentResource.addMethod(method);
//        }
//        
//        ResourceBuilder resourceBuilder = getBuildManager().getBuilder(ResourceBuilder.class);
//        
//        List<Element> resourceTypeElements = Utils.extractElements(element,"resource");
//        for(Element resourceTypeElement : resourceTypeElements){
//            
//            ResourceModel res = resourceBuilder.build(resourceTypeElement);
//            currentResource.addResource(res);
//        }
//        
//        ParameterBuilder paramBuilder = getBuildManager().getBuilder(ParameterBuilder.class);
//        List<Element> paramElements = Utils.extractElements(element, "param");
//        for(Element paramElement : paramElements){            
//			ParameterModel param = paramBuilder.build(paramElement);
//            String style = param.getStyle();
//            if("query".equals(style)){
//                currentResource.addQueryParam(param);
//            }
//            else if("header".equals(style)){
//                currentResource.addHeader(param);
//            }
//        }
    }
	
	private String getResourceId(String content) {
			String type = null;
			int bracketIndex = content.indexOf("[");
			if (bracketIndex > 0){
				type = content.substring(0, bracketIndex).trim();
			}
		return type;
	}

	private ResourceTypeModel getResourceTypeModel(ResourceTypeModel resourceType, String path){
		while (path.startsWith("/"))
			path = path.substring("/".length());
		while (path.endsWith("/"))
			path = path.substring(0, path.length() - "/".length());        
		
		ResourceTypeModel currentResource = resourceType;
		List<String> pathSegments = new ArrayList<String>(Arrays.asList(path.split("/")));

		ResourceTypeModel previousResource = null;
		for (int i = 0; i < pathSegments.size() ; i++){
			String segment = "/" + pathSegments.get(i);   
			if (i != 0)
				currentResource = new ResourceModel();
			currentResource.setPath(segment);
			if (i != 0)
				previousResource.addResource((ResourceModel) currentResource);
			previousResource = currentResource;
		}
		return currentResource;
	}
}
