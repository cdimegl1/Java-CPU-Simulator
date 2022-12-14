package projectview;

import javax.swing.Timer;

public class StepControl {
    private static final int TICK = 500;
    private boolean autoStepOn = false;
    private Timer timer;
    private ViewMediator mediator;

    public StepControl(ViewMediator v) {
        mediator = v;
    }

    public boolean isAutoStepOn() {
        return autoStepOn;
    }

    public void setAutoStepOn(boolean autoStepOn) {
        this.autoStepOn = autoStepOn;
    }

    void toggleAutoStep() {
        autoStepOn = !autoStepOn;
    }

    void setPeriod(int period) {
        timer.setDelay(period);
    }

    void start() {
        timer = new Timer(TICK, e -> {
            if (autoStepOn) mediator.step();
        });
        timer.start();
    }
}
