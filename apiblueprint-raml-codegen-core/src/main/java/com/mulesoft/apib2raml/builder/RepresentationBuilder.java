package com.mulesoft.apib2raml.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.RepresentationModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class RepresentationBuilder extends AbstractBuilder<RepresentationModel> {

	public RepresentationBuilder(Class<RepresentationModel> modelClass) {
		super(modelClass);
	}

	public void fillModel(RepresentationModel representation, Node node) throws Exception {

//		String mediaType = element.getAttribute("mediaType");
//		representation.setMediaType(mediaType);
//
//		String elementName = element.getAttribute("element");
//		if (elementName.contains(":")) {
//			elementName = elementName
//					.substring(elementName.lastIndexOf(":") + 1);
//		}
//		String schemaName = elementName;
//		representation.setSchema(schemaName);
//
//		List<Element> docs = Utils.extractElements(element, "wadl:doc");
//		List<Element> examples = new ArrayList<Element>();
//		for (Element docElement : docs) {
//			examples.addAll(Utils.extractElements(docElement, "xsdxt:code"));
//		}
//		if (examples.size() > 0) {
//			String exampleFilePath = examples.get(0).getAttribute("href");
//			try {
//				String exampleContent = getPathResolver().getContent(exampleFilePath);
//				String fileName = exampleFilePath.substring(
//					exampleFilePath.lastIndexOf("/") + "/".length(),
//					exampleFilePath.lastIndexOf("."));
//				fileName = Utils.stringToCamel(fileName).replace(".", "");
//				boolean isXML = mediaType.equals("application/xml");
//				String postfix = isXML?"-example.xml":"-example.json";
//				String example = " !include examples/" + fileName + postfix;
//				FileUtils.writeStringToFile(new File(getPathResolver().getOutputRootPath() + "/examples/" + fileName + postfix), exampleContent);
//				representation.setExample(example);
//				
//				if (isXML)
//					fileName = fileName + "XML";
//				
//				representation.setSchema(fileName);
////				String schema = getSchema(String exampleContent, boolean isXML);
//			}
//			catch (FileNotFoundException e){
//				e.printStackTrace();
//			}
//		}
//
//		ParameterBuilder paramBuilder = getBuildManager().getBuilder(
//				ParameterBuilder.class);
//		List<Element> paramElements = Utils.extractElements(element, "param");
//		for (Element paramElement : paramElements) {
//			ParameterModel param = paramBuilder.build(paramElement);
//			String style = param.getStyle();
//			if ("queryr".equals(style)) {
//				representation.addFormParameter(param);
//			}
//		}
	}
}
