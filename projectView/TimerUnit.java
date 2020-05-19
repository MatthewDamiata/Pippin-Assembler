package projectview;

import javax.swing.Timer;

public class TimerUnit {
	private static final int TICK = 500;
	private boolean autoStepOn = false;
	private Timer timer;
	private Mediator mediator;
	
	TimerUnit(Mediator mediator){
		this.mediator=mediator;
	}
	
	boolean isAutoStepOn() {
		return autoStepOn;
	}
	void setAutoStepOn(boolean autoStepOn) {
		this.autoStepOn = autoStepOn;
	}
	
	void toggleAutoStep() {
		autoStepOn = !autoStepOn;
	}
	
	void setPeriod(int period) {
		timer.setDelay(period);
	}
	
	void start() {
		timer = new Timer(TICK, e -> {if(autoStepOn) mediator.step();});
		timer.start();
	}
}