package nicelee.ui.item;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public abstract class MJMenuWithRadioGroupBuilder {

	JMenu menu;

	ItemListener itemListener;
	JRadioButtonMenuItem[] menuItems;

	public MJMenuWithRadioGroupBuilder(String menuName, String... menuItemNames) {
		menu = new JMenu(menuName);
		menuItems = new JRadioButtonMenuItem[menuItemNames.length];
		for (int i = 0; i < menuItems.length; i++) {
			menuItems[i] = new JRadioButtonMenuItem(menuItemNames[i]);
		}
	}

	public MJMenuWithRadioGroupBuilder(String menuName, List<String> menuItemNames) {
		menu = new JMenu(menuName);
		menuItems = new JRadioButtonMenuItem[menuItemNames.size()];
		for (int i = 0; i < menuItems.length; i++) {
			menuItems[i] = new JRadioButtonMenuItem(menuItemNames.get(i));
		}
	}

	// 该函数在ItemListener注册之前调用，故而不会在初始化时触发
	public MJMenuWithRadioGroupBuilder withMenuItemSelected(int selectedIndex) {
		menuItems[selectedIndex].setSelected(true);
		return this;
	}

	public MJMenuWithRadioGroupBuilder withItemListener(ItemListener itemListener) {
		this.itemListener = itemListener;
		return this;
	}

	public JMenu build() {
		ButtonGroup g = new ButtonGroup();
		// 如果itemListener为空，则初始化
		if (itemListener == null) {
			itemListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						for (int i = 0; i < menuItems.length; i++) {
							JRadioButtonMenuItem menuItem = menuItems[i];
							if (menuItem == e.getSource()) {
								onItemSelected(i, menuItem);
								break;
							}
						}
					}

				}
			};
		}
		for (JRadioButtonMenuItem menuItem : menuItems) {
			menuItem.addItemListener(itemListener);
			g.add(menuItem);
			menu.add(menuItem);
		}
		init(menuItems);
		return menu;
	}

	public abstract void init(JRadioButtonMenuItem[] menuItems);

	public abstract void onItemSelected(int itemIndex, JRadioButtonMenuItem item);

}
