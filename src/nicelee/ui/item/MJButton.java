package nicelee.ui.item;


import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

import nicelee.ui.Global;

public class MJButton extends JButton {

	private static final long serialVersionUID = -2872601692608872176L;

	public MJButton() {
		super();
		setUI(Global.btnStyle);
	}

	public MJButton(String text) {
		super(text);
		setUI(Global.btnStyle);
	}

	public MJButton(Icon icon) {
		super(icon);
		setUI(Global.btnStyle);
	}

	void setUI(boolean isSet) {
		if (isSet) {
			this.setUI(new BasicButtonUI());
		}
	}
}