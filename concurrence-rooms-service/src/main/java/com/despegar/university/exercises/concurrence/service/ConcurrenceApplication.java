package com.despegar.university.exercises.concurrence.service;

import static com.despegar.university.exercises.concurrence.service.jetty.JettyThreadPoolExecutor.getThreadPoolExecutor;
import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.REQUEST;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.DispatcherServlet;

import com.despegar.library.api.httpfilters.CompressionFilter;
import com.despegar.library.routing.filter.RoutingFilter;
import com.despegar.university.exercises.concurrence.domain.exceptions.MainException;

public class ConcurrenceApplication {

    private static final Logger LOGGER = getLogger(ConcurrenceApplication.class);
    private static final String APP_CONTEXT = "classpath:application-context.xml";
    private static final String APP_PATH = "/concurrence-rooms/%s/*";

    public static void main(String[] args) throws Exception {
        try {

            if (args.length < 1) {
                throw new MainException("Port was not specified as first parameter.");
            }

            Properties props = new Properties();
            props.load(new ClassPathResource("conf/env/environment.properties").getInputStream());

            DispatcherServlet app = new DispatcherServlet();
            app.setContextConfigLocation(APP_CONTEXT);


            ServletContextHandler handler = getServletContextHandler(app, props);

            handler.addFilter(new FilterHolder(new RoutingFilter()), "/*", of(REQUEST));
            handler.addFilter(new FilterHolder(new CompressionFilter()), "/*", of(REQUEST));

            Server server = new Server(Integer.valueOf(args[0]));
            server.setHandler(handler);
            server.setThreadPool(new ExecutorThreadPool(getThreadPoolExecutor()));
            server.start();
            server.join();

        } catch (Exception e) {
            LOGGER.error("Oops! Error trying to startup: ", e);
        }
    }

    private static ServletContextHandler getServletContextHandler(DispatcherServlet app, Properties props) {
        ServletContextHandler handler = new ServletContextHandler(NO_SESSIONS);

        String teamName = (String) props.get("team.name");
        handler.addServlet(new ServletHolder(app), String.format(APP_PATH, teamName));

        return handler;
    }
}
