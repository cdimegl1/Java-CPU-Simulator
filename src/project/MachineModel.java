package project;

import static project.Instruction.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class MachineModel {
    public final Map<Integer, Consumer<Instruction>> ACTION = new TreeMap<>();
    private CPU cpu = new CPU();
    private Memory memory = new Memory();
    private HaltCallback callBack;

    public void setData(int i, int j) {
        memory.setData(i, j);
    }

    int[ ] getData(int min, int max) {
        return memory.getData(min, max);
    }

    int[ ] getData() {
        return memory.getData();
    }

    public int getData(int index) {
        return memory.getData(index);
    }

    public Instruction getCode(int index) {
        return memory.getCode(index);
    }

    public void setCode(int i, Instruction j) {
        memory.setCode(i, j);
    }

    public Instruction[] getCode() {
        return memory.getCode();
    }

    public Instruction[] getCode(int min, int max) {
        return memory.getCode(min, max);
    }

    public int getProgramSize() {
        return memory.getProgramSize();
    }

    public int getChangedDataIndex() {
        return memory.getChangedDataIndex();
    }

    public void setProgramSize(int i) {
        memory.setProgramSize(i);
    }

    public void clear() {
        memory.clearCode();
        memory.clearData();
        setPC(0);
        setAccum(0);
    }

    public void step() {
        try {
            Instruction instr = getCode(cpu.pc);
            Instruction.checkParity(instr);
            ACTION.get(instr.opcode / 8).accept(instr);
        } catch (Exception e) {
            halt();
            throw e;
        }
    }

    public int getPC() {
        return cpu.pc;
    }

    public int getAccum() {
        return cpu.accum;
    }

    public void setAccum(int i) {
        cpu.accum = i;
    }

    public void setPC(int i) {
        cpu.pc = i;
    }

    public void halt() {
        callBack.halt();
    }

    public MachineModel(HaltCallback halt) {
        callBack = halt;

        ACTION.put(OPCODES.get("NOP"), instr -> {
            int flags = instr.opcode & 6;
            if (flags != 0) {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("ADD"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum += memory.getData(instr.arg);
            } else if (flags == 2) {
                cpu.accum += instr.arg;
            } else if (flags == 4) {
                cpu.accum += memory.getData(memory.getData(instr.arg));
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("SUB"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum -= memory.getData(instr.arg);
            } else if (flags == 2) {
                cpu.accum -= instr.arg;
            } else if (flags == 4) {
                cpu.accum -= memory.getData(memory.getData(instr.arg));
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("MUL"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum *= memory.getData(instr.arg);
            } else if (flags == 2) {
                cpu.accum *= instr.arg;
            } else if (flags == 4) {
                cpu.accum *= memory.getData(memory.getData(instr.arg));
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("DIV"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                if (memory.getData(instr.arg) == 0) throw new DivideByZeroException("Can't divide by zero.");
                cpu.accum /= memory.getData(instr.arg);
            } else if (flags == 2) {
                if (instr.arg == 0) throw new DivideByZeroException("Can't divide by zero");
                cpu.accum /= instr.arg;
            } else if (flags == 4) {
                if (memory.getData(memory.getData(instr.arg)) == 0)
                    throw new DivideByZeroException("Can't divide by zero");
                cpu.accum /= memory.getData(memory.getData(instr.arg));
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("NOT"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum = cpu.accum == 0 ? 1 : 0;
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("HALT"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                halt();
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
        });

        ACTION.put(OPCODES.get("LOD"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum = memory.getData(instr.arg);
            } else if (flags == 2) {
                cpu.accum = instr.arg;
            } else if (flags == 4) {
                cpu.accum = memory.getData(memory.getData(instr.arg));
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("STO"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                memory.setData(instr.arg, cpu.accum);
            } else if (flags == 4) {
                memory.setData(memory.getData(instr.arg), cpu.accum);
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("AND"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum = (cpu.accum != 0 && memory.getData(instr.arg) != 0) ? 1 : 0;
            } else if (flags == 2) {
                cpu.accum = (cpu.accum != 0 && instr.arg != 0) ? 1 : 0;
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("JUMP"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.pc += instr.arg;
            } else if (flags == 2) {
                cpu.pc = instr.arg;
            } else if (flags == 4) {
                cpu.pc += memory.getData(instr.arg);
            } else if (flags == 6) {
                cpu.pc = memory.getData(instr.arg);
            }
        });

        ACTION.put(OPCODES.get("JMPZ"), instr -> {
            int flags = instr.opcode & 6;
            if (cpu.accum == 0) {
                if (flags == 0) {
                    cpu.pc += instr.arg;
                } else if (flags == 2) {
                    cpu.pc = instr.arg;
                } else if (flags == 4) {
                    cpu.pc += memory.getData(instr.arg);
                } else if (flags == 6) {
                    cpu.pc = memory.getData(instr.arg);
                }
            } else {
                cpu.pc++;
            }
        });

        ACTION.put(OPCODES.get("CMPL"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum = memory.getData(instr.arg) < 0 ? 1 : 0;
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });

        ACTION.put(OPCODES.get("CMPZ"), instr -> {
            int flags = instr.opcode & 6;
            if (flags == 0) {
                cpu.accum = memory.getData(instr.arg) == 0 ? 1 : 0;
            } else {
                String fString = "(" + (flags % 8 > 3 ? "1" : "0") + (flags % 4 > 1 ? "1" : "0") + ")";
                throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
            }
            cpu.pc++;
        });
    }

    private class CPU {
        private int accum;
        private int pc;
    }
}
