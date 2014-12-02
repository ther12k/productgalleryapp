package net.rizkyzulkarnaen.productgallery;

import java.io.Serializable;

public class Item implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1045476801069647140L;
	final String name;
    final int drawable;

    Item(String name, int drawable) {
        this.name = name;
        this.drawable = drawable;
    }
}
