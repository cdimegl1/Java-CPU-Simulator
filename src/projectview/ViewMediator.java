package projectview;

import project.*;

import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Color;

public class ViewMediator {
    private MachineModel model;
    private JFrame frame;
    private StepControl stepControl;
    private CodeViewPanel codeViewPanel;
    private MemoryViewPanel memoryViewPanel1;
    private MemoryViewPanel memoryViewPanel2;
    private MemoryViewPanel memoryViewPanel3;
    private ControlPanel controlPanel;
    private ProcessorViewPanel processorPanel;
    private States currentState = States.NOTHING_LOADED;
    private FilesMgr filesMgr;
    private MenuBarBuilder menuBuilder;

    public void step() {
        if (currentState != States.PROGRAM_HALTED && currentState != States.NOTHING_LOADED) {
            try {
                model.step();
            } catch (CodeAccessException e) {
                JOptionPane.showMessageDialog(frame, "Illegal access to code from line " + model.getPC() + "\n" + "Exception message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
                System.out.println("Illegal access to code from line " + model.getPC());
                System.out.println("Exception message: " + e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(frame, "Array Index Out of Bounds\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(frame, "Null Pointer\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (ParityCheckException e) {
                JOptionPane.showMessageDialog(frame, "Parity check failed on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (IllegalInstructionException e) {
                JOptionPane.showMessageDialog(frame, "Illegal instruction on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, "Illegal argument on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (DivideByZeroException e) {
                JOptionPane.showMessageDialog(frame, "Illegal division by zero on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            }
            notify("");
        }
    }

    public void clear() {
        model.clear();
        setCurrentState(States.NOTHING_LOADED);
        currentState.enter();
        notify("Clear");
        model.setProgramSize(0);
    }

    public void toggleAutoStep() {
        stepControl.toggleAutoStep();
        if (stepControl.isAutoStepOn()) {
            setCurrentState(States.AUTO_STEPPING);
        } else {
            setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
        }
    }

    public void reload() {
        stepControl.setAutoStepOn(false);
        clear();
        filesMgr.finalLoad_ReloadStep();
    }

    public void setPeriod(int value) {
        stepControl.setPeriod(value);
    }

    public void makeReady(String s) {
        stepControl.setAutoStepOn(false);
        setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
        currentState.enter();
        notify(s);
    }

    public void execute() {
        while (currentState != States.PROGRAM_HALTED && currentState != States.NOTHING_LOADED) {
            try {
                model.step();
            } catch (CodeAccessException e) {
                JOptionPane.showMessageDialog(frame, "Illegal access to code from line " + model.getPC() + "\n" + "Exception message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
                System.out.println("Illegal access to code from line " + model.getPC());
                System.out.println("Exception message: " + e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(frame, "Array Index Out of Bounds\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(frame, "Null Pointer\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (ParityCheckException e) {
                JOptionPane.showMessageDialog(frame, "Parity check failed on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (IllegalInstructionException e) {
                JOptionPane.showMessageDialog(frame, "Illegal instruction on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, "Illegal argument on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            } catch (DivideByZeroException e) {
                JOptionPane.showMessageDialog(frame, "Illegal division by zero on line " + model.getPC() + "\nException message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
            }
        }
        notify("");
    }

    public void assembleFile() {
        filesMgr.assembleFile();
    }

    public void loadFile() {
        filesMgr.loadFile();
    }

    public void exit() {
        int decision = JOptionPane.showConfirmDialog(frame, "Do you really wish to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == JOptionPane.YES_OPTION) {
            System.exit(0);
        }

    }

    public States getCurrentState() {
        return currentState;
    }

    public void setCurrentState(States s) {
        if (s == States.PROGRAM_HALTED) stepControl.setAutoStepOn(false);
        currentState = s;
        s.enter();
        notify("");
    }

    private void createAndShowGUI() {
        stepControl = new StepControl(this);
        filesMgr = new FilesMgr(this);
        filesMgr.initialize();
        codeViewPanel = new CodeViewPanel(model);
        memoryViewPanel1 = new MemoryViewPanel(model, 0, 160);
        memoryViewPanel2 = new MemoryViewPanel(model, 160, Memory.DATA_SIZE / 2);
        memoryViewPanel3 = new MemoryViewPanel(model, Memory.DATA_SIZE / 2, Memory.DATA_SIZE);
        controlPanel = new ControlPanel(this);
        processorPanel = new ProcessorViewPanel(model);
        menuBuilder = new MenuBarBuilder(this);
        frame = new JFrame("Simulator");
        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);
        bar.add(menuBuilder.createFileMenu());
        bar.add(menuBuilder.createExecuteMenu());
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout(1, 1));
        content.setBackground(Color.BLACK);
        frame.setSize(1200, 600);
        frame.add(codeViewPanel.createCodeDisplay(), BorderLayout.LINE_START);
        frame.add(processorPanel.createProcessorDisplay(), BorderLayout.PAGE_START);
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1, 3));
        center.add(memoryViewPanel1.createMemoryDisplay());
        center.add(memoryViewPanel2.createMemoryDisplay());
        center.add(memoryViewPanel3.createMemoryDisplay());
        frame.add(center, BorderLayout.CENTER);
        frame.add(controlPanel.createControlDisplay(), BorderLayout.PAGE_END);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
        frame.setLocationRelativeTo(null);
        stepControl.start();
        getCurrentState().enter();
        notify("");
        frame.setVisible(true);
    }

    private void notify(String str) {
        codeViewPanel.update(str);
        memoryViewPanel1.update(str);
        memoryViewPanel2.update(str);
        memoryViewPanel3.update(str);
        controlPanel.update();
        processorPanel.update();
    }

    public MachineModel getModel() {
        return model;
    }

    public void setModel(MachineModel model) {
        this.model = model;
    }

    public JFrame getFrame() {
        return frame;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewMediator mediator = new ViewMediator();
            MachineModel model = new MachineModel(() -> mediator.setCurrentState(States.PROGRAM_HALTED));
            mediator.setModel(model);
            mediator.createAndShowGUI();
        });
    }
}
