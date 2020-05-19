package projectview;


import javax.swing.*;
import java.awt.*;
import project.*;



public class Mediator{
	private Machine machine;
	private TimerUnit tUnit;
	private CodeViewPanel codeViewPanel;
	MemoryViewPanel memoryViewPanel1;
	MemoryViewPanel memoryViewPanel2; 
	MemoryViewPanel memoryViewPanel3; 
	ControlPanel controlPanel; 
	ProcessorViewPanel processorPanel;
	private States currentState= States.NOTHING_LOADED;;
	private IOUnit ioUnit;
	MenuBarBuilder menuBuilder;


	private JFrame frame;
	public Machine getMachine() {
		return this.machine;
	}
	public void setMachine(Machine machine) {
		this.machine=machine;
	}
	public JFrame getFrame() {
		return this.frame;
	}
	
	public void step() { 
		if (currentState != States.PROGRAM_HALTED && 
				currentState != States.NOTHING_LOADED) {
			try {
				machine.step();
			} catch (CodeAccessException e) {
				JOptionPane.showMessageDialog(frame, 
					"Illegal access to code from line " + machine.getPC() + "\n"
							+ "Exception message: " + e.getMessage(),
							"Run time error",
							JOptionPane.OK_OPTION);
				System.out.println("Illegal access to code from line " + machine.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage()); // just for debugging			
			} catch(ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal index " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame, 
						"There is an argument with a null value " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(ParityCheckException e) {
				JOptionPane.showMessageDialog(frame, 
						"Parity Check error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(IllegalInstructionException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal instruction error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal argument error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame, 
						"Cannot divide by zero error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			}
			notify("");
		}
	}
	
	public void execute() {
		while (currentState != States.PROGRAM_HALTED && 
				currentState != States.NOTHING_LOADED) {
			try {
				machine.step();
			} catch (CodeAccessException e) {
				JOptionPane.showMessageDialog(frame, 
					"Illegal access to code from line " + machine.getPC() + "\n"
							+ "Exception message: " + e.getMessage(),
							"Run time error",
							JOptionPane.OK_OPTION);
				System.out.println("Illegal access to code from line " + machine.getPC()); // just for debugging
				System.out.println("Exception message: " + e.getMessage()); // just for debugging			
			} catch(ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal index " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame, 
						"There is an argument with a null value " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(ParityCheckException e) {
				JOptionPane.showMessageDialog(frame, 
						"Parity Check error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(IllegalInstructionException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal instruction error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal argument error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame, 
						"Cannot divide by zero error " + machine.getPC() + "\n"
								+ "Exception message: " + e.getMessage(),
								"Run time error",
								JOptionPane.OK_OPTION);
			}
			
		}
	notify("");
	}
	
	public void  reload() {
		tUnit.setAutoStepOn(false);
		clear();
		ioUnit.finalLoad_ReloadStep();
		
	}
	
	public void toggleAutoStep() {
		tUnit.toggleAutoStep();
		if(tUnit.isAutoStepOn()) {
			setCurrentState(States.AUTO_STEPPING);
		}
		else
			setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
	}
	
	public void assembleFile() {
		ioUnit.assembleFile();
	}
	
	public void loadFile() {
		ioUnit.loadFile();
	}
	
	public void setPeriod(int value) {
		tUnit.setPeriod(value);
	}
	
	public void setCurrentState(States s){
		currentState=s;
		if(s == States.PROGRAM_HALTED) tUnit.setAutoStepOn(false);		
		currentState = s;
		s.enter();
		notify("");
	}
	
	public States getCurrentState() {
		return currentState;
	}
	
	public void clear() {
		machine.clear();
		setCurrentState(States.NOTHING_LOADED);
		currentState.enter();
		notify("Clear");
	}
	
	private void notify(String str) {
		codeViewPanel.update(str);
		memoryViewPanel1.update(str);
		memoryViewPanel2.update(str);
		memoryViewPanel3.update(str);
		controlPanel.update();
		processorPanel.update();
	}
	
	public void exit() { // method executed when user exits the program
		int decision = JOptionPane.showConfirmDialog(
				frame, "Do you really wish to exit?",
				"Confirmation", JOptionPane.YES_NO_OPTION);
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	private void createAndShowGUI() {
		tUnit = new TimerUnit(this);
		ioUnit = new IOUnit(this);
		ioUnit.initialize();
		codeViewPanel = new CodeViewPanel(machine);
		memoryViewPanel1 = new MemoryViewPanel(machine, 0, 160);
		memoryViewPanel2 = new MemoryViewPanel(machine, 160, Memory.DATA_SIZE/2);
		memoryViewPanel3 = new MemoryViewPanel(machine, Memory.DATA_SIZE/2, Memory.DATA_SIZE);
		controlPanel = new ControlPanel(this);
		processorPanel = new ProcessorViewPanel(machine);
		menuBuilder = new MenuBarBuilder(this);
		frame = new JFrame("Simulator");
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		bar.add(menuBuilder.createFileMenu());
		bar.add(menuBuilder.createExecuteMenu());

		Container content = frame.getContentPane(); 
		content.setLayout(new BorderLayout(1,1));
		content.setBackground(Color.BLACK);
		frame.setSize(1200,600);
		frame.add(codeViewPanel.createCodeDisplay(), BorderLayout.LINE_START);
		frame.add(processorPanel.createProcessorDisplay(),BorderLayout.PAGE_START);
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,3));
		center.add(memoryViewPanel1.createMemoryDisplay());
		center.add(memoryViewPanel2.createMemoryDisplay());
		center.add(memoryViewPanel3.createMemoryDisplay());
		frame.add(center, BorderLayout.CENTER);
		frame.add(controlPanel.createControlDisplay(), BorderLayout.PAGE_END);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
		frame.setLocationRelativeTo(null);
		tUnit.start();
		currentState.enter();
		frame.setVisible(true);
		notify("");
	}
	
	public void makeReady(String s) {
		tUnit.setAutoStepOn(false);
		setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
		currentState.enter();
		notify(s);
	}
	
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Mediator mediator = new Mediator();
				Machine machine = 
					new Machine(() -> 
					mediator.setCurrentState(States.PROGRAM_HALTED));
				mediator.setMachine(machine);
				mediator.createAndShowGUI();
			}
		});
	}


}
