package org.entitypedia.games.sdoclet;

import org.entitypedia.games.sdoclet.swagger.model.ApiListing;
import org.entitypedia.games.sdoclet.swagger.model.ResourceListing;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href="https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public interface SwaggerSerializer {

    void serialize(File file, ResourceListing listing) throws IOException;

    void serialize(File file, ApiListing api) throws IOException;
}