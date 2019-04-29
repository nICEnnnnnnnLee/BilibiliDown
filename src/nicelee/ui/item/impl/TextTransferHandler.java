package nicelee.ui.item.impl;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.TransferHandler;

/**
 * https://codeday.me/bug/20181022/332533.html
 * 有做修改，用于 JEditorPane 中文本内容的复制
 */
public class TextTransferHandler extends TransferHandler{

	private static final long serialVersionUID = 9053062598699556138L;
	
	protected Transferable createTransferable(JComponent c) {
        final JEditorPane pane = (JEditorPane) c;
        final String plainText = pane.getSelectedText();
        return new TextTransferable(plainText);
    }


    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        if (action == COPY) {
            clip.setContents(this.createTransferable(comp), null);
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
    
}
