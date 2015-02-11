package org.entitypedia.games.sdoclet.swagger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.javadoc.FieldDoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href="https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class ModelProperty {

    private String type;
    private Integer position;
    private Boolean required = false;
    private String description;
    private AllowableValues allowableValues;

    @JsonIgnore
    private List<String> containerOf;

    public ModelProperty(String type, String description, List<String> containerOf) {
        this.type = type;
        if (null != description && !"".equals(description.trim())) {
            this.description = description;
        }
        this.containerOf = containerOf;
        if (type.equals("boolean")) {
            List<String> values = new ArrayList<>();
            values.add("false");
            values.add("true");
            allowableValues = new AllowableValues(values);
        }
    }

    public ModelProperty(FieldDoc[] enumConstants, String description) {
        this.type = "string";
        this.description = description;
        List<String> values = new ArrayList<>();
        for (FieldDoc fieldDoc : enumConstants) {
            values.add(fieldDoc.name());
        }
        this.allowableValues = new AllowableValues(values);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AllowableValues getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(AllowableValues allowableValues) {
        this.allowableValues = allowableValues;
    }

    public void setContainerOf(List<String> containerOf) {
        this.containerOf = containerOf;
    }

    public Map<String, String> getItems() {
        Map<String, String> result = null;
        if (null != containerOf && 0 < containerOf.size()) {
            result = new HashMap<>();
            for (String type : containerOf) {
                result.put("$ref", type);
            }
        }
        return result;
    }
}