package org.block.api.load;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.block.api.BlockAPI;
import org.block.api.CustomBlock;

public class BlockLoader
{
	public static final File PATH = new File("./plugins/BlockAPI/Blocks/");

	public static List<CustomBlock> load(BlockAPI plugin) {
		ArrayList<CustomBlock> list = new ArrayList<CustomBlock>();
		if (!PATH.exists()) {
			PATH.mkdirs();
			return list;
		}
		BlockClassLoader classLoader = new BlockClassLoader(plugin.getClass().getClassLoader());
		File[] classes = PATH.listFiles();
		for (int x = 0; x < classes.length; x++) {
			File file = classes[x];
			if ((!file.isDirectory()) && (file.getAbsolutePath().endsWith(".class")))
				try
			{
					Class<?> myClass = classLoader.loadClass("file:" + file.getAbsolutePath(), file.getName().replace(".class", ""));
					Object object = myClass.newInstance();
					if ((object instanceof CustomBlock)) {
						CustomBlock cblock = (CustomBlock)object;
						list.add(cblock);
					}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}