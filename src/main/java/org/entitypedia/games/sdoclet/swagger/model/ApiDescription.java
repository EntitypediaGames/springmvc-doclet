package org.entitypedia.games.sdoclet.swagger.model;

import java.util.List;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ApiDescription {

    private final String path;
    private String description;
    private List<Operation> operations;

    public ApiDescription(String path) {
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

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}