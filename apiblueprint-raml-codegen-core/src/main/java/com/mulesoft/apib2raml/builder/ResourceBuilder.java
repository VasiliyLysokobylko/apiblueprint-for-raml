package com.mulesoft.apib2raml.builder;

import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.ResourceModel;
import com.mulesoft.apib2raml.model.ResourceTypeModel;

public class ResourceBuilder extends AbstractResourceBuilder<ResourceModel>{

	public ResourceBuilder(Class<ResourceModel> modelClass) {
		super(modelClass);
	}

	public void fillModel(ResourceModel resource, AbstractNode node) throws Exception {
		super.fillModel(resource, node);
//		Element element;
//		String type = element.getAttribute("type");
//		if(!type.isEmpty()){
//			if (type.startsWith("#"))
//				type = type.substring("#".length());
//			
//			ResourceTypeBuilder resourceTypeBuilder = getBuildManager().getBuilder(ResourceTypeBuilder.class);
//			ResourceTypeModel resourceTypeModel = resourceTypeBuilder.build(element.getAttribute("type"));
//			resource.setType(resourceTypeModel);
//		}
	}
}
