package simple.loader.url;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;

public class ByteBackedURLStreamHandler extends URLStreamHandler
{
    private final Map<String, byte[]> backingBytes;

    public ByteBackedURLStreamHandler(Map<String, byte[]> backingBytes)
    {
        this.backingBytes = backingBytes;
    }

    @Override
    protected URLConnection openConnection(URL u)
    {
        if (backingBytes.containsKey(u.getPath()))
        {
            return new ByteBackedURLConnection(u, backingBytes.get(u.getPath()));
        }

        return null;
    }
}