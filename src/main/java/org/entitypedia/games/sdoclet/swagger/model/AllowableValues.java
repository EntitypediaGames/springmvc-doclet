package org.entitypedia.games.sdoclet.swagger.model;

import java.util.List;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class AllowableValues {
    private List<String> values;

    public AllowableValues(List<String> values) {
        this.values = values;
    }

    public String getValueType() {
        return "List";
    }

    public List<String> getValues() {
        return values;
    }
}