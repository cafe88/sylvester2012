import processing.core.PApplet;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

	class LayerBlend {
		String name;
		GLTextureFilter filter;

		LayerBlend(PApplet Parent, String Name, String XmlFile) {
			name = Name;
			filter = new GLTextureFilter(Parent, XmlFile);
		}

		void apply(GLTexture bottomLayer, GLTexture topLayer,
				GLTexture resultLayer) {
			filter.apply(new GLTexture[] { bottomLayer, topLayer }, resultLayer); // all
																					// are
																					// called
																					// the
																					// same
																					// way
		}
	}