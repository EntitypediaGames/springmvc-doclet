package org.entitypedia.games.sdoclet.swagger.model;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ApiListingReference {

    /** The base path that is prepended to all @Path elements. This may be an override for certain scenarios only */
    private final String path;

    /** Short description of the Api */
    private String description;

    /** optional explicit ordering of this Api in the Resource Listing */
    private Integer position;

    public ApiListingReference(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
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