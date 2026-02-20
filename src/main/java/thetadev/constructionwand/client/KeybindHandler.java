package thetadev.constructionwand.client;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.ConfigClient;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;

public class KeybindHandler {
    public static final KeyMapping KEY_OPT = new KeyMapping(getKey("wand_option"), GLFW.GLFW_KEY_LEFT_CONTROL, getKey("category"));

    private static String getKey(String name) {
		return String.join(".", "key", ConstructionWand.MODID, name);
	}

	public KeybindHandler() {
        leftShiftPressed = false;
        optPressed = false;
	}

    private boolean leftShiftPressed;
    private boolean optPressed;

    @SubscribeEvent
    public void KeyEvent(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        if(WandUtil.holdingWand(player) == null) return;

        boolean optState = isOptKeyDown();
        boolean leftShiftState = isLeftShiftKeyDown();
        if(optPressed != optState || leftShiftPressed != leftShiftState) {
            optPressed = optState;
            leftShiftPressed = leftShiftState;
            PacketQueryUndo packet = new PacketQueryUndo(optPressed, leftShiftPressed);
            ConstructionWand.instance.HANDLER.sendToServer(packet);
            //ConstructionWand.LOGGER.debug("OPT key update: " + optPressed);
        }
    }

    // Sneak+(OPT)+Scroll to change direction lock
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void MouseScrollEvent(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        double scroll = event.getScrollDelta();

        if(!modeKeyCombDown() || scroll == 0) return;

        ItemStack wand = WandUtil.holdingWand(player);
        if(wand == null) return;

        WandOptions wandOptions = new WandOptions(wand);
        wandOptions.lock.next(scroll < 0);
        ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(wandOptions.lock, true));
        event.setCanceled(true);
    }

    // Sneak+(OPT)+Left click wand to change core
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();

        if(!modeKeyCombDown()) return;

        ItemStack wand = event.getItemStack();
        if(!(wand.getItem() instanceof ItemWand)) return;

        WandOptions wandOptions = new WandOptions(wand);
        wandOptions.cores.next();
        ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(wandOptions.cores, true));
    }

    // Sneak+(OPT)+Right click wand to open GUI
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isServer()) return;

        if(!guiKeyCombDown()) return;

        ItemStack wand = event.getItemStack();
        if(!(wand.getItem() instanceof ItemWand)) return;

        Minecraft.getInstance().setScreen(new ScreenWand(wand));
        event.setCanceled(true);
    }

    public static boolean isLeftShiftKeyDown() {
        return Minecraft.getInstance().options.keyShift.isDown();
    }

    public static boolean isOptKeyDown() {
        return KEY_OPT.isDown();
    }

    public static boolean modeKeyCombDown() {
        return isOptKeyDown() && (isLeftShiftKeyDown() || !ConfigClient.SHIFTOPT_MODE.get());
    }

    public static boolean guiKeyCombDown() {
        return isOptKeyDown() && (isLeftShiftKeyDown() || !ConfigClient.SHIFTOPT_GUI.get());
    }
}
