package com.mulesoft.apib2raml.builder;

import java.security.acl.LastOwnerException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pegdown.ast.Node;

import com.mulesoft.apib2raml.model.AbstractElement;
import com.mulesoft.apib2raml.model.ApplicationModel;
import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.RepresentationModel;
import com.mulesoft.apib2raml.model.RequestModel;
import com.mulesoft.apib2raml.model.ResourceModel;
import com.mulesoft.apib2raml.model.ResourceTypeModel;
import com.mulesoft.apib2raml.model.ResponseModel;

public class BuildManager {
	
	private HashMap<Class<? extends AbstractBuilder<?>>, AbstractBuilder<?>> builderMap;
	
	private HashMap<Class<? extends AbstractElement>, HashMap<String, AbstractElement>> modelMap
		= new HashMap<Class<? extends AbstractElement>, HashMap<String,AbstractElement>>();
	
	public BuildManager() {
		init();
	}
	
	protected String currentResource = "";
	
	protected HashMap<String,String> globalSchemas = new HashMap<String, String>(); 
	
	protected Set<String> exampleNames = new HashSet<String>();
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractElement>T getModelElement(Class<T> clazz, Node node){
		
		String id = null;
//		if(element.hasAttribute("id")){
//			id = element.getAttribute("id");
//		}
//		else if(element.hasAttribute("href")){
//			id = element.getAttribute("href");
//			if(id.startsWith("#")){
//				id = id.substring(1);
//			}
//		}
		
		if(id==null){
			try {
				T newInstance = clazz.newInstance();
				return newInstance;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		HashMap<String, AbstractElement> map = modelMap.get(clazz);
//		if(map==null){
//			map = new HashMap<String, AbstractElement>();
//			modelMap.put(clazz, map);
//		}
//		AbstractElement instance = map.get(id);
//		if(instance==null){
//			try {
//				instance = clazz.newInstance();
////				instance.setId(id);
//				map.put(id, instance);				
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//		return (T) instance;
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractElement>T getModelElement(Class<T> clazz, String id){
//		if(id.startsWith("#")){
//			id = id.substring("#".length());
//		}
//		
//		HashMap<String, AbstractElement> map = modelMap.get(clazz);
//		if(map==null){
//			map = new HashMap<String, AbstractElement>();
//			modelMap.put(clazz, map);
//		}
		AbstractElement instance = null; //= map.get(id);
//		if(instance==null){
//			try {
//				instance = clazz.newInstance();
//				instance.setId(id);
//				map.put(id, instance);				
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
		return (T) instance;
	}
	
	public ApplicationModel process(Node node) throws Exception{
		ApplicationBuilder appBuilder = getBuilder(ApplicationBuilder.class);
		ApplicationModel appModel = appBuilder.build(node);
		return appModel;
	}
	
	
	public void setPathResolver(IPathResolver pathResolver){
		for(AbstractBuilder<?> bld : builderMap.values()){
			bld.setPathResolver(pathResolver);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public <T>T getBuilder(Class<T> clazz){
		return (T) builderMap.get(clazz) ;
	}
	
	private static final HashMap<Class<?>,Class<?>> builderToModelMap = new HashMap<Class<?>, Class<?>>();
	static{
		builderToModelMap.put(ApplicationBuilder.class, ApplicationModel.class);
		builderToModelMap.put(MethodBuilder.class, MethodModel.class);
		builderToModelMap.put(ParameterBuilder.class, ParameterModel.class);
		builderToModelMap.put(URIParameterBuilder.class, ParameterModel.class);
		builderToModelMap.put(RepresentationBuilder.class,	RepresentationModel.class);
		builderToModelMap.put(RequestBuilder.class, RequestModel.class);
		builderToModelMap.put(ResourceTypeBuilder.class, ResourceTypeModel.class);
		builderToModelMap.put(ResourceBuilder.class, ResourceModel.class);
		builderToModelMap.put(ResponseBuilder.class, ResponseModel.class);
//		builderToModelMap.put(DocumentationExtractor.class, DocumentationModel.class);
	}


	@SuppressWarnings("unchecked")
	private void init() {
		builderMap = new HashMap<Class<? extends AbstractBuilder<?>>, AbstractBuilder<?>>();
		for(Map.Entry<Class<?>,Class<?>> entry : builderToModelMap.entrySet()){
			Class<? extends AbstractBuilder<?>> builderClass = (Class<? extends AbstractBuilder<?>>) entry.getKey();
			Class<?> modelClass = entry.getValue();
			try {
				AbstractBuilder<?> builder = builderClass.getConstructor(Class.class).newInstance(modelClass);
				builder.setBuildManager(this);
				builderMap.put(builderClass, builder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setCurrentResource(String currentResource) {
		currentResource = currentResource.trim();
		while (currentResource.endsWith("/"))
			currentResource = currentResource.substring(0, currentResource.length() - "/".length()); 
		currentResource = currentResource.substring(currentResource.lastIndexOf("/") + "/".length());
		currentResource = currentResource.replace("{", "");
		currentResource = currentResource.replace("}", "");
		currentResource = currentResource.replace("?", "");
		this.currentResource = currentResource;
	}

	public HashMap<String, String> getGlobalSchemas() {
		return globalSchemas;
	}
	
	public String getCurrentResource(){
		return this.currentResource;
	}
	
	public String registerGlobalSchema(String content){
		String schemaName = this.currentResource;
		int schemaNumber = 1;
		while (this.globalSchemas.containsKey(schemaName)){
			schemaName = schemaName + schemaNumber;
			schemaNumber++;
		}
		globalSchemas.put(schemaName, content);
		
		return schemaName;
	}

	public String getExampleName() {
		String exampleName = this.currentResource;
		int index = 1; 
		while(this.exampleNames.contains(exampleName)){
			exampleName = this.currentResource + index;
			index++;
		}
		if (Utils.isEmptyString(exampleName))
			exampleName = "file";
		this.exampleNames.add(exampleName);
		
		return exampleName;
	}
}
