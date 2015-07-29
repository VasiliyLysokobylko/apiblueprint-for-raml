package com.mulesoft.apib2raml.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.raml.model.Action;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.AbstractParam;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mulesoft.apib2raml.model.AbstractElement;
import com.mulesoft.apib2raml.model.ResourceModel;

public class Utils {
    
    public static List<Element> extractElements(Element parent, String tag){
        
        ArrayList<Element> list = new ArrayList<Element>();
        
        NodeList children = parent.getChildNodes();
        int length = children.getLength();
        for(int i = 0 ; i < length ;i++){            
            Node node = children.item(i);            
            if(!(node instanceof Element)){
                continue;
            }
            Element el = (Element) node;
            String tagName = el.getTagName();
            if(!tagName.equals(tag)){
                continue;
            }
            
            list.add(el);
        }        
        return list;        
    }

    public static String refinePath(String path) {
        
        if(!path.startsWith("/")){
            return "/" + path;
        }        
        return path;
    }

    public static void setParentUri(ResourceModel resource, String uri) {
        resource.setParenUri(uri);
        
        String path = resource.getPath();
        LinkedHashMap<String, ResourceModel> resources = resource.getResources();
        for(ResourceModel res : resources.values()){
            setParentUri(res, uri+path);
        }
        
    }
    
    public static void setDocumentation(AbstractElement element, Resource resource) {
//    	if (element.getDoc() == null)
//    		return;
//        String content = element.getDoc().getContent();
//        if(content.trim().isEmpty()){
//            return;
//        }
//        resource.setDescription(content);
    }
    

    public static void setDocumentation(AbstractElement element, Action action) {
//        DocumentationModel doc = element.getDoc();
//        if(doc==null){
//        	return;
//        }
//		String content = doc.getContent();
//        if(content.trim().isEmpty()){
//            return;
//        }
//        action.setDescription(content);
    }

    public static void setDocumentation(AbstractElement element, Response response) {
//        String content = element.getDoc().getContent();
//        if(content.trim().isEmpty()){
//            return;
//        }
//        response.setDescription(content);
    }
    
    public static void setDocumentation(AbstractElement element, AbstractParam param) {
//        String content = element.getDoc().getContent();
//        if(content.trim().isEmpty()){
//            return;
//        }
//        param.setDescription(content);
    }
    
    public static boolean isEmptyString(String str){
        return str == null || str.trim().isEmpty();
    }

	public static String stringToCamel(String rawString){
		rawString = rawString.trim();
		if (rawString.contains(" ") || rawString.contains("-") || rawString.contains("_")){
			String separator = " ";
			if(!rawString.contains(" "))
				separator = rawString.contains("-")?"-":"_";
			String [] segments = rawString.trim().toLowerCase().split(separator);
			StringBuilder camelCaseString = new StringBuilder();
			camelCaseString.append(segments[0]);
			for (int i = 1; i < segments.length; i++){
				String segment = segments[i];
				camelCaseString.append(segment.substring(0, 1).toUpperCase() + segment.substring(1));
			}
			return camelCaseString.toString();
		}
		else {
			rawString = rawString.substring(0, 1).toLowerCase() + rawString.substring(1);
			return rawString;
		}
	}
	
	public static String getTextContent(org.pegdown.ast.Node node) {
		StringBuilder content = new StringBuilder();
		if(node.getChildren().size() > 0){
			List<org.pegdown.ast.Node> nodes = node.getChildren();
			for (org.pegdown.ast.Node child : nodes) {
				if (child instanceof TextNode) {
					content.append(getTextContent(child));
				} else if (child instanceof SpecialTextNode) {
					content.append(getTextContent(child));
				}else if (child instanceof RefLinkNode){
					content.append("[" + getTextContent(child) + "]");
				}else {
					content.append(getTextContent(child));
				}
			}
		}else{
			content.append(((TextNode)node).getText());
		}
        return content.toString();
    }

    public static String getTextContent(TextNode node) {
        return node.getText();
    }
   
    public static boolean isResource(org.pegdown.ast.Node childNode) {
    	String content = getTextContent(childNode);
    	if (isMethod(content))
    		return false;
    	if (content.contains("/") || (content.contains("[") && content.contains("]")))
    		return true;
    	return false;
	}
    
	public static boolean isMethod(String method) {
		boolean isMethod = false;
		method = method.trim().toUpperCase();
		if (method.contains("["))
			method = method.substring(method.indexOf("[") + "[".length());
		
		if (method.startsWith("OPTIONS") || method.startsWith("GET")
				|| method.startsWith("HEAD") || method.startsWith("POST")
				|| method.startsWith("PUT") || method.startsWith("PATCH")
				|| method.startsWith("DELETE") || method.startsWith("TRACE")
				|| method.startsWith("CONNECT"))
			isMethod = true;
		return isMethod;
	}

	public static String getPath(String content) {
		if (!content.contains("/"))
			return null;
		String path; 
		path = content.substring(content.indexOf("/"));
		path = path.replace("]", "").trim();
		return path;
	}
	
	public static String getCleanExample(String content) {
		String example = "";
		if(content.contains("<?xml")){
			example = content.substring(content.indexOf("<?xml"));
		}else if(content.contains("{") || content.contains("[")){
			int openCurlyIndex = content.length();
			int openBracketIndex = content.length();
			if (content.contains("{"))
				openCurlyIndex = content.indexOf("{");
			if (content.contains("["))
				openBracketIndex = content.indexOf("[");
			int startInd = openCurlyIndex < openBracketIndex?openCurlyIndex:openBracketIndex;
			example = content.substring(startInd);
		}else{
			example = content;
		}
		
		return example.trim();
	}
	
	public static boolean isNextActionSection(String content) {
		content = content.trim();
		if(content.toLowerCase().startsWith("relation:"))
			return true;
		else if(content.toLowerCase().startsWith("parameters"))
			return true;
		else if(content.toLowerCase().startsWith("attributes"))
			return true;
		else if(content.toLowerCase().startsWith("request"))
			return true;
		else if(content.toLowerCase().startsWith("response"))
			return true;
		return false;
	}

	public static boolean isNextPayloadSection(String content) {
		content = content.trim();
		if(content.toLowerCase().startsWith("body"))
			return true;
		else if(content.toLowerCase().startsWith("headers"))
			return true;
		else if(content.toLowerCase().startsWith("schema"))
			return true;
		else if(content.toLowerCase().startsWith("attributes"))
			return true;
		return false;
	}
	
	public static org.pegdown.ast.Node extractHeaderParameters(org.pegdown.ast.Node node) throws IOException {
		org.pegdown.ast.Node headers = new RootNode();
		for (int i = 0; i < node.getChildren().size(); i++){
			org.pegdown.ast.Node header = extractHeaders(node.getChildren().get(i));
			headers.getChildren().addAll(header.getChildren());
		}
		return headers;
	}

	private static org.pegdown.ast.Node extractHeaders(org.pegdown.ast.Node node) throws IOException {
		org.pegdown.ast.Node headers = new RootNode();
		for (int i = 0; i < node.getChildren().size(); i++){
			org.pegdown.ast.Node childNode = node.getChildren().get(i);
			String childNodeContent = getTextContent(childNode);
			if(!isNextPayloadSection(childNodeContent)){
				BufferedReader rdr = new BufferedReader(new StringReader(childNodeContent));
				for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
				    TextNode headerLine = new TextNode(line);
				    RootNode rootNode = new RootNode();
				    rootNode.getChildren().add(headerLine);
				    headers.getChildren().add(rootNode);
				}
				rdr.close();
			}
		}
		return headers;
	}
	
	static org.pegdown.ast.Node getHeaderSection(org.pegdown.ast.Node node, org.pegdown.ast.Node childNode, int currentPosition) {
		org.pegdown.ast.Node headerSection = new RootNode();
		headerSection.getChildren().add(childNode);
		int j = currentPosition + 1;
		while (j < node.getChildren().size()){
			org.pegdown.ast.Node child = node.getChildren().get(j);
			String childContent = Utils.getTextContent(child);
			if (Utils.isNextPayloadSection(childContent))
				break;
			else
				headerSection.getChildren().add(child);
			j++;
		}
		return headerSection;
	}
	
	public static String saveExample(String mimeType, String path, String exampleName, String content){
		String fileName = "";
		String extension = ".json"; 
		if (!Utils.isEmptyString(mimeType)){
			if (mimeType.contains("xml"))
				extension = ".xml";
			else if (mimeType.contains("plain"))
				extension = ".md";
		}
		if (!Utils.isEmptyString(exampleName))
			exampleName = exampleName.substring(0,1).toLowerCase() + exampleName.substring(1);
		fileName = exampleName + "-example" + extension;
		String examplePath =  path + "\\examples\\" + fileName;
		
		try {
			FileUtils.writeStringToFile(new File(examplePath), content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	protected static org.pegdown.ast.Node extractListElements(org.pegdown.ast.Node parentNode, String sectionName) {
		RootNode sections = new RootNode();
		boolean isRelevant = false;
		
		for (int i = 0; i < parentNode.getChildren().size(); i++){
			org.pegdown.ast.Node currentChildNode = parentNode.getChildren().get(i);
			String currentContent = Utils.getTextContent(currentChildNode);
			boolean isFirstNode = currentContent.toLowerCase().trim().startsWith(sectionName);
			if (isFirstNode)
				isRelevant = true;
			else if (!isFirstNode & Utils.isNextActionSection(currentContent))
				isRelevant = false;
			
			if (isRelevant)
				sections.getChildren().add(currentChildNode);
		}
		return sections;
	}

	public static org.pegdown.ast.Node getParamItems(
			org.pegdown.ast.Node parameters) {
		org.pegdown.ast.Node paramItemsNode = new RootNode();
		for (int i = 0; i < parameters.getChildren().size(); i++){
			org.pegdown.ast.Node childNode = parameters.getChildren().get(i);
			if (childNode instanceof BulletListNode & childNode.getChildren().size()>1)
				paramItemsNode.getChildren().addAll(childNode.getChildren());
			else 
				paramItemsNode = getParamItems(childNode);
		}
		return paramItemsNode;
	}
}
