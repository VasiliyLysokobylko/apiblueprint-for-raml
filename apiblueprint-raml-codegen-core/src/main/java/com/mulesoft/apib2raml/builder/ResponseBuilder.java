package com.mulesoft.apib2raml.builder;

import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.RootNode;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.ResponseModel;

public class ResponseBuilder extends AbstractBuilder<ResponseModel> {
    
    public ResponseBuilder(Class<ResponseModel> modelClass) {
		super(modelClass);
	}

	public void fillModel(ResponseModel responseModel, Node node) throws Exception{
		Node nestedNode = node;
		if (nestedNode.getChildren().size() == 1 && nestedNode.getChildren().get(0).getChildren().size() > 1){
			nestedNode = new RootNode();
			nestedNode.getChildren().addAll(node.getChildren().get(0).getChildren());
		}
		for (int i = 0; i < nestedNode.getChildren().size(); i++){
			Node childNode = nestedNode.getChildren().get(i);
			String childNodeContent = Utils.getTextContent(childNode).trim();
			if (responseModel.getMediaType() == null & childNodeContent.toLowerCase().startsWith("response")){
				String status = getStatus(childNodeContent);
				responseModel.setStatus(status);				
			}
			if (responseModel.getMediaType() == null && childNodeContent.contains("(") & childNodeContent.contains(")")){
				String mediaType = childNodeContent.substring(
						childNodeContent.indexOf("(") + "(".length(),
						childNodeContent.indexOf(")"));  
				responseModel.setMediaType(mediaType);
				if (i < nestedNode.getChildren().size() - 1){
					Node nextChildNode = nestedNode.getChildren().get(i + 1);
					String nextChilNodeContent = Utils.getTextContent(nextChildNode);
					String example = Utils.getCleanExample(nextChilNodeContent);
					if (!Utils.isNextPayloadSection(example) && !Utils.isNextActionSection(example)){
						String path = getPathResolver().getOutputRootPath();
						String resourceName = getBuildManager().getExampleName();
						String fileName = Utils.saveExample(responseModel.getMediaType(), path, resourceName, example);
						responseModel.setExample(" !include examples/" + fileName);
					}
				}
			}

			if(childNodeContent.trim().toLowerCase().startsWith("body")){
				String example = Utils.getCleanExample(childNodeContent);
				if (example.toLowerCase().startsWith("body")){
					if (i < nestedNode.getChildren().size()-1){
						Node bodyNode = nestedNode.getChildren().get(i+1);
						example = Utils.getCleanExample(Utils.getTextContent(bodyNode));
					}
				}
				String path = getPathResolver().getOutputRootPath();
				String resourceName = getBuildManager().getExampleName();
				String fileName = Utils.saveExample(responseModel.getMediaType(), path, resourceName, example);
				responseModel.setExample(" !include examples/" + fileName);
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("headers")){
				ParameterBuilder parameterBuilder = getBuildManager().getBuilder(ParameterBuilder.class);
				Node headerSection = Utils.getHeaderSection(nestedNode, childNode, i);
				Node headerParameterNodes = Utils.extractHeaderParameters(headerSection);
				for (int k = 0; k < headerParameterNodes.getChildren().size(); k++){
					ParameterModel headerParameter = parameterBuilder.build((AbstractNode) headerParameterNodes.getChildren().get(k));
					responseModel.addHeader(headerParameter);
				}
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("schema")){
				if (i < nestedNode.getChildren().size() - 1){
					String schema = Utils.getTextContent(nestedNode.getChildren().get(i + 1));
					String schemaName = getBuildManager().registerGlobalSchema(schema);
					responseModel.setSchema(schemaName);
				}
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("attributes")){}//TODO implement attributes section.
		}
//        extractDocumentation(element, responseModel);
//        
//        String status = element.getAttribute("status");
//        responseModel.setStatus(status);
//        
//        List<Element> paramElements = Utils.extractElements(element, "param");
//        ParameterBuilder paramBuilder = getBuildManager().getBuilder(ParameterBuilder.class);
//        for(Element paramElement : paramElements){            
//			ParameterModel param = paramBuilder.build(paramElement);
//            String style = param.getStyle();
//            if("header".equals(style)){
//                responseModel.addHeader(param);
//            }
//        }
//        
//        RepresentationBuilder representationBuilder = getBuildManager().getBuilder(RepresentationBuilder.class);
//        List<Element> representationElements = Utils.extractElements(element, "representation");
//        for(Element representationElement : representationElements){
//			RepresentationModel representation = representationBuilder.build(representationElement);
//            responseModel.addRepresentation(representation);
//        }
    }

	private String getStatus(String childNodeContent) {
		StringBuilder status = new StringBuilder();
		if (childNodeContent.contains(" ")){
			int startInd = childNodeContent.indexOf(" "); 
			StringBuilder rawStatus = new StringBuilder(childNodeContent.substring(startInd).trim());
//			int i = 0;
//			while(Character.isDigit(rawStatus.charAt(i))){
//				i++;
//			}
			for (int i = 0; i < rawStatus.length(); i++){
				if (Character.isDigit(rawStatus.charAt(i)))
					status.append(rawStatus.charAt(i));
				else
					break;
			}
		}
		return status.toString();
	}

}
