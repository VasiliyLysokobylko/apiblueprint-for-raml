package com.mulesoft.apib2raml.builder;

public interface IPathResolver {
	
	String getContent(String path);
	
	String getOutputRootPath();
}
