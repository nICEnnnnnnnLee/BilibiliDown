package nicelee.ui.item;

import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import nicelee.ui.Global;

//实现JTextfield 的全选、复制、剪切、粘贴功能。
//实现Place Holder。
public class MJTextField extends JTextField implements MouseListener, KeyListener, FocusListener {

    private static final long serialVersionUID = 10110L;

    private JPopupMenu pop = null; // 弹出菜单

    private JMenuItem selectAll = null, copy = null, paste = null, cut = null; // 功能菜单

    private String placeHolder;
    public MJTextField() {
        super();
        placeHolder = "";
        init();
    }
    
    public MJTextField(String placeHolder) {
    	super(placeHolder);
    	this.setForeground(Color.GRAY);
    	this.placeHolder = placeHolder;
    	init();
    }

    private void init() {
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.addFocusListener(this);
        pop = new JPopupMenu();
        pop.add(selectAll = new JMenuItem("全选"));
        pop.add(copy = new JMenuItem("复制"));
        pop.add(paste = new JMenuItem("粘贴"));
        pop.add(cut = new JMenuItem("剪切"));
        selectAll.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        selectAll.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		action(e);
        	}
        });
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        this.add(pop);
    }

    /**
     * 菜单动作
     * 
     * @param e
     */
    public void action(ActionEvent e) {
        if (e.getSource() == copy) { // 复制
            this.copy();
        } else if (e.getSource() == paste) { // 粘贴
        	if(this.getText().equals(placeHolder)) {
        		this.setText("");
        	}
        	this.setForeground(Color.BLACK);
            this.paste();
        } else if (e.getSource() == cut) { // 剪切
            this.cut();
        } else if (e.getSource() == selectAll) { // 全选
            this.selectAll();
        }
    }

    public JPopupMenu getPop() {
        return pop;
    }

    public void setPop(JPopupMenu pop) {
        this.pop = pop;
    }

    /**
     * 剪切板中是否有文本数据可供粘贴
     * 
     * @return true为有文本数据
     */
    public boolean isClipboardString() {
        boolean b = false;
        Clipboard clipboard = this.getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        try {
            if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
                b = true;
            }
        } catch (Exception e) {
        }
        return b;
    }

    /**
     * 文本组件中是否具备复制的条件
     * 
     * @return true为具备
     */
    public boolean isCanCopy() {
        boolean b = false;
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start != end)
            b = true;
        return b;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            copy.setEnabled(isCanCopy());
            paste.setEnabled(isClipboardString());
            cut.setEnabled(isCanCopy());
            pop.show(this, e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			System.out.println("输入了Enter键");
			Global.index.search();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().equals(placeHolder)) {
			this.setText("");
        }
		this.setForeground(Color.BLACK);
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			this.setForeground(Color.GRAY);
			this.setText(placeHolder);
        }
		
	}

}