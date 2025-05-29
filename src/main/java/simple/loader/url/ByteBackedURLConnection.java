package simple.loader.url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class ByteBackedURLConnection extends URLConnection
{
    private final byte[] bytes;

    ByteBackedURLConnection(URL url, byte[] bytes)
    {
        super(url);
        this.bytes = bytes;
    }

    @Override
    public void connect() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(bytes);
    }
}