package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href=https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class NameBasedTranslator implements Translator {

    private String ignore;

    public NameBasedTranslator ignore(String name) {
        this.ignore = name;
        return this;
    }

    @Override
    public OptionalName typeName(Type type) {
        return nameFor(AnnotationHelper.typeOf(type));
    }

    @Override
    public OptionalName fieldName(FieldDoc field) {
        return nameFor(field.name());
    }

    @Override
    public OptionalName methodName(MethodDoc method) {
        String name = null;
        if (method.name().startsWith("get") && method.name().length() > 3) {
            name = method.name().substring(3);
            name = name.substring(0, 1).toLowerCase() + (name.length() > 1 ? name.substring(1) : "");
        }
        return nameFor(name);
    }

    private OptionalName nameFor(String name) {
        if (ignore.equals(name)) {
            return OptionalName.ignored();
        }
        return OptionalName.presentOrMissing(name);
    }
}