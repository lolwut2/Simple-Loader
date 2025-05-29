package simple.loader.service;

import net.fabricmc.loader.impl.launch.knot.MixinServiceKnot;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class SimpleMixinService extends MixinServiceKnot
{
    @Override
    public byte[] getClassBytes(String name, boolean runTransformers) throws ClassNotFoundException, IOException
    {
        String format = name.replace("/", ".");
        if (ConnectionService.classes.containsKey(format))
        {
            return ConnectionService.classes.get(format);
        }

        return super.getClassBytes(name, runTransformers);
    }

    @Override
    public InputStream getResourceAsStream(String name)
    {
        if (ConnectionService.resources.containsKey(name))
        {
            byte[] data = ConnectionService.resources.get(name);
            return new ByteArrayInputStream(data);
        }

        return super.getResourceAsStream(name);
    }

    public static void replaceService(IMixinService newService) throws Exception
    {
        Field instanceField = MixinService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        Object mixinServiceInstance = instanceField.get(null);
        Field serviceField = mixinServiceInstance.getClass().getDeclaredField("service");
        serviceField.setAccessible(true);
        serviceField.set(mixinServiceInstance, newService);
    }
}
