package org.entitypedia.games.sdoclet.swagger.model;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class Parameter {

    private final String name;
    private String description;
    private String defaultValue;
    private Boolean required;
    private Boolean allowMultiple;
    private final String dataType;
    private AllowableValues allowableValues;
    private String paramType;
    private String paramAccess;

    public Parameter(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getDataType() {
        return dataType;
    }

    public AllowableValues getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(AllowableValues allowableValues) {
        this.allowableValues = allowableValues;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamAccess() {
        return paramAccess;
    }

    public void setParamAccess(String paramAccess) {
        this.paramAccess = paramAccess;
    }
}