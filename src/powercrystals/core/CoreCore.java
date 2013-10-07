package powercrystals.core;

import java.io.File;
import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import powercrystals.core.oredict.OreDictTracker;
import powercrystals.core.updater.IUpdateableMod;
import powercrystals.core.updater.UpdateManager;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CoreCore extends DummyModContainer implements IUpdateableMod
{
	public static final String version = "1.6.2R1.1.8";
	public static final String modId = "PowerCrystalsCore";
	public static final String modName = "PowerCrystals Core";
	
	public static Property doUpdateCheck;
	public static Property doLivingDeath;
	
	public static CoreCore instance;

	public CoreCore()
	{
		super(new ModMetadata());
		ModMetadata md = super.getMetadata();
		md.modId = modId;
		md.name = modName;
		md.version = version.substring(version.indexOf('R') + 1);
		md.authorList = Arrays.asList("PowerCrystals", 
	            "AtomicStryker", 
	            "skyboy026");
		md.url = "http://minecraft.curseforge.com/mc-mods/powercrystalscore/";
		md.description = "Core functionality for Power Crystals' mods.";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent evt)
	{
		loadConfig(new File(evt.getModConfigurationDirectory().getAbsolutePath() +
				"/powercrystals/core/client.cfg"));
		loadServerConfig(new File(evt.getModConfigurationDirectory().getAbsolutePath() +
				"/powercrystals/core/server.cfg"));
	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt)
	{
		for(String s : OreDictionary.getOreNames())
		{
			for(ItemStack stack : OreDictionary.getOres(s))
			{
				OreDictTracker.registerOreDictEntry(stack, s);
			}
		}
		
		instance = this;
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@ForgeSubscribe
	public void registerOreEvent(OreRegisterEvent event)
	{
		OreDictTracker.registerOreDictEntry(event.Ore, event.Name);
	}

	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		if (!doLivingDeath.getBoolean(true) || event.entity.worldObj.isRemote ||
				!(event.entity instanceof EntityLiving) ||
				!((EntityLiving)event.entityLiving).hasCustomNameTag()) return;
		((WorldServer)event.entity.worldObj).getMinecraftServer().
			getConfigurationManager().sendChatMsg(event.entityLiving.func_110142_aN().func_94546_b());
	}
	
	@Subscribe
	public void load(FMLInitializationEvent evt)
	{
		TickRegistry.registerScheduledTickHandler(new UpdateManager(this), Side.CLIENT);
	}
	
	private void loadConfig(File f)
	{
		Configuration c = new Configuration(f);
		c.load();
		
		doUpdateCheck = c.get(Configuration.CATEGORY_GENERAL, "EnableUpdateCheck", true);
		doUpdateCheck.comment = "Set to false to disable update checks for all Power Crystals' mods.";
		
		c.save();
	}
	
	private void loadServerConfig(File f)
	{
		Configuration c = new Configuration(f);
		c.load();
		
		doLivingDeath = c.get(Configuration.CATEGORY_GENERAL, "EnableGenericDeathMessage", true);
		doLivingDeath.comment = "Set to true to display death messages for any named entity.";
		
		c.save();
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getModName()
	{
		return modName;
	}

	@Override
	public String getModVersion()
	{
		return version;
	}
}
