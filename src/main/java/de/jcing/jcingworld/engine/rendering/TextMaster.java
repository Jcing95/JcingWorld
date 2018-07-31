package de.jcing.jcingworld.engine.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jcing.jcingworld.engine.GraphicsLoader;
import de.jcing.jcingworld.engine.font.FontType;
import de.jcing.jcingworld.engine.font.GUIText;
import de.jcing.jcingworld.engine.font.TextMeshData;
import de.jcing.jcingworld.engine.shading.font.FontRenderer;

public class TextMaster {
	private static GraphicsLoader loader;

	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;

	public static void init(GraphicsLoader theLoader) {
		//TODO: no static TextMaster. Decide on fonttype (pixelated or vector?)!
		renderer = new FontRenderer();
		loader = theLoader;
	}

	public static void render() {
		renderer.render(texts);
	}

	public static void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		// TODO: TEXT remove VAO and VBOS
		if (textBatch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}

	public static void cleanUp() {
		//TODO: textMaster.cleanUp() unused
		renderer.cleanUp();
	}
}
