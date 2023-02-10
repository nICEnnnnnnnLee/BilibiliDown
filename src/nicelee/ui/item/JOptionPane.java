package nicelee.ui.item;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * 处理 String类型的msg，将其转化为可以选择文本的JTextArea，而不是JLabel
 *
 */
public class JOptionPane {

	public static final int PLAIN_MESSAGE = -1;
	public static final int ERROR_MESSAGE = 0;
	public static final int INFORMATION_MESSAGE = 1;
	public static final int WARNING_MESSAGE = 2;

	public static final int YES_NO_OPTION = 0;

	public static JLabel template;

	private static Font defaultFont() {
		if (template == null) {
			template = new JLabel();
		}
		return template.getFont();
	}

	private static Color defaultColor() {
		if (template == null) {
			template = new JLabel();
		}
		return template.getBackground();
	}

	private static JTextArea ofMsg(String msg) {
		JTextArea ta = new JTextArea(msg);
		ta.setBackground(defaultColor());
		ta.setFont(defaultFont());
		ta.setEditable(false);
		return ta;
	}

	public static void showMessageDialog(Component parentComponent, String message) throws HeadlessException {
		javax.swing.JOptionPane.showMessageDialog(parentComponent, ofMsg(message));
	}

	public static void showMessageDialog(Component parentComponent, String message, String title, int messageType)
			throws HeadlessException {
		javax.swing.JOptionPane.showMessageDialog(parentComponent, ofMsg(message), title, messageType);
	}

	public static int showOptionDialog(Component parentComponent, String message, String title, int optionType,
			int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException {
		return javax.swing.JOptionPane.showOptionDialog(parentComponent, ofMsg(message), title, optionType, messageType,
				icon, options, initialValue);
	}
}
