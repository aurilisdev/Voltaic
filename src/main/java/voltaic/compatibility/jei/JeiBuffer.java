package voltaic.compatibility.jei;

import net.minecraftforge.fml.ModList;

public class JeiBuffer {

	public static boolean isJeiInstalled() {
		return ModList.get().isLoaded("jei");
	}

}
