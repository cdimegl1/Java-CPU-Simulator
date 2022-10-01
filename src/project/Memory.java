package project;

import java.util.Arrays;

public class Memory {
    public static final int DATA_SIZE = 512;
    public static final int CODE_SIZE = 256;
    private int[] data = new int[DATA_SIZE];
    private Instruction[] code = new Instruction[CODE_SIZE];
    private int changedDataIndex = -1;
    private int programSize = 0;

    int[] getData() {
        return data;
    }

    int getData(int index) {
        return data[index];
    }

    public int getChangedDataIndex() {
        return changedDataIndex;
    }

    void setData(int index, int value) {
        data[index] = value;
        changedDataIndex = index;
    }

    int[] getData(int min, int max) {
        return Arrays.copyOfRange(data, min, max);
    }

    void clearData() {
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = 0;
        }
        changedDataIndex = -1;
    }

    int getProgramSize() {
        return programSize;
    }

    Instruction[] getCode() {
        return code;
    }

    Instruction[] getCode(int min, int max) {
        return Arrays.copyOfRange(code, min, max);
    }

    Instruction getCode(int index) {
        return code[index];
    }

    void setCode(int index, Instruction value) {
        code[index] = value;
        programSize = Math.max(programSize, index);
    }

    void clearCode() {
        for(int i = 0; i < code.length; i++) {
            code[i] = null;
        }
        programSize = -1;
    }

    void setProgramSize(int pSize) {
        programSize = pSize;
    }
}
