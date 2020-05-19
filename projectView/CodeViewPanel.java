package projectview;

import project.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
public class CodeViewPanel {
	private Machine m;
	private Instruction instr;
	private JScrollPane scroller;
	private JTextField[] codeText=new JTextField[Memory.CODE_SIZE];
	private JTextField[] codeBinHex=new JTextField[Memory.CODE_SIZE];
	private int previousColor=-1;
	
	public CodeViewPanel(Machine m) {
		this.m=m;
	}
	public JComponent createCodeDisplay() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder(
		        BorderFactory.createLineBorder(Color.BLACK),
		        "Code Memory View",
		        TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		panel.setBorder(border);
		//make the three panels
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		
		JPanel numPanel=new JPanel();
		numPanel.setLayout(new GridLayout(0,1));
		
		JPanel textPanel=new JPanel();
		textPanel.setLayout(new GridLayout(0,1));
		JPanel hexPanel=new JPanel();
		
		hexPanel.setLayout(new GridLayout(0,1));
		
		
		
		

		innerPanel.add(numPanel, BorderLayout.LINE_START) ;
		innerPanel.add(textPanel, BorderLayout.CENTER); 
		innerPanel.add(hexPanel, BorderLayout.LINE_END);
	
		
		
		for(int i=0;i<Memory.CODE_SIZE;i++) {
			numPanel.add(new JLabel(i+": ", JLabel.RIGHT));
			
			codeText[i] = new JTextField(10);
			
			codeBinHex[i] = new JTextField(12);
			textPanel.add(codeText[i]); 
			hexPanel.add(codeBinHex[i]);
		}
		
		scroller =new JScrollPane(innerPanel);
		panel.add(scroller);
		return panel;
	}
	public void update(String arg) {
		
		if("Load Code".equals(arg)) {
			for(int i = 0; i < m.getCode().size(); i++) {
				instr = m.getCode(i);
				codeText[i].setText(instr.getText());
				codeBinHex[i].setText(instr.getBinHex());
			}	
			previousColor = m.getPC();			
			codeBinHex[previousColor].setBackground(Color.YELLOW);
			codeText[previousColor].setBackground(Color.YELLOW);
		} else if("Clear".equals(arg)) {
	        for(int i = 0; i < Memory.CODE_SIZE; i++) {
				codeText[i].setText("");
				codeBinHex[i].setText("");
			}	
			if(previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
				codeText[previousColor].setBackground(Color.WHITE);
				codeBinHex[previousColor].setBackground(Color.WHITE);
			}
			previousColor = -1;
		}		
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.WHITE);
			codeBinHex[previousColor].setBackground(Color.WHITE);
		}
		previousColor = m.getPC();
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.YELLOW);
			codeBinHex[previousColor].setBackground(Color.YELLOW);
		} 
		if(scroller != null && instr != null && m!= null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			int pc = m.getPC();
			if(pc >= 0 && pc < Memory.CODE_SIZE && codeBinHex[pc] != null) { 
				Rectangle bounds = codeBinHex[pc].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Machine machine = new Machine(()->System.exit(0));
		CodeViewPanel panel = new CodeViewPanel(machine);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createCodeDisplay());
		frame.setVisible(true);
		System.out.println(Loader.load(machine, new File("factorial.pexe")));
		panel.update("Load Code");
	}
	
	

}