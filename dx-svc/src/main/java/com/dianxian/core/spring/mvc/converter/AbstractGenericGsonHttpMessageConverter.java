package com.dianxian.core.spring.mvc.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import com.dianxian.core.spring.mvc.method.annotation.GsonSerializer;
import com.dianxian.core.spring.mvc.method.annotation.ResponseBodyConvertContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter}
 * that can read and write JSON using the
 * <a href="https://code.google.com/p/google-gson/">Google Gson</a> library's
 * {@link Gson} class.
 *
 * <p>This converter can be used to bind to typed beans or untyped {@code HashMap}s.
 * By default, it supports {@code application/json} and {@code application/*+json} with
 * {@code UTF-8} character set.
 *
 * <p>Tested against Gson 2.6; compatible with Gson 2.0 and higher.
 *
 * @author Roy Clarkson
 * @since 4.1
 * @see #setGson
 * @see #setSupportedMediaTypes
 */
public abstract class AbstractGenericGsonHttpMessageConverter<T> extends AbstractGenericHttpMessageConverter<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Gson gson = new Gson();
    private String jsonPrefix;

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     */
    public AbstractGenericGsonHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON_UTF8, new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    /**
     * Set the {@code Gson} instance to use.
     * If not set, a default {@link Gson#Gson() Gson} instance is used.
     * <p>Setting a custom-configured {@code Gson} is one way to take further
     * control of the JSON serialization process.
     */
    public void setGson(Gson gson) {
        Assert.notNull(gson, "'gson' is required");
        this.gson = gson;
    }

    /**
     * Return the configured {@code Gson} instance for this converter.
     */
    public Gson getGson() {
        return this.gson;
    }

    /**
     * Specify a custom prefix to use for JSON output. Default is none.
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with ")]}', ".
     * Default is {@code false}.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON
     * Hijacking. The prefix renders the string syntactically invalid as a script
     * so that it cannot be hijacked.
     * This prefix should be stripped before parsing the string as JSON.
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        TypeToken<?> token = getTypeToken(clazz);
        return readTypeToken(token, inputMessage);
    }

    @Override
    public T read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(type);
        return readTypeToken(token, inputMessage);
    }

    /**
     * Return the Gson {@link TypeToken} for the specified type.
     * <p>The default implementation returns {@code TypeToken.get(type)}, but
     * this can be overridden in subclasses to allow for custom generic
     * collection handling. For instance:
     * <pre class="code">
     * protected TypeToken<?> getTypeToken(Type type) {
     *   if (type instanceof Class && List.class.isAssignableFrom((Class<?>) type)) {
     *     return new TypeToken<ArrayList<MyBean>>() {};
     *   }
     *   else {
     *     return super.getTypeToken(type);
     *   }
     * }
     * </pre>
     * @param type the type for which to return the TypeToken
     * @return the type token
     */
    protected TypeToken<?> getTypeToken(Type type) {
        return TypeToken.get(type);
    }

    private T readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
        Reader json = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
        try {
            return this.gson.fromJson(json, token.getType());
        }
        catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharSet() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharSet();
    }

    @Override
    protected void writeInternal(T o, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        Charset charset = getCharset(outputMessage.getHeaders());
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
        try {
            if (this.jsonPrefix != null) {
                writer.append(this.jsonPrefix);
            }
            ResponseBodyConvertContext convertContext = ResponseBodyConvertContext.getCurrentContext();
            // 防止内存泄漏，使用后就删除ThreadLocal
            ResponseBodyConvertContext.removeCurrentContext();
            Gson gson = this.getGson();
            Method method = convertContext.getReturnType().getMethod();
            if (null != method) {
                GsonSerializer gsonSerializer = method.getAnnotation(GsonSerializer.class);
                if (null != gsonSerializer) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    if (gsonSerializer.serializeNulls()) {
                        gsonBuilder.serializeNulls();
                    }
                    if (gsonSerializer.prettyPrinting()) {
                        gsonBuilder.setPrettyPrinting();
                    }
                    if (!gsonSerializer.escapeHtmlChars()) {
                        gsonBuilder.disableHtmlEscaping();
                    }
                    gson = gsonBuilder.create();
                }
            }
            if (type != null) {
                gson.toJson(o, type, writer);
            }
            else {
                gson.toJson(o, writer);
            }
            writer.close();
        }
        catch (JsonIOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
}