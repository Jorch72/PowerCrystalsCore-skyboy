package powercrystals.core;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.6.2")
public class CoreLoader implements IFMLLoadingPlugin
{
	public static boolean runtimeDeobfEnabled = true;
	public static File pccLocation;
	
	@Override
	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "powercrystals.core.at.PCCAccessTransformer", "powercrystals.core.asm.PCCASMTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return "powercrystals.core.CoreCore";
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		runtimeDeobfEnabled = (Boolean)data.get("runtimeDeobfuscationEnabled");
		pccLocation = (File)data.get("coremodLocation");
	}
}
