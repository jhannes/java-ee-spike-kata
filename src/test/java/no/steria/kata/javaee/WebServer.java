package no.steria.kata.javaee;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

    public static void main(String[] args) throws Exception {
        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:enAnnenDatabase");
        dataSource.setUser("sa");
        new EnvEntry("jdbc/personDs", dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        Server server = new Server(8081);
        server.addHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        System.out.println("http://localhost:8081/");
    }

}