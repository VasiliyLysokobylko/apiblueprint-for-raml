package com.mulesoft.apib2raml.builder;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.w3c.dom.Element;

import com.mulesoft.apib2raml.model.AbstractElement;

public abstract class AbstractBuilder<T extends AbstractElement> {
	
	protected Class<T> modelClass;
	
	public AbstractBuilder(Class<T> modelClass) {
		this.modelClass = modelClass;
	}

	protected IPathResolver pathResolver;
	
	protected BuildManager buildManager;
	

	protected void extractDocumentation(Element xmlEelement, AbstractElement modelElement) throws Exception {
//		DocumentationExtractor docExtractor = getBuildManager().getBuilder(DocumentationExtractor.class);
//		DocumentationModel doc = docExtractor.build(xmlEelement);
//	    modelElement.setDescription(doc);
	}
	
	protected T build(Node node) throws Exception{
		T modelElement = getBuildManager().getModelElement(modelClass, node);
		fillModel(modelElement, node);
		return modelElement;
	}

	protected T build(String element) throws Exception{
		T modelElement = getBuildManager().getModelElement(modelClass, element);
		
		return modelElement;
	}
	
	abstract void fillModel(T modelElement, Node node) throws Exception;

	public IPathResolver getPathResolver() {
		return pathResolver;
	}

	public void setPathResolver(IPathResolver pathResolver) {
		this.pathResolver = pathResolver;
	}

	public BuildManager getBuildManager() {
		return buildManager;
	}

	public void setBuildManager(BuildManager buildManager) {
		this.buildManager = buildManager;
	}
}
