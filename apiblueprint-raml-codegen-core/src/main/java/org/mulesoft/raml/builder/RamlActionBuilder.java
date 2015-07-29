package org.mulesoft.raml.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;

import com.mulesoft.apib2raml.builder.Utils;
import com.mulesoft.apib2raml.model.HasRepresentation;
import com.mulesoft.apib2raml.model.MethodModel;
import com.mulesoft.apib2raml.model.ParameterModel;
import com.mulesoft.apib2raml.model.RepresentationModel;
import com.mulesoft.apib2raml.model.RequestModel;
import com.mulesoft.apib2raml.model.ResponseModel;

public class RamlActionBuilder {
    
    private RamlParameterBuilder paramBuilder = new RamlParameterBuilder();
    
    private MimeTypeBuilder mimeTypeBuilder = new MimeTypeBuilder();
    
    public Action buildRamlAction(MethodModel methodModel){
        Action action = new Action();
        
        try{
            String typeString = methodModel.getName();   
            if (typeString != null){
            	ActionType type = Enum.valueOf(ActionType.class, typeString);
            	action.setType(type);
            }
        }
        catch(Exception e){
            
        }
        Utils.setDocumentation(methodModel, action);
        
        action.setDescription(methodModel.getDescription());
        
        Map<String, MimeType> bodyMap = action.getBody();
        Map<String, QueryParameter> queryParamMap = action.getQueryParameters();
        Map<String, Response> responsesMap = action.getResponses();
        Map<String, Header> headersMap = action.getHeaders();
        
        List<ParameterModel> queryParams = methodModel.getQueryParams();
        
        List<RequestModel> requests = methodModel.getRequests();
        for(RequestModel requestModel : requests){
            
        	MimeType mimeType = getRequestMimeType(requestModel);
        	String mediaType = mimeType.getType();
        	if (mediaType != null)
        		bodyMap.put(mediaType, mimeType);
            
            for(ParameterModel paramModel : queryParams){
                String name = paramModel.getName();
                QueryParameter qParam = paramBuilder.buildQueryParameter(paramModel);
                queryParamMap.put(name, qParam);
            }
            
            List<ParameterModel> headers = requestModel.getHeaders();
            for(ParameterModel paramModel: headers){
                String name = paramModel.getName();
                Header header = paramBuilder.buildHeader(paramModel);
                headersMap.put(name, header);
            }
        }
        
        Map<String, List<ResponseModel>> responses = methodModel.getResponses();
        for(List<ResponseModel> list : responses.values()){
            ResponseModel responseModel = list.get(0);
            String status = responseModel.getStatus();
            if(Utils.isEmptyString(status)){
                status = "200";
            }
            Response response = new Response();
            responsesMap.put(status, response);
            
            Utils.setDocumentation(responseModel, response);
            
            Map<String, Header> responseHeadersMap = response.getHeaders();
            Map<String, MimeType> responseBodyMap = response.getBody();            
            
//            List<RepresentationModel> representations = responseModel.getRepresentations();
            for (ResponseModel resp: list){
            	MimeType mimeType = mimeTypeBuilder.buildMimeType(resp);
            	String mediaType = mimeType.getType();
            	if (mediaType != null)
            		responseBodyMap.put(mediaType, mimeType);
            }
            
            List<ParameterModel> responseHeaders = responseModel.getHeaders();
            for(ParameterModel paramModel: responseHeaders){
                String name = paramModel.getName();
                Header header = paramBuilder.buildHeader(paramModel);
                responseHeadersMap.put(name, header);
            }
        }
        
        return action;        
    }
    
    private MimeType getRequestMimeType(RequestModel requestModel){
        MimeType mimeType = new MimeType();
        String mediaType = requestModel.getMediaType();
        String schema = requestModel.getSchema();
        String example = requestModel.getExample();
        mimeType.setType(mediaType);
        mimeType.setSchema(schema);
        mimeType.setExample(example);

        return mimeType;
    }

}
