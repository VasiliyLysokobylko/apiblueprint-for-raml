package org.mulesoft.raml.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.model.MimeType;
import org.raml.model.parameter.FormParameter;

import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.RepresentationModel;
import com.mulesoft.apib2raml.model.ResponseModel;

public class MimeTypeBuilder {
    
    private RamlParameterBuilder paramBuilder = new RamlParameterBuilder();
    
    public MimeType buildMimeType(ResponseModel representationModel){
        MimeType mimeType = new MimeType();
        String mediaType = representationModel.getMediaType();
        String schema = representationModel.getSchema();
        String example = representationModel.getExample();
        mimeType.setType(mediaType);
//        mimeType.setSchemaOrigin(schema);
        mimeType.setSchema(schema);
        mimeType.setExample(example);
//        List<ParameterModel> formParameters = representationModel.getFormParameters();
        
//        Map<String, List<FormParameter>> formParamMap = mimeType.getFormParameters();
//        for(ParameterModel paramModel : formParameters){
//            String name = paramModel.getName();
//            FormParameter formParam = paramBuilder.buildFormParameter(paramModel);
//            List<FormParameter> list = formParamMap.get(name);
//            if(list == null){
//                list = new ArrayList<FormParameter>();
//                formParamMap.put(name, list);
//            }
//            list.add(formParam);
//        }
        return mimeType;
    }

}
