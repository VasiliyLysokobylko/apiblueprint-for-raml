package com.mulesoft.apib2raml.builder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.TextNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mulesoft.apib2raml.model.ApplicationModel;
import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ResourceModel;
import com.mulesoft.apib2raml.builder.ResourceBuilder;


public class ApplicationBuilder extends AbstractBuilder<ApplicationModel> {
    
	public ApplicationBuilder(Class<ApplicationModel> modelClass) {
		super(modelClass);
	}

	private final String DEFAULT_API_TITLE = "Enter API title here";
    
    public void fillModel(ApplicationModel app, org.pegdown.ast.Node node) throws Exception{
    	
    	app.setIncludedSchemas(getBuildManager().getGlobalSchemas());
    	
    	String apiTitle = getTitle(node);
    	if(!Utils.isEmptyString(apiTitle))
    		app.setTitle(apiTitle);
    	else
    		app.setTitle(DEFAULT_API_TITLE);
    	
    	String baseURI = getBaseURI(node);
    	if(!Utils.isEmptyString(baseURI))
    		app.setBaseUri(baseURI);
    	
//        extractDocumentation(element, app);
//        
//        Map <String, String> includedSchemas = getSchemas(element, pathResolver);
//        app.setIncludedSchemas(includedSchemas);
//        

        List<ResourceModel> resources = extractResources(node);
//        List<ResourceModel> resourceTypes = extractResources(node);
       
        for(ResourceModel resource : resources){
        	app.addResource(resource);
        }
        
        
        
        
//        List<Element> resourcesElements = Utils.extractElements(element, "resources");
//        for(Element resourcesElement : resourcesElements){
//            
//            String baseUri = resourcesElement.getAttribute("base");
//            String title = resourcesElement.getAttribute("title");
//            if(app.getBaseUri()==null){
//                app.setBaseUri(baseUri);
//            }
//            if (app.getTitle() == null && !title.equals("")){
//            	app.setTitle(title);
//            } 
//            else {
//            	app.setTitle(DEFAULT_API_TITLE);
//            }
//            
//            List<Element> resourceElements = Utils.extractElements(resourcesElement,"resource");
//            for(Element resourceElement : resourceElements){
//				ResourceModel res = resourceBuilder.build(resourceElement);
//                Utils.setParentUri(res,"");
//                app.addResource(res);
//            }
//        }
//        
//        ResourceTypeBuilder resourceTypeBuilder = getBuildManager().getBuilder(ResourceTypeBuilder.class);
//        List<Element> resourceTypeElements = Utils.extractElements(element, "resource_type");
//        for(Element resourceTypeElement : resourceTypeElements){
//        	resourceTypeBuilder.build(resourceTypeElement);
//        }
//
//        MethodBuilder methodBuilder = getBuildManager().getBuilder(MethodBuilder.class);
//        List<Element> methodElements = Utils.extractElements(element, "method");
//        for(Element methodElement: methodElements){
//        	methodBuilder.build(methodElement);
//        }
//        
//        RepresentationBuilder representationBuilder = getBuildManager().getBuilder(RepresentationBuilder.class);
//        List<Element> representationElements = Utils.extractElements(element, "representation");
//        for(Element representationElement: representationElements){
//        	representationBuilder.build(representationElement);
//        }
    }

	private String getTitle(org.pegdown.ast.Node node) {
		for (org.pegdown.ast.Node titleNode : node.getChildren()){
			if(titleNode instanceof HeaderNode){
				String title = Utils.getTextContent(titleNode);
				return title;
			}
		}
		return null;
	}
    
	private String getBaseURI(Node node) {
		for (org.pegdown.ast.Node baseURINode: node.getChildren()){
			String text = Utils.getTextContent(baseURINode);
			if(text.trim().toUpperCase().startsWith("HOST:")){
				return (text.substring("HOST:".length())).trim();
			}
		}
		return null;
	}
   
    private List<ResourceModel> extractResources(Node node) throws Exception {
    	List<ResourceModel> resources = new ArrayList<ResourceModel>();
    	
    	RootNode resourceNodes = new RootNode();
    	boolean isFirst = true;
    	for(int i = 0; i < node.getChildren().size(); i++){
    		boolean isLast = i+1 == node.getChildren().size();
    		org.pegdown.ast.Node childNode = node.getChildren().get(i);
    		if ((childNode instanceof HeaderNode || isLast) & !isFirst){
    			if(isLast)
    				resourceNodes.getChildren().add(childNode);
    			if(Utils.getTextContent(childNode).contains("/") || isLast){
    				ResourceBuilder resourceBuilder = getBuildManager().getBuilder(ResourceBuilder.class);
    				ResourceModel res = resourceBuilder.build(resourceNodes);
    				setMethodsQuery(res);
    				resources.add(res);
    				resourceNodes = new RootNode();
    			}
    			if (!isLast)//TODO May add unnecessary nodes. Check it!
    				resourceNodes.getChildren().add(childNode);
    		}else{
    			if (isFirst && (childNode instanceof HeaderNode)){
    				if(Utils.isResource(childNode) || Utils.isMethod(Utils.getTextContent(childNode))){
    					resourceNodes.getChildren().add(childNode);
    					isFirst = false;
    				}
    			}if(!isFirst)
    				resourceNodes.getChildren().add(childNode);
    		}
    	}
    		
    	return resources;
    }

private void setMethodsQuery(ResourceModel res) {
		if (res.getQueryParams().size() > 0){
			for (MethodModel method : res.getMethods()){
//				method.getQueryParams().addAll(res.getQueryParams());
			}
		}
	}

/*old methods*/
    //TODO look at old methods
	private Map<String, String> getSchemas(Element element, IPathResolver pathResolver) {
    	
    	List<String> includePaths = getIncludePaths(element);
    	
    	Map<String, String> grammarsElements = null;
		try {
			grammarsElements = getGrammarsElements(element);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	Map<String,String> schemas = new HashMap<String, String>();
    	schemas.putAll(grammarsElements);
    	
    	for (String path : includePaths){
    		String schemaContent = pathResolver.getContent(path);
    		separateSchemasByElement(schemas, schemaContent);
    	}
    	return schemas;
	}
    
    private void separateSchemasByElement(Map<String, String> schemas, String schemaContent) {
    	try {
			Map<String, org.w3c.dom.Node> customTypes = new HashMap<String, org.w3c.dom.Node>();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (IOUtils.toInputStream(schemaContent));
			NodeList rootNodes = doc.getChildNodes();
			for (int i = 0; i < rootNodes.getLength(); i++){
				NodeList schemaNodes = rootNodes.item(i).getChildNodes();
				for (int j = 0; j < schemaNodes.getLength(); j++){
					org.w3c.dom.Node schemaNode = (org.w3c.dom.Node)schemaNodes.item(j);
					String type = schemaNodes.item(j).getNodeName().toLowerCase();
					if (type.contains("complextype") || type.contains("simpletype")){
						String schemaName = schemaNode.getAttributes().getNamedItem("name").getNodeValue();
						customTypes.put(schemaName , schemaNode);
					}
				}
			}
			List<String> elementNames = getRootElementNames(schemaContent);
			
			for (String elementName : elementNames){
				org.w3c.dom.Node rootNode = doc.cloneNode(true);
				org.w3c.dom.Node elementNode = getNodeByName(rootNode.getChildNodes(), elementName);
				List<String> usingTags = getUsingTags(elementNode, customTypes);
				
				for (String element : elementNames){
					if (!element.equals(elementName)){
						org.w3c.dom.Node node = getNodeByName(rootNode.getChildNodes(), element);
						node.getParentNode().removeChild(node);
					}
				}
				
				Iterator<Map.Entry<String, org.w3c.dom.Node>> iterator = customTypes.entrySet().iterator();
				while (iterator.hasNext()){
					Map.Entry<String, org.w3c.dom.Node> entry = iterator.next();
					String key = entry.getKey();
					if (!usingTags.contains(key)){
						org.w3c.dom.Node node = getNodeByName(rootNode.getChildNodes(), key);
						node.getParentNode().removeChild(node);
					}
				}

				String newSchema = docToString((Document)rootNode);
				schemas.put(elementName, newSchema);
				System.out.println();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	private List<String> getUsingTags(org.w3c.dom.Node node, Map<String, org.w3c.dom.Node> customTypes) {
		List <String> usingTags = new ArrayList<String>();
		
		boolean hasNodeType = hasAttribute(node, "type");
		boolean hasChild = node.hasChildNodes();
		if (!hasNodeType && hasChild){
			for (int i = 0; i < node.getChildNodes().getLength(); i++){
				usingTags.addAll(getUsingTags(node.getChildNodes().item(i), customTypes));
			}
			return usingTags;
		} else if (!hasNodeType && !hasChild)
			return usingTags;
		
		String complexTypeKey = node.getAttributes().getNamedItem("type").getNodeValue();
		if (complexTypeKey.contains(":"))
			complexTypeKey = complexTypeKey.substring(complexTypeKey.indexOf(":") + ":".length());
		if(!customTypes.containsKey(node.getAttributes().getNamedItem("type").getNodeValue()) & !customTypes.containsKey(complexTypeKey))
			return usingTags;
		org.w3c.dom.Node complexType = customTypes.get(complexTypeKey);
		NodeList childNodes = complexType.getChildNodes();
		usingTags.add(complexTypeKey);
		
		for (int i = 0; i < childNodes.getLength(); i++){
			org.w3c.dom.Node childNode = childNodes.item(i);
			boolean hasChildNodeType = hasAttribute(childNode, "type");
			boolean hasChildNodes = childNode.hasChildNodes();
			if (!hasChildNodeType && !hasChildNodes)
				continue;
			if (hasChildNodes)
				usingTags.addAll(getUsingTags(childNode, customTypes));
			if (hasChildNodeType){
				String childType = childNode.getAttributes().getNamedItem("type").getNodeValue();
				if(customTypes.containsKey(childNode.getAttributes().getNamedItem("type").getNodeValue())){
					usingTags.add(childType);
				}
			}
		}
		return usingTags;
	}

	private List<String> getRootElementNames(String content) {
		
		ArrayList<String> list = new ArrayList<String>();
//		String propertyValue = System.getProperty(DOMImplementationRegistry.PROPERTY);
//
//		System.setProperty(DOMImplementationRegistry.PROPERTY,"org.apache.xerces.dom.DOMXSImplementationSourceImpl");
//		DOMImplementationRegistry registry;
//		try {
//			ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes("UTF-8"));
//			LSInput input = new DOMInputImpl();
//			input.setByteStream(bais);
//
//			registry = DOMImplementationRegistry.newInstance();
//
//			XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
//			XSLoader schemaLoader = impl.createXSLoader(null);
//			XSModel model = schemaLoader.load(input);
//
//			XSNamedMap declarations = model.getComponents(XSConstants.ELEMENT_DECLARATION);
//			
//			for(int i = 0 ; i < declarations.size() ; i++){
//				String schemaName = declarations.item(i).getName();
//				list.add(schemaName);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		finally{
//			if(propertyValue!=null){
//				System.setProperty(DOMImplementationRegistry.PROPERTY,propertyValue);
//			}
//			else{
//				System.clearProperty(DOMImplementationRegistry.PROPERTY);
//			}
//		}
		return list;
	}

	private List<String> getIncludePaths (Element element){
    	List<String> includePaths = new ArrayList<String>();
//    	List<Element> grammars = Utils.extractElements(element, "grammars");
//    	 
//    	for (Element grammar : grammars){
//    		List<Element> includes = Utils.extractElements(grammar, "include");  
//    		for (Element include : includes)
//    			if (include.hasAttribute("href"))
//    				includePaths.add(include.getAttribute("href"));
//    	}
    	return includePaths;
    }
	
	private org.w3c.dom.Node getNodeByName (NodeList nodeList, String name){
		org.w3c.dom.Node node = null;
		for (int i = 0; i < nodeList.getLength(); i++){
			node = nodeList.item(i);
			if (node != null && hasAttribute(node, "name")){
				String nameValue = node.getAttributes().getNamedItem("name").getNodeValue();
				if (nameValue.equals(name))
					return node;
			}
		}
		for (int n = 0; n < nodeList.getLength(); n++){
			node = getNodeByName(nodeList.item(n).getChildNodes(), name);
			if (hasAttribute(node, "name") && (node.getAttributes().getNamedItem("name").getNodeValue().equals(name)))
				return node;
		}
		return node;
	}
	
	private boolean hasAttribute(org.w3c.dom.Node node, String attribute) {
		NamedNodeMap attributeNodes = node.getAttributes();
		if (attributeNodes != null){
			for (int i = 0; i < attributeNodes.getLength(); i++){
				org.w3c.dom.Node attributeNode = attributeNodes.item(i);
				if (attributeNode.getNodeName().equals(attribute))
					return true;
			}
		}
		return false;
	}
	
	private String docToString(Document doc) {
	    try {
	        StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}

	private Map<String,String> getGrammarsElements(Element element) throws Exception {
		Map<String, String> schemasElements = new HashMap<String, String>();
//		List<Element> grammars = Utils.extractElements(element, "grammars");
//		
//		for (Element grammar : grammars){
//    		List<Element> elements = Utils.extractElements(grammar, "xs:element");  
//    		for (Element schema : elements){
//    			if (schema.hasAttribute("name")){
//    				String rawSchema = elementToString(schema);
//    				String headerPart = rawSchema.substring(0, rawSchema.indexOf(">") + ">".length());
//    				String schemaContent = headerPart + "\n" + 
//    						"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n" +
//    						rawSchema.substring(headerPart.length()) +
//    						"\n</xs:schema>";
//    				schemasElements.put(schema.getAttribute("name"), schemaContent);
//    			}
//    		}
//    	}
		return schemasElements;
	}
}
