package com.dianxian.resource;

import com.dianxian.core.exception.BizLogicException;
import io.swagger.annotations.Api;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.hibernate.validator.constraints.Email;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XuWenHao on 4/11/2016.
 */
@Api(hidden = true)
@Path("helloworld")
public class HelloWorldResource {
    @Inject
    private javax.inject.Provider<ConfiguredValidator> validatorProvider;

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello World";
    }

    @GET
    @Path("error")
    @Produces("text/plain")
    public String exceptionMessage(@QueryParam("code") int code) {
        throw new BizLogicException(code, "123");
    }

    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@Min(1L) @NotNull @QueryParam("v") Long name, @Email String mail) {
        if (true) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("field", "userId");
            data.put("error", "null pointer");
            throw new BizLogicException(4001, "hello exception world", data);
        }
        return "OK";
    }

//    @NotNull
//    @QueryParam("x")
//    private String x;

    @Path("exception")
    @GET
    @Produces("text/plain")
    public String getExp(@NotNull @QueryParam("p") String p, @NotNull @PathParam("t") String t, @DefaultValue("xxx") @QueryParam("x") String x) {
        throw new RuntimeException("not ready");
    }

    public static class Param {
        @NotNull
        @QueryParam("p")
        private String p;

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }
    }

    @Path("bean")
    @GET
    @Produces("text/plain")
    public String beanValidation(@BeanParam Param p) {
//        validatorProvider.get().validateResourceAndInputParams(p, null, null);
        return "bbean";
    }

    public static class TestBean {
        public TestBean(String v) {

        }
    }

    @Path("postBean")
    @POST
    @Produces("text/plain")
    public String postBean(TestBean bean) {
        return "postBean";
    }
}
