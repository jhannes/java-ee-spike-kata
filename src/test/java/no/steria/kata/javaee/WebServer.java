package no.steria.kata.javaee;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;

public class WebServer {

    public static void main(String[] args) throws Exception {
        String jndiDataSource = "jdbc/personDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:demo");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "update");

        Server server = new Server(8088);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        int localPort = server.getConnectors()[0].getLocalPort();
        String baseUrl = "http://localhost:" + localPort + "/";
        System.out.println(baseUrl);
    }
}
