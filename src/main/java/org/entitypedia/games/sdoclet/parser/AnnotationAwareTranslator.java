package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href=https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class AnnotationAwareTranslator implements Translator {

    private final Map<OptionalName, Type> reverseIndex;
    private final Map<Type, OptionalName> namedTypes;

    private String ignore;
    private String element;
    private String elementProperty;
    private String rootElement;
    private String rootElementProperty;

    public AnnotationAwareTranslator() {
        reverseIndex = new HashMap<>();
        namedTypes = new HashMap<>();
    }

    public AnnotationAwareTranslator ignore(String qualifiedAnnotationType) {
        this.ignore = qualifiedAnnotationType;
        return this;
    }

    public AnnotationAwareTranslator element(String qualifiedAnnotationType, String property) {
        this.element = qualifiedAnnotationType;
        this.elementProperty = property;
        return this;
    }

    public AnnotationAwareTranslator rootElement(String qualifiedAnnotationType, String property) {
        this.rootElement = qualifiedAnnotationType;
        this.rootElementProperty = property;
        return this;
    }

    @Override
    public OptionalName typeName(Type type) {
        if (namedTypes.containsKey(type)) {
            return namedTypes.get(type);
        }
        if (AnnotationHelper.isPrimitive(type) || type.asClassDoc() == null) {
            return null;
        }

        OptionalName name = nameFor(rootElement, rootElementProperty, type.asClassDoc());
        if (name.isPresent()) {
            StringBuilder nameBuilder = new StringBuilder(name.value());
            while (reverseIndex.containsKey(name)) {
                nameBuilder.append('_');
                name = OptionalName.presentOrMissing(nameBuilder.toString());
            }
            namedTypes.put(type, name);
            reverseIndex.put(name, type);
        }
        return name;
    }

    @Override
    public OptionalName fieldName(FieldDoc field) {
        return nameFor(element, elementProperty, field);
    }

    @Override
    public OptionalName methodName(MethodDoc method) {
        return nameFor(element, elementProperty, method);
    }

    private OptionalName nameFor(String annotation, String property, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(ignore)) {
            return OptionalName.ignored();
        }
        return OptionalName.presentOrMissing(element.getAnnotationValue(annotation, property));
    }
}