package util.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public abstract class AbstractIOObject
{
    public static final AbstractIOObject NONE = new AbstractIOObject() {
        @Override
        public String getName() {return null;}

        @Override
        public AbstractIOObject[] listChildren() { return new AbstractIOObject[0];}

        @Override
        public InputStream getInputStream() throws IOException { throw new FileNotFoundException();}


        @Override
        public OutputStream getOutputStream() throws IOException { throw new FileNotFoundException();}

        @Override
        public boolean isLeave() { return true; }

        @Override
        public long size() { return 0; }

        @Override
        public Date lastAltered() { return new Date(0L);}

        @Override
        public boolean exists() { return false; }

        @Override
        public boolean delete() throws IOException { return false; }

        @Override
        public void create() throws IOException {throw new IOException("Null-File can't be created");}
    };

    public abstract String getName();

    public abstract AbstractIOObject[] listChildren();

    public abstract InputStream getInputStream()
            throws IOException;

    public abstract OutputStream getOutputStream()
            throws IOException;

    public abstract boolean isLeave();

    public abstract long size();

    public abstract Date lastAltered();

    public abstract boolean exists();

    public abstract boolean delete()
        throws IOException;

    public abstract void create()
        throws IOException;
}