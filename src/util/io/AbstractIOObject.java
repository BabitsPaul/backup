package util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public abstract class AbstractIOObject
{
    public abstract String getName();

    public abstract AbstractIOObject[] listChildren();

    public abstract InputStream getInputStream()
            throws IOException;

    public abstract OutputStream getOutputStream()
            throws IOException;

    public abstract boolean isLeave();

    public abstract long size();

    public abstract Date lastAltered();
}