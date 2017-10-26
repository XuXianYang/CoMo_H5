package com.dianxian.core.swagger.jaxrs.ext;

import com.dianxian.core.swagger.parameters.*;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.*;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

public class DefaultParameterExtension extends AbstractSwaggerExtension {

    @Override
    public List<Parameter> extractParameters(Swagger swagger, List<Annotation> annotations, Type type, Set<Type> typesToSkip, Iterator<SwaggerExtension> chain) {
        if (shouldIgnoreType(type, typesToSkip)) {
            return new ArrayList<Parameter>();
        }

        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter parameter = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                QueryParam param = (QueryParam) annotation;
                QueryParameter qp = new QueryParameter()
                        .name(param.value());
                createProperty(type, qp, swagger);
//                Property schema = ModelConverters.getInstance().readAsProperty(type);
//                if (schema != null) {
//                    if (schema instanceof RefProperty) {
//                        qp.setSchema(PropertyBuilder.toModel(schema));
//                        for (Map.Entry<String, Model> entry : ModelConverters.getInstance().readAll(type).entrySet()) {
//                            swagger.addDefinition(entry.getKey(), entry.getValue());
//                        }
//                    } else {
//                        qp.setProperty(schema);
//                    }
//                }
                parameter = qp;
            } else if (annotation instanceof PathParam) {
                PathParam param = (PathParam) annotation;
                PathParameter pp = new PathParameter()
                        .name(param.value());
                createProperty(type, pp, swagger);
//                Property schema = createProperty(type);
//                if (schema != null) {
//                    pp.setProperty(schema);
//                }
                parameter = pp;
            } else if (annotation instanceof HeaderParam) {
                HeaderParam param = (HeaderParam) annotation;
                HeaderParameter hp = new HeaderParameter()
                        .name(param.value());
                createProperty(type, hp, swagger);
//                Property schema = createProperty(type);
//                if (schema != null) {
//                    hp.setProperty(schema);
//                }
                parameter = hp;
            } else if (annotation instanceof CookieParam) {
                CookieParam param = (CookieParam) annotation;
                CookieParameter cp = new CookieParameter()
                        .name(param.value());
                createProperty(type, cp, swagger);
//                Property schema = createProperty(type);
//                if (schema != null) {
//                    cp.setProperty(schema);
//                }
                parameter = cp;
            } else if (annotation instanceof FormParam) {
                FormParam param = (FormParam) annotation;
                FormParameter fp = new FormParameter()
                        .name(param.value());
                createProperty(type, fp, swagger);
//                Property schema = createProperty(type);
//                if (schema != null) {
//                    fp.setProperty(schema);
//                }
                parameter = fp;
            }
        }
        if (parameter != null) {
            parameters.add(parameter);
        }

        return parameters;
    }

    @Override
    protected boolean shouldIgnoreClass(Class<?> cls) {
        return cls.getName().startsWith("javax.ws.rs.");
    }

    private void createProperty(Type type, AbstractHttpParameter parameter, Swagger swagger) {
        Property schema = ModelConverters.getInstance().readAsProperty(type);
        if (schema != null) {
            if (schema instanceof RefProperty) {
                parameter.setSchema(PropertyBuilder.toModel(schema));
                for (Map.Entry<String, Model> entry : ModelConverters.getInstance().readAll(type).entrySet()) {
                    swagger.addDefinition(entry.getKey(), entry.getValue());
                }
            } else {
                parameter.setProperty(schema);
            }
        }
    }

    private Property createProperty(Type type) {
        return enforcePrimitive(ModelConverters.getInstance().readAsProperty(type), 0);
    }

    private Property enforcePrimitive(Property in, int level) {
        if (in instanceof RefProperty) {
            return new StringProperty();
        }
        if (in instanceof ArrayProperty) {
            if (level == 0) {
                final ArrayProperty array = (ArrayProperty) in;
                array.setItems(enforcePrimitive(array.getItems(), level + 1));
            } else {
                return new StringProperty();
            }
        }
        return in;
    }
}
