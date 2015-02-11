package org.entitypedia.games.sdoclet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.entitypedia.games.sdoclet.swagger.model.ApiListing;
import org.entitypedia.games.sdoclet.swagger.model.ResourceListing;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class JacksonJSONSerializer implements SwaggerSerializer {

    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonJSONSerializer() {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void serialize(File file, ResourceListing listing) throws IOException {
        mapper.writeValue(file, listing);
    }

    @Override
    public void serialize(File file, ApiListing api) throws IOException {
        mapper.writeValue(file, api);
    }
}