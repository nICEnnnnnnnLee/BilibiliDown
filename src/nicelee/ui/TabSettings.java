package nicelee.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import nicelee.ui.item.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import nicelee.bilibili.annotations.Config;
import nicelee.bilibili.util.ConfigUtil;
import nicelee.ui.item.MJButton;

public class TabSettings extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 302743425054589939L;
	final ImageIcon backgroundIcon = Global.backgroundImg;
	final static int LINE_HEIGHT = 32, H_GAP = 5, V_GAP = 5;
	JPanel jpContent;
	String searchContent = "";
	JTextField searchFiled;
	JButton btnSearch, btnClear;

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
		label.setOpaque(true);
		label.setText(text);
		label.setPreferredSize(new Dimension(width, height));
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		return label;
	}

	public void initUI() {
		JLabel tips = new JLabel("该面板仅用于辅助生成配置文件，配置在重启后方能生效！！");
		tips.setFont(this.getFont().deriveFont(30.0f));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, H_GAP, V_GAP));
		this.add(tips);
		this.add(createTextLabel(null, 450, LINE_HEIGHT));
		this.initSaveButton();
		this.initResetButton();
		this.initCloseButton(this);
		this.add(createTextLabel(null, 470, LINE_HEIGHT));

		this.initSearchItems();

		jpContent = new JPanel();
		jpContent.setOpaque(false);
		JScrollPane jpScorll = new JScrollPane(jpContent);
		jpScorll.setPreferredSize(new Dimension(1150, 540));
		jpScorll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpScorll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		jpScorll.setOpaque(false);
//		jpScorll.getViewport().setOpaque(false);
		this.add(jpScorll);
		updateSettingsUI();
	}

	private void initSearchItems() {
		JPanel searchp = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		searchp.setPreferredSize(new Dimension(1150, LINE_HEIGHT + 2));

		searchFiled = new JTextField();
		searchFiled.setPreferredSize(new Dimension(500, LINE_HEIGHT));
		searchp.add(searchFiled);
		searchFiled.addActionListener(this);

		btnSearch = new MJButton("筛选");
		Dimension size = new Dimension(60, LINE_HEIGHT);
		btnSearch.setPreferredSize(size);
		btnSearch.addActionListener(this);
		searchp.add(btnSearch);

		btnClear = new MJButton("清空");
		btnClear.setPreferredSize(size);
		btnClear.addActionListener(this);
		searchp.add(btnClear);

		searchp.add(createTextLabel(null, 20, LINE_HEIGHT));
		searchp.add(new JLabel("<-- 筛选/清空会恢复配置值，如有改动，请先保存修改再点击"));

		this.add(searchp);
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
						value = cm.getSelectedItem().toString();
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
					resetJPanel();
				} else {
					JOptionPane.showMessageDialog(null, "保存失败", "!", JOptionPane.ERROR_MESSAGE);
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
				resetJPanel();
			}
		});
		this.add(btn);
	}

	private void resetJPanel() {
		jpContent.removeAll();
		updateSettingsUI();
		jpContent.updateUI();
		jpContent.repaint();
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

	private void onContentChanged(JLabel label, String curValue, String sysValue) {
		if (curValue.equals(sysValue) || (curValue.isEmpty() && sysValue == null)) {
			label.setBackground(null);
		} else {
			label.setBackground(Color.PINK);
		}
	}

	private void updateSettingsUI() {
		int lineOunt = 0;
		final int totalWidth = 700, sBoxWidth = 80, txtValueWidth = 340, btnChooserWidth = 20;
		final Dimension sBoxDim = new Dimension(sBoxWidth, LINE_HEIGHT),
				txtValueDimWithoutbtnChooser = new Dimension(txtValueWidth, LINE_HEIGHT),
				txtValueDimWithbtnChooser = new Dimension(txtValueWidth - btnChooserWidth, LINE_HEIGHT),
				btnChooserDim = new Dimension(btnChooserWidth, LINE_HEIGHT);
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
					boolean visible = note.contains(searchContent) || key.toLowerCase().contains(searchContent)
							|| (selected != null && selected.toLowerCase().contains(searchContent));
					if (selects.length > 0) {
						JComboBox<String> selectCBox = new JComboBox<>(selects);
						selectCBox.setSelectedItem(selected);
						selectCBox.setPreferredSize(sBoxDim);
						selectCBox.addItemListener(new ItemListener() {

							@Override
							public void itemStateChanged(ItemEvent e) {
								if (e.getStateChange() == ItemEvent.SELECTED) {
									onContentChanged(name, e.getItem().toString(), selected);
								}
							}
						});
						JLabel blank = this.createTextLabel(null, totalWidth - sBoxWidth, LINE_HEIGHT);
						name.setVisible(visible);
						selectCBox.setVisible(visible);
						blank.setVisible(visible);
						jpContent.add(name);
						jpContent.add(selectCBox);
						jpContent.add(blank);
					} else {
						final boolean withBtnChooser = !config.pathType().isEmpty();
						JTextField value = new JTextField(selected);
						value.setPreferredSize(
								withBtnChooser ? txtValueDimWithbtnChooser : txtValueDimWithoutbtnChooser);
						value.addFocusListener(new FocusAdapter() {
							@Override
							public void focusLost(FocusEvent e) {
								onContentChanged(name, value.getText(), selected);
							}
						});
						name.setVisible(visible);
						value.setVisible(visible);
						jpContent.add(name);
						jpContent.add(value);

						int blankWidth = totalWidth - txtValueWidth;
						JLabel blank = this.createTextLabel(null, blankWidth, LINE_HEIGHT);
						blank.setVisible(visible);
						if (withBtnChooser) {
							boolean isFile = !config.pathType().startsWith("dir");
							JButton btnFileChooser = new JButton("...");
							btnFileChooser.setPreferredSize(btnChooserDim);
							btnFileChooser.addActionListener(evt -> {
								JFileChooser fChooser = new JFileChooser(value.getText());
//								fChooser.setCurrentDirectory(new File(value.getText()));
								fChooser.setFileSelectionMode(
										isFile ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
								fChooser.setDialogTitle(isFile ? "请选择文件" : "请选择文件夹");
								fChooser.setApproveButtonText("确定");
								int userSelection = fChooser.showOpenDialog(null);
								if (userSelection == JFileChooser.APPROVE_OPTION) {
									value.setText(fChooser.getSelectedFile().getAbsolutePath());
								}
							});
							btnFileChooser.setVisible(visible);
							JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
							p.add(btnFileChooser);
							p.add(blank);
							p.setVisible(visible);
							jpContent.add(p);
						} else {
							jpContent.add(blank);
						}
					}
					if (visible)
						lineOunt++;
				}
			}
		}
		jpContent.setPreferredSize(new Dimension(1100, (LINE_HEIGHT + V_GAP) * lineOunt + 10));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClear) {
			searchFiled.setText("");
		}
		searchContent = searchFiled.getText().toLowerCase();
		resetJPanel();

	}

}
