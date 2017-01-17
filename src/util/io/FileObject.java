package util.io;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class FileObject
    extends AbstractIOObject
{
    private final File f;

    public FileObject(File f)
    {
        this.f = f;
    }

    public FileObject(String name)
    {
        this(new File(name));
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
        return f.lastModified();
    }
}
