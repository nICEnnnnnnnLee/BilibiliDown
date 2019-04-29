package nicelee.ui.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.ui.Global;

/**
 * 内嵌于主页 nicelee.ui.TabIndex
 * 
 * 舍弃，使用菜单栏FunctionMenuBar代替
 */
public class OperationPanel extends JPanel {
	private static final long serialVersionUID = -752743062676819407L;
	
	JComboBox<String> cbType; // 全部视频 / 第一个视频
	JComboBox<String> cbQn; // 清晰度
	/**
	 * 
	 */
	public OperationPanel() {
		super();
		initUI();
	}
	
	void initUI() {
		this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.setPreferredSize(new Dimension(355, 80));
		FlowLayout f=(FlowLayout)getLayout();
		f.setHgap(0);//水平间距
		f.setVgap(0);//组件垂直间距
		this.setLayout(f);
		
		JPanel jp1 = new JPanel();
		jp1.setPreferredSize(new Dimension(350, 36));
		
		
		JLabel label = new JLabel("关闭全部Tab");
		label.setPreferredSize(new Dimension(277, 36));
		JButton btn = new JButton("执行");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.index.closeAllVideoTabs();
			}
			
		});
		jp1.add(label);
		jp1.add(btn);
		this.add(jp1);
		
		JPanel jp2 = new JPanel();
		jp2.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.red));
		jp2.setPreferredSize(new Dimension(350, 36));
		JLabel label2 = new JLabel("下载策略");
		jp2.add(label2);
		cbType = new JComboBox<String>();
		cbType.addItem("全部");
		cbType.addItem("仅第一");
		cbType.setSelectedIndex(1);
		jp2.add(cbType);
		
		JLabel label3 = new JLabel("优先清晰度");
		jp2.add(label3);
		cbQn = new JComboBox<>();
		for(VideoQualityEnum item: VideoQualityEnum.values()) {
			cbQn.addItem(item.getQuality());
		}
		cbQn.setSelectedIndex(2);
		jp2.add(cbQn);
		
		JButton btnDownload = new JButton("执行");
		btnDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean downAll = cbType.getSelectedItem().toString().startsWith("全部");
				int qn = VideoQualityEnum.getQN(cbQn.getSelectedItem().toString());
				Global.index.downVideoTabs(downAll, qn);
			}
			
		});
		jp2.add(btnDownload);
		this.add(jp2);
	}
}
