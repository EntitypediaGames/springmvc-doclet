package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href="https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public interface Translator {

    OptionalName typeName(Type type);

    OptionalName fieldName(FieldDoc field);

    OptionalName methodName(MethodDoc method);

    class OptionalName {
        private final Status status;
        private final String name;

        private OptionalName(Status status, String name) {
            this.status = status;
            this.name = name;
        }

        public static OptionalName presentOrMissing(String name) {
            if (null != name && !"".equals(name)) {
                return new OptionalName(Status.PRESENT, name);
            } else {
                return new OptionalName(Status.MISSING, null);
            }
        }

        public static OptionalName ignored() {
            return new OptionalName(Status.IGNORED, null);
        }

        public String value() {
            return name;
        }

        public boolean isPresent() {
            return status == Status.PRESENT;
        }

        public boolean isMissing() {
            return status == Status.MISSING;
        }

        private static enum Status {
            PRESENT, IGNORED, MISSING
        }
    }
}