package powercrystals.core.gui;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenBase extends GuiContainer
{
	private List<Control> _controls = new LinkedList<Control>();
	private String _backgroundTexture;

	public GuiScreenBase(Container container, String backgroundTexture)
	{
		super(container);
		_backgroundTexture = backgroundTexture;
	}
	
	protected void addControl(Control control)
	{
		_controls.add(control);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
	{
		mouseX -= guiLeft;
		mouseY -= guiTop;
		int texture = mc.renderEngine.getTexture(_backgroundTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		for(Control c : _controls)
		{
			if(c.getVisible())
			{
				c.drawBackground(mouseX, mouseY, gameTicks);
			}
		}
		GL11.glPopMatrix();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		mouseX -= guiLeft;
		mouseY -= guiTop;
		for(Control c : _controls)
		{
			if(c.getVisible())
			{
				c.drawForeground(mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();
		int mouseX = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;
		int wheelMovement = Mouse.getEventDWheel();
		
		if(wheelMovement == 0)
		{
			return;
		}
		
		for(int i = _controls.size() - 1; i >= 0; i--)
		{
			Control c = _controls.get(i);
			if(!c.isPointInBounds(mouseX, mouseY) || !c.visible || !c.enabled)
			{
				continue;
			}
			if(c.onMouseWheel(mouseX, mouseY, wheelMovement))
			{
				return;
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		mouseX -= guiLeft;
		mouseY -= guiTop;
		
		for(int i = _controls.size() - 1; i >= 0; i--)
		{
			Control c = _controls.get(i);
			if(!c.isPointInBounds(mouseX, mouseY) || !c.visible || !c.enabled)
			{
				continue;
			}
			if(c.onMouseClicked(mouseX, mouseY, mouseButton))
			{
				return;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char characterTyped, int keyPressed)
	{
		for(int i = _controls.size() - 1; i >= 0; i--)
		{
			Control c = _controls.get(i);
			if(!c.visible || !c.enabled)
			{
				continue;
			}
			if(c.onKeyTyped(characterTyped, keyPressed))
			{
				return;
			}
		}
		super.keyTyped(characterTyped, keyPressed);
	}

	protected boolean isPointInArea(int x1, int y1, int width, int height, int px, int py)
	{
		return px >= x1 - 1 && px < x1 + width + 1 && py >= y1 - 1 && py < y1 + height + 1;
	}
}
