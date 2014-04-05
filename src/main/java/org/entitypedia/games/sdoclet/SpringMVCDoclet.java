package org.entitypedia.games.sdoclet;

import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.entitypedia.games.sdoclet.parser.SpringMCVParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> To start the doclet, pass
 * <code>-doclet</code> followed by the fully-qualified
 * name of the starting class on the javadoc tool command line.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see com.sun.javadoc.Doclet
 * @see <a href=https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class SpringMVCDoclet {

    private final static Map<String, Integer> options = new HashMap<String, Integer>();

    static {
        options.put(DocletOptions.DESTINATION_DIRECTORY, 2);
        options.put(DocletOptions.DOC_BASE_PATH, 2);
        options.put(DocletOptions.API_BASE_PATH, 2);
        options.put(DocletOptions.API_VERSION, 2);
        options.put(DocletOptions.API_PACKAGE, 2);
        options.put(DocletOptions.CONTROLLER_PACKAGE, 2);
    }


    /**
     * Generate documentation here.
     * This method is required for all doclets.
     *
     * @return true on success.
     */
    public static boolean start(RootDoc doc) throws IOException {
        DocletOptions options = DocletOptions.parse(doc.options());
        return new SpringMCVParser(options, doc).run();
    }

    /**
     * Check for doclet-added options.  Returns the number of
     * arguments you must specify on the command line for the
     * given option.  For example, "-d docs" would return 2.
     * <p/>
     * This method is required if the doclet contains any options.
     * If this method is missing, Javadoc will print an invalid flag
     * error for every option.
     *
     * @return number of arguments on the command line for an option
     * including the option name itself.  Zero return means
     * option not known.  Negative value means error occurred.
     */
    public static int optionLength(String option) {
        Integer value = options.get(option);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    /**
     * Return the version of the Java Programming Language supported
     * by this doclet.
     * <p/>
     * This method is required by any doclet supporting a language version
     * newer than 1.1.
     *
     * @return the language version supported by this doclet.
     * @since 1.5
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}