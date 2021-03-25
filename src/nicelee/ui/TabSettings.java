package nicelee.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import nicelee.bilibili.annotations.Config;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.ui.item.MJButton;

public class TabSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 302743425054589939L;
	final ImageIcon backgroundIcon = Global.backgroundImg;
	final static int LINE_HEIGHT = 32;
	JPanel jpContent;

	private TabSettings() {
		initUI();
	}

	public static void openSettingTab() {
		JLabel label = new JLabel("设置页");
		JPanel panel = new TabSettings();
		Global.tabs.addTab("设置页", panel);
		Global.tabs.setTabComponentAt(Global.tabs.indexOfComponent(panel), label);
		Global.tabs.setSelectedComponent(panel);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2)
					Global.tabs.remove(panel);
				else
					Global.tabs.setSelectedComponent(panel);
			}
		});
	}

	private JLabel createTextLabel(String text, int width, int height) {
		JLabel label = new JLabel();
		if (text != null) {
			text = " " + text;
			label.setBorder(BorderFactory.createLineBorder(Color.RED));
		}
		label.setText(text);
		label.setPreferredSize(new Dimension(width, height));
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		return label;
	}

	public void initUI() {
		JLabel tips = new JLabel("该面板仅用于辅助生成配置文件，配置在重启后方能生效！！");
		tips.setFont(this.getFont().deriveFont(30.0f));
		this.add(tips);
		this.add(createTextLabel(null, 450, LINE_HEIGHT));
		this.initSaveButton();
		this.initResetButton();
		this.initCloseButton(this);
		this.add(createTextLabel(null, 470, LINE_HEIGHT));
		jpContent = new JPanel();
		jpContent.setOpaque(false);
		JScrollPane jpScorll = new JScrollPane(jpContent);
		jpScorll.setPreferredSize(new Dimension(1150, 580));
		jpScorll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpScorll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		jpScorll.setOpaque(false);
//		jpScorll.getViewport().setOpaque(false);
		this.add(jpScorll);
		updateSettingsUI();
	}

	private void initSaveButton() {
		JButton btnSaveSettings = new MJButton("保存");
		Dimension size = new Dimension(60, LINE_HEIGHT);
		btnSaveSettings.setPreferredSize(size);
		btnSaveSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 更新Global.settings
				Component[] comps = jpContent.getComponents();
				for (int i = 0; i < comps.length - 2; i += 3) {
					String key = ((JLabel) comps[i]).getToolTipText();
					String value;
					if (comps[i + 1] instanceof JComboBox) {
						JComboBox<?> cm = (JComboBox<?>) comps[i + 1];
						value = (String) cm.getSelectedItem();
					} else {
						JTextField filed = (JTextField) comps[i + 1];
						value = filed.getText().trim();
					}
					Global.settings.put(key, value);
					// System.out.printf("key: value -> %s: %s\r\n", key, value);
				}
				// 根据Global.settings 写配置文件
				if (ConfigUtil.saveConfig()) {
					JOptionPane.showMessageDialog(null, "保存成功", "OK", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "保存成功", "!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		this.add(btnSaveSettings);
	}

	private void initResetButton() {
		JButton btn = new MJButton("重置");
		btn.setPreferredSize(new Dimension(60, LINE_HEIGHT));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jpContent.removeAll();
				updateSettingsUI();
				jpContent.updateUI();
				jpContent.repaint();
			}
		});
		this.add(btn);
	}

	private void initCloseButton(JPanel panel) {
		JButton btn = new MJButton("关闭");
		btn.setPreferredSize(new Dimension(60, LINE_HEIGHT));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.tabs.remove(panel);
			}
		});
		this.add(btn);
	}

	private void updateSettingsUI() {
		int lineOunt = 0;
		for (Field field : Global.class.getDeclaredFields()) {
			Config config = field.getAnnotation(Config.class);
			if (config != null) {
				String note = config.note();
				if (!note.isEmpty()) {
					String key = config.key();
					String[] selects = config.valids();
					String selected = Global.settings.get(key);
					JLabel name = this.createTextLabel(note, 380, LINE_HEIGHT);
					name.setToolTipText(key);
					if (selects.length > 0) {
						JComboBox<String> selectCBox = new JComboBox<>(selects);
						int totalWidth = 700, width = 80;
						selectCBox.setSelectedItem(selected);
						selectCBox.setPreferredSize(new Dimension(width, LINE_HEIGHT));
						jpContent.add(name);
						jpContent.add(selectCBox);
						jpContent.add(this.createTextLabel(null, totalWidth - width, LINE_HEIGHT));
						lineOunt++;
					} else {
						JTextField value = new JTextField(selected);
						int totalWidth = 700, width = 340;
						value.setPreferredSize(new Dimension(width, LINE_HEIGHT));
						jpContent.add(name);
						jpContent.add(value);
						jpContent.add(this.createTextLabel(null, totalWidth - width, LINE_HEIGHT));
						lineOunt++;
					}
				}
			}
		}
		jpContent.setPreferredSize(new Dimension(1100, (LINE_HEIGHT + 4) * (lineOunt + 1)));
	}

}
