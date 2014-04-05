package org.entitypedia.games.sdoclet.swagger.model;

import java.util.List;
import java.util.Map;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ApiListing {

    private String apiVersion;
    private String swaggerVersion;
    private String basePath;
    private String resourcePath;

    /** content type produced by this Api */
    private List<String> produces;

    /** media type consumed by this Api */
    private List<String> consumes;

    /** protocols that this Api requires (i.e. https) */
    private List<String> protocols;

    /** authorizations required by this Api */
    private List<String> authorizations;

    private List<ApiDescription> apis;
    private Map<String, Model> models;

    /** General description of this class */
    private String description;
    private Integer position;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }

    public List<String> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(List<String> authorizations) {
        this.authorizations = authorizations;
    }

    public List<ApiDescription> getApis() {
        return apis;
    }

    public void setApis(List<ApiDescription> apis) {
        this.apis = apis;
    }

    public Map<String, Model> getModels() {
        return models;
    }

    public void setModels(Map<String, Model> models) {
        this.models = models;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}