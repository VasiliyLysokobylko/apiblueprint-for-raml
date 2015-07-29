package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class ParameterModel extends AbstractElement {
    private String name;
    
    private String style;
    
    private String defaultValue;
    
    private String path;
    
    private String fixedValue;
    
    private String example;
    
    private String type;
    
    private boolean required = false;
    
    private boolean repeating = false;
    
    private List<String> options = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getExample() {
		return this.example;
	}

	public void setExample(String example) {
		this.example = example;
	}
}
