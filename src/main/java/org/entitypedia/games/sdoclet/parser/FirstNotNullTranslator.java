package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href="https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class FirstNotNullTranslator implements Translator {

    private final List<Translator> chain;

    public FirstNotNullTranslator() {
        chain = new ArrayList<Translator>();
    }

    public FirstNotNullTranslator addNext(Translator link) {
        chain.add(link);
        return this;
    }

    @Override
    public OptionalName typeName(final Type type) {
        OptionalName name = null;
        Iterator<Translator> iterator = chain.iterator();
        while ((name == null || name.isMissing()) && iterator.hasNext()) {
            name = iterator.next().typeName(type);
        }
        return name;
    }

    @Override
    public OptionalName fieldName(final FieldDoc field) {
        OptionalName name = null;
        Iterator<Translator> iterator = chain.iterator();
        while ((name == null || name.isMissing()) && iterator.hasNext()) {
            name = iterator.next().fieldName(field);
        }
        return name;
    }

    @Override
    public OptionalName methodName(final MethodDoc method) {
        OptionalName name = null;
        Iterator<Translator> iterator = chain.iterator();
        while ((name == null || name.isMissing()) && iterator.hasNext()) {
            name = iterator.next().methodName(method);
        }
        return name;
    }
}