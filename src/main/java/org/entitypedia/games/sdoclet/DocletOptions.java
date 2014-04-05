package org.entitypedia.games.sdoclet;

import org.entitypedia.games.sdoclet.parser.AnnotationAwareTranslator;
import org.entitypedia.games.sdoclet.parser.FirstNotNullTranslator;
import org.entitypedia.games.sdoclet.parser.NameBasedTranslator;
import org.entitypedia.games.sdoclet.parser.Translator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href=https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class DocletOptions {

    public final static String SWAGGER_VERSION = "1.2";
    public final static List<String> PRODUCES = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(new String[] {"application/json"})));
    public final static List<String> CONSUMES = PRODUCES;
    public final static List<String> PROTOCOLS = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(new String[] {"http", "https"})));

    /**
     * The folder to output generated files.
     */
    public final static String DESTINATION_DIRECTORY = "-d";
    private File destinationDirectory;

    /**
     * The location (URL) of the documentation.
     */
    public final static String DOC_BASE_PATH = "-docBasePath";
    private String docBasePath = "http://localhost:8080";

    /**
     * The location (URL) of the API.
     */
    public final static String API_BASE_PATH = "-apiBasePath";
    private String apiBasePath = "http://localhost:8080";

    /**
     * The version of the documented API.
     */
    public final static String API_VERSION = "-apiVersion";
    private String apiVersion = "N/A";

    /**
     * API package name.
     */
    public final static String API_PACKAGE = "-apiPackage";
    private String apiPackage;

    /**
     * Package with controllers implementing API interfaces.
     */
    public final static String CONTROLLER_PACKAGE = "-controllerPackage";
    private String controllerPackage;

    /**
     * List of opaque (not parsed) types.
     */
    public final static String TYPES_TO_TREAT_AS_OPAQUE = "-typesToTreatAsOpaque";
    private List<String> typesToTreatAsOpaque;

    private SwaggerSerializer swaggerSerializer = new JacksonJSONSerializer();

    private Translator translator;

    public DocletOptions() {
        typesToTreatAsOpaque = new ArrayList<>();
        typesToTreatAsOpaque.add("org.joda.time.DateTime");
        typesToTreatAsOpaque.add("java.util.UUID");
        translator = new FirstNotNullTranslator()
                .addNext(new AnnotationAwareTranslator()
                        .ignore("com.fasterxml.jackson.annotation.JsonIgnore")
                        .element("com.fasterxml.jackson.annotation.JsonProperty", "value")
                        .rootElement("com.fasterxml.jackson.annotation.JsonRootName", "value")
                )
                .addNext(new NameBasedTranslator().ignore("serialVersionUID"));
    }

    public static DocletOptions parse(String[][] options) {
        DocletOptions parsedOptions = new DocletOptions();
        for (String[] option : options) {
            switch (option[0]) {
                case DESTINATION_DIRECTORY:
                    parsedOptions.destinationDirectory = new File(option[1]);
                    if (!parsedOptions.destinationDirectory.isDirectory()) {
                        throw new IllegalArgumentException(DESTINATION_DIRECTORY + " option requires a directory!");
                    }
                    break;
                case DOC_BASE_PATH:
                    parsedOptions.docBasePath = option[1];
                    break;
                case API_BASE_PATH:
                    parsedOptions.apiBasePath = option[1];
                    break;
                case API_VERSION:
                    parsedOptions.apiVersion = option[1];
                    break;
                case API_PACKAGE:
                    parsedOptions.apiPackage = option[1];
                    break;
                case CONTROLLER_PACKAGE:
                    parsedOptions.controllerPackage = option[1];
                    break;
                case TYPES_TO_TREAT_AS_OPAQUE:
                    parsedOptions.typesToTreatAsOpaque.addAll(asList(copyOfRange(option, 1, option.length)));
                    break;
            }
        }
        return parsedOptions;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public String getDocBasePath() {
        return docBasePath;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public SwaggerSerializer getSwaggerSerializer() {
        return swaggerSerializer;
    }

    public DocletOptions setSwaggerSerializer(SwaggerSerializer swaggerSerializer) {
        this.swaggerSerializer = swaggerSerializer;
        return this;
    }

    public String getApiPackage() {
        return apiPackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public List<String> getTypesToTreatAsOpaque() {
        return typesToTreatAsOpaque;
    }

    public Translator getTranslator() {
        return translator;
    }
}