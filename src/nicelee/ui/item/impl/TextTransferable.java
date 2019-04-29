package nicelee.ui.item.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * https://codeday.me/bug/20181022/332533.html
 * 有做修改，用于 JEditorPane 中文本内容的复制
 */
public class TextTransferable implements Transferable{
	private static final DataFlavor[] supportedFlavors;

	static {
		try {
			supportedFlavors = new DataFlavor[] { new DataFlavor("text/plain;class=java.lang.String") };
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final String plainData;

	public TextTransferable(String plainData) {
        this.plainData = plainData;
    }

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor supportedFlavor : supportedFlavors) {
			if (supportedFlavor == flavor) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(supportedFlavors[0])) {
			return plainData;
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
