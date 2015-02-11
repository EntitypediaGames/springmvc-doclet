package org.entitypedia.games.sdoclet.swagger.model;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ResponseMessage {

    private final Integer code;
    private final String message;
    private String responseModel;

    public ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getResponseModel() {
        return responseModel;
    }

    public void setResponseModel(String responseModel) {
        this.responseModel = responseModel;
    }
}