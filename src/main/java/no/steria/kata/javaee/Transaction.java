package no.steria.kata.javaee;

import java.io.Closeable;

public interface Transaction extends Closeable {

    void setCommit();

    @Override
    public void close();

}
