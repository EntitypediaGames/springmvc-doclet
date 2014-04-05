package org.entitypedia.games.sdoclet.swagger.model;

import java.util.Map;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class Model {

    private final String name;
    private Map<String, ModelProperty> properties;
    private String description;

    public Model(String name, Map<String, ModelProperty> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public Map<String, ModelProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ModelProperty> properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}