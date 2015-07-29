package com.mulesoft.apib2raml.model;

import java.util.ArrayList;
import java.util.List;

public class HasRepresentation extends AbstractElement {
    
    
    private List<RepresentationModel> representations = new ArrayList<RepresentationModel>();


    public List<RepresentationModel> getRepresentations() {
        return representations;
    }

    public void addRepresentation(RepresentationModel representation) {
        this.representations.add(representation);
    }

}
