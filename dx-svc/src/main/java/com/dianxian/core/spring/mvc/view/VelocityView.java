package com.dianxian.core.spring.mvc.view;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class VelocityView extends VelocityLayoutView {


    @Override
    protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ViewToolContext velocityContext = new ViewToolContext(
                getVelocityEngine(), request, response, getServletContext());
        velocityContext.putAll(model);

        if (getToolboxConfigLocation() != null) {
            XmlFactoryConfiguration cfg = new XmlFactoryConfiguration();
            cfg.read(new ServletContextResource(getServletContext(), getToolboxConfigLocation()).getURL());
            ToolboxFactory factory = cfg.createFactory();

            if (factory.hasTools(Scope.REQUEST)) {
                velocityContext.addToolbox(factory.createToolbox(Scope.REQUEST));
            }
            if (factory.hasTools(Scope.APPLICATION)) {
                velocityContext.addToolbox(factory.createToolbox(Scope.APPLICATION));
            }
            if (factory.hasTools(Scope.SESSION)) {
                velocityContext.addToolbox(factory.createToolbox(Scope.SESSION));
            }
        }

        return velocityContext;
    }
}