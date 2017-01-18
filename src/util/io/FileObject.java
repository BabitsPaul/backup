package util.io;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class FileObject
    extends AbstractIOObject
{
    //TODO transform to java.nio

    private final File f;

    public FileObject(String name)
    {
        this.f = new File(name);
    }

    @Override
    public String getName()
    {
        return f.getAbsolutePath();
    }

    @Override
    public AbstractIOObject[] listChildren()
    {
        return Arrays.stream(Optional.of(f.list()).orElse(new String[0])).
                    map(FileObject::new).
                    toArray(AbstractIOObject[]::new);
    }

    @Override
    public InputStream getInputStream()
            throws IOException
    {
        return new FileInputStream(f);
    }

    @Override
    public OutputStream getOutputStream()
            throws IOException
    {
        return new FileOutputStream(f);
    }

    @Override
    public boolean isLeave()
    {
        return f.isFile();
    }

    @Override
    public long size()
    {
        return f.length();
    }

    @Override
    public Date lastAltered() {
        return new Date(f.lastModified());
    }

    @Override
    public boolean exists() {
        return f.exists();
    }

    @Override
    public boolean delete() throws IOException {
        return f.delete();
    }
}
