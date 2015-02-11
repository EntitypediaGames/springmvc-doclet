package org.entitypedia.games.sdoclet.swagger.model;

import org.entitypedia.games.sdoclet.DocletOptions;

import java.util.List;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ResourceListing {

    private final String apiVersion;
    private final String basePath;
    private final List<ApiListingReference> apis;
    private final List<AuthorizationType> authorizations;
    private final ApiInfo info;

    public ResourceListing(String apiVersion, String basePath, List<ApiListingReference> apis, List<AuthorizationType> authorizations, ApiInfo info) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.apis = apis;
        this.authorizations = authorizations;
        this.info = info;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getSwaggerVersion() {
        return DocletOptions.SWAGGER_VERSION;
    }

    public String getBasePath() {
        return basePath;
    }

    public List<ApiListingReference> getApis() {
        return apis;
    }

    public List<AuthorizationType> getAuthorizations() {
        return authorizations;
    }

    public ApiInfo getInfo() {
        return info;
    }
}