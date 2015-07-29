package com.mulesoft.apib2raml.builder;

import java.util.ArrayList;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.RootNode;

import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.RequestModel;
import com.mulesoft.apib2raml.model.ResponseModel;

public class MethodBuilder extends AbstractBuilder<MethodModel> {
    
    public MethodBuilder(Class<MethodModel> modelClass) {
		super(modelClass);
	}
    
    public void fillModel(MethodModel method, Node node) throws Exception{
    	boolean hasRelation = false;
        for (int i = 0; i < node.getChildren().size(); i++){
        	Node childNode = node.getChildren().get(i);
        	String childNodeContent = Utils.getTextContent(childNode);
        	if ((childNode instanceof HeaderNode)){
        		String methodName = getMethodName(childNodeContent);
        		if (methodName != null)
        			method.setName(methodName);
        		if(i < node.getChildren().size() - 1){
        			Node nextNode = node.getChildren().get(i+1);
        			if (nextNode instanceof ParaNode && method.getDescription() == null 
        					&& !Utils.getTextContent(childNode).trim().toLowerCase().startsWith("group")){
        				String description = Utils.getTextContent(nextNode);
        				method.setDescription(description);
        			}
        		}
        	}else if(childNode instanceof BulletListNode){
        		if(!hasRelation){
        			Node relationNodes = Utils.extractListElements(childNode, "relation");
        			if (relationNodes.getChildren().size() > 0)
        				hasRelation = true;
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
        		if(parametersNode.getChildren().size() > 0){
        			URIParameterBuilder uriParameterBuilder = getBuildManager().getBuilder(URIParameterBuilder.class);
        			for (int k = 0; k < parametersNode.getChildren().size(); k++){
        				if (!Utils.isNextActionSection(Utils.getTextContent(parametersNode.getChildren().get(k)))){
        					ParameterModel uriParameter = uriParameterBuilder.build((AbstractNode) parametersNode.getChildren().get(k));
        					method.addQueryParam(uriParameter);
        				}
        			}
        		}

        		Node requestNodes = Utils.extractListElements(childNode, "request");
        		if(requestNodes.getChildren().size() > 0){
        			List<RequestModel> requestModels = getRequestModels(node, requestNodes, i);
        			for (RequestModel requestModel : requestModels)
        				method.addRequest(requestModel);
        		}

        		Node responseNodes = Utils.extractListElements(childNode, "response");
        		if(responseNodes.getChildren().size() > 0){
        			List<ResponseModel> responseModels = getReponseModels(node, responseNodes, i);
        			for(ResponseModel responseModel : responseModels){
        				if (hasRelation)
        					addLinkHeader(responseModel);
        				
        				method.addResponse(responseModel);
        			}
        		}

        	}
        }
    }
    
    private void addLinkHeader(ResponseModel responseModel) {
    	ParameterModel linkHeader = new ParameterModel();
    	linkHeader.setName("Link");
    	if (responseModel.getStatus().startsWith("2"))
    		responseModel.addHeader(linkHeader);
	}

	private String getRelation(Node relationNodes) {
    	String relation = "";
    	String rawRelation = Utils.getTextContent(relationNodes);
    	int ind = rawRelation.indexOf(":");
    	if(ind > 0){
    		relation = rawRelation.substring(ind + ":".length()).trim();
    	}
    	return relation;
	}

	private List<RequestModel> getRequestModels(Node node, Node childNode, int currentPosition) throws Exception {
		List<RequestModel> requestModels = new ArrayList<RequestModel>();
		RequestBuilder builder = getBuildManager().getBuilder(RequestBuilder.class);
		
    	Node sectionNodes = getChildNodes(childNode);
    	if (sectionNodes != null){
    		for (int i = 0; i < sectionNodes.getChildren().size(); i++){
    			Node subChildNode = sectionNodes.getChildren().get(i);
    			String childNodeContent = Utils.getTextContent(subChildNode);
    			if(childNodeContent.trim().toLowerCase().startsWith("request")){
    				Node nodes = getSection(sectionNodes, subChildNode, i);
    				RequestModel request = builder.build(nodes);
    				requestModels.add(request);
    			}
    		}
    	}
    	else {
    		RequestModel request = builder.build(
					getSection(node, childNode, currentPosition));
    		requestModels.add(request);
    	}
    	return requestModels;
	}

    private List<ResponseModel> getReponseModels(Node node, Node childNode, int currentPosition) throws Exception {
		List<ResponseModel> responseModels = new ArrayList<ResponseModel>();
		ResponseBuilder builder = getBuildManager().getBuilder(ResponseBuilder.class);
		
    	Node sectionNodes = getChildNodes(childNode);
    	if (sectionNodes != null){
    		for (int i = 0; i < sectionNodes.getChildren().size(); i++){
    			Node subChildNode = sectionNodes.getChildren().get(i);
    			String childNodeContent = Utils.getTextContent(subChildNode);
    			if(childNodeContent.trim().toLowerCase().startsWith("response")){
    				Node nodes = getSection(sectionNodes, subChildNode, i);
    				ResponseModel response = builder.build(nodes);
    				responseModels.add(response);
    			}
    		}
    	}
    	else {
    		ResponseModel response = builder.build(
					getSection(node, childNode, currentPosition));
    		responseModels.add(response);
    	}
    	return responseModels;
	}

	private Node getChildNodes(Node childNode) {
			Node rootNode;
			if (childNode.getChildren().size() > 1){
				return childNode;
			}
			for (int i = 0; i < childNode.getChildren().size(); i++){
				rootNode = getChildNodes(childNode.getChildren().get(i));	
				if(rootNode != null)
					return rootNode;
			}
		return null;
	}

	private Node getSection(Node node, Node childNode, int currentPosition) {
    	Node sectionNodes;
    	if (node instanceof ListItemNode){
    		sectionNodes = new ListItemNode(childNode);
    	}else if(node instanceof BulletListNode){
    		sectionNodes = new BulletListNode(childNode);
    	}else{
    		sectionNodes = new RootNode();
    		sectionNodes.getChildren().add(childNode);
    	}
		int j = currentPosition + 1;
		while (j<node.getChildren().size()){
			Node sectionNode = node.getChildren().get(j);
			String sectionNodeContent = Utils.getTextContent(sectionNode);
			if (Utils.isNextActionSection(sectionNodeContent))
				break;
			else
				sectionNodes.getChildren().add(sectionNode);
			j++;
		}
    	return sectionNodes;
	}
    
	private String getMethodName(String content) {
		String methodName = null;
		if (content.contains("["))
			content = content.substring(content.indexOf("[") + "[".length());
		content = content.toUpperCase();
		if (content.startsWith("OPTIONS") || content.startsWith("GET")
				|| content.startsWith("HEAD") || content.startsWith("POST")
				|| content.startsWith("PUT") || content.startsWith("PATCH")
				|| content.startsWith("DELETE") || content.startsWith("TRACE")
				|| content.startsWith("CONNECT")){
			int whitespaceIndex = content.length();
			int closeBracketIndex = content.length();
			if (content.contains(" "))
				whitespaceIndex = content.indexOf(" ");
			if (content.contains("]"))
				closeBracketIndex = content.indexOf("]");
			int endIndex = closeBracketIndex>=whitespaceIndex?whitespaceIndex:closeBracketIndex;
			methodName =  content.substring(0,endIndex).trim();
		}
		return methodName;
	}
}
