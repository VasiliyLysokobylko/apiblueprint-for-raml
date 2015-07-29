package com.mulesoft.apib2raml.builder;

import java.sql.Savepoint;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.RootNode;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.RepresentationModel;
import com.mulesoft.apib2raml.model.RequestModel;

public class RequestBuilder extends AbstractBuilder<RequestModel> {
    
    public RequestBuilder(Class<RequestModel> modelClass) {
		super(modelClass);
	}

	public void fillModel(RequestModel requestModel, Node node) throws Exception{
		Node nestedNode = node;
		if (nestedNode.getChildren().size() == 1 && nestedNode.getChildren().get(0).getChildren().size() > 1){
			nestedNode = new RootNode();
			nestedNode.getChildren().addAll(node.getChildren().get(0).getChildren());
		}
		for (int i = 0; i < nestedNode.getChildren().size(); i++){
			Node childNode = nestedNode.getChildren().get(i);
			String childNodeContent = Utils.getTextContent(childNode);
			if (requestModel.getMediaType() == null && childNodeContent.contains("(") & childNodeContent.contains(")")){
				String mediaType = childNodeContent.substring(
						childNodeContent.indexOf("(") + "(".length(),
						childNodeContent.indexOf(")"));  
				requestModel.setMediaType(mediaType);
				if (i < nestedNode.getChildren().size() - 1){
					Node nextChildNode = nestedNode.getChildren().get(i + 1);
					String nextChilNodeContent = Utils.getTextContent(nextChildNode);
					String example = Utils.getCleanExample(nextChilNodeContent);
					if (!Utils.isNextPayloadSection(example) && !Utils.isNextActionSection(example)){
						String path = getPathResolver().getOutputRootPath();
						String resourceName = getBuildManager().getExampleName();
						String fileName = Utils.saveExample(requestModel.getMediaType(), path, resourceName, example);
						requestModel.setExample(" !include examples/" + fileName);
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
				String fileName = Utils.saveExample(requestModel.getMediaType(), path, resourceName, example);
				requestModel.setExample(" !include examples/" + fileName);
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("headers")){
				ParameterBuilder parameterBuilder = getBuildManager().getBuilder(ParameterBuilder.class);
				Node headerSection = Utils.getHeaderSection(nestedNode, childNode, i);
				Node headerParameterNodes = Utils.extractHeaderParameters(headerSection);
				for (int k = 0; k < headerParameterNodes.getChildren().size(); k++){
					ParameterModel headerParameter = parameterBuilder.build((AbstractNode) headerParameterNodes.getChildren().get(k));
					requestModel.addHeader(headerParameter);
				}
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("schema")){
				if (i < nestedNode.getChildren().size() - 1){
					String schema = Utils.getTextContent(nestedNode.getChildren().get(i + 1));
					String schemaName = getBuildManager().registerGlobalSchema(schema);
					requestModel.setSchema(schemaName);
				}
			}
			else if(childNodeContent.trim().toLowerCase().startsWith("attributes")){}//TODO implement attirbutes section.  
		}
//        extractDocumentation(element, requestModel);
//        
//        ParameterBuilder paramBuilder = getBuildManager().getBuilder(ParameterBuilder.class);
//        List<Element> paramElements = Utils.extractElements(element, "param");
//        for(Element paramElement : paramElements){
//			ParameterModel param = paramBuilder.build(paramElement);
//            String style = param.getStyle();
//            if("query".equals(style)){
//                requestModel.addQueryParam(param);
//            }
//            else if("header".equals(style)){
//                requestModel.addHeader(param);
//            }
//        }
//        RepresentationBuilder representationBuilder = getBuildManager().getBuilder(RepresentationBuilder.class);
//        List<Element> representationElements = Utils.extractElements(element, "representation");
//        for(Element representationElement : representationElements){
//			RepresentationModel representation = representationBuilder.build(representationElement);
//            requestModel.addRepresentation(representation);
//        }
    }
    private RootNode getSection(Node node, Node childNode, int currentPosition) {
    	RootNode sectionNodes = new RootNode();
		sectionNodes.getChildren().add(childNode);
		int j = currentPosition + 1;
		while (j<node.getChildren().size()){
			Node sectionNode = node.getChildren().get(j);
			String sectionNodeContent = Utils.getTextContent(sectionNode);
			if (Utils.isNextPayloadSection(sectionNodeContent))
				break;
			else
				sectionNodes.getChildren().add(sectionNode);
			j++;
		}
    	return sectionNodes;
	}
}
