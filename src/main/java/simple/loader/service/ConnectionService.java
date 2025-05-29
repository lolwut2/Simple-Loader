package simple.loader.service;

import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import simple.loader.url.ByteBackedURLStreamHandler;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConnectionService
{
    public static final Map<String, byte[]> classes = new HashMap<>();
    public static final Map<String, byte[]> raw = new HashMap<>();
    public static final Map<String, byte[]> resources = new HashMap<>();

    public static void load()
    {
        try
        {
            Socket socket = new Socket("", 0);
            DataInputStream istream = new DataInputStream(socket.getInputStream());
            DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
            ostream.writeUTF(HWID.getHWID());
            ostream.flush();

            try (ZipInputStream zipStream = new ZipInputStream(istream))
            {
                ZipEntry zipEntry;
                while ((zipEntry = zipStream.getNextEntry()) != null)
                {
                    String entryName = zipEntry.getName();
                    byte[] entryData = readStream(zipStream);
                    if (entryName.endsWith(".class"))
                    {
                        String className = entryName.replace('/', '.').replace(".class", "");
                        classes.put(className, entryData);
                        raw.put("/" + entryName, entryData);
                    }
                    else
                    {
                        resources.put(entryName, entryData);
                        if (!entryName.startsWith("/"))
                        {
                            resources.put("/" + entryName, entryData);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ClassLoader classLoader = FabricLauncherBase.getLauncher().getTargetClassLoader();
            Class<?> clazz = classLoader.getClass();
            try
            {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
                MethodHandle handle = lookup.findVirtual(clazz, "addUrlFwd", MethodType
                        .methodType(void.class, URL.class)).bindTo(classLoader);

                handle.invokeExact(new URL("simpleloader", // change this
                        null, -1, "/", new ByteBackedURLStreamHandler(raw)));
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }

            Mixins.addConfiguration("mixins.loadedmod.json");
        }
        catch (IOException | RuntimeException e)
        {
            e.printStackTrace();
        }
    }

    private static byte[] readStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1)
        {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    private static byte[] generateHWID()
    {
        try
        {
            MessageDigest hash = MessageDigest.getInstance("MD5");
            String s = System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("os.version")
                    + Runtime.getRuntime().availableProcessors() + System.getenv("PROCESSOR_IDENTIFIER")
                    + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432")
                    + System.getenv("NUMBER_OF_PROCESSORS");
            return hash.digest(s.getBytes());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Error("Algorithm wasn't found.", e);
        }
    }
}