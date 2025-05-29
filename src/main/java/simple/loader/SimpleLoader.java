package simple.loader;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import simple.loader.service.ConnectionService;
import simple.loader.service.SimpleMixinService;

import java.util.List;
import java.util.Set;

public class SimpleLoader implements IMixinConfigPlugin
{
    @Override
    public void onLoad(String s)
    {
        try
        {
            SimpleMixinService.replaceService(new SimpleMixinService());
            System.setProperty("mixin.service", SimpleMixinService.class.getName());
            ConnectionService.load();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRefMapperConfig()
    {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1)
    {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1)
    {

    }

    @Override
    public List<String> getMixins()
    {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo)
    {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo)
    {

    }
}