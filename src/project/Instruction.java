package project;

import static java.util.Map.entry;

import java.util.Map;
import java.util.TreeMap;

public class Instruction {
    byte opcode;
    int arg;
    public static final Map<String, Integer> OPCODES = Map.ofEntries(
            entry("NOP", 0),
            entry("NOT", 1),
            entry("HALT", 2),
            entry("LOD", 3),
            entry("STO", 4),
            entry("ADD", 5),
            entry("SUB", 6),
            entry("MUL", 7),
            entry("DIV", 8),
            entry("AND", 9),
            entry("JUMP", 10),
            entry("JMPZ", 11),
            entry("CMPL", 12),
            entry("CMPZ", 13)
    );
    public static final Map<Integer, String> MNEMONICS = new TreeMap<>();

    static {
        for (String str : OPCODES.keySet())
            MNEMONICS.put(OPCODES.get(str), str);
    }


    public Instruction(byte anOpCode, int anArg) {
        opcode = anOpCode;
        arg = anArg;
    }

    public static boolean noArgument(Instruction instr) {
        return instr.opcode < 24;
    }

    static int numOnes(int k) {
        String str = Integer.toUnsignedString(k, 2);
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') count++;
        }
        return count;
    }

    static void checkParity(Instruction instr) {
        if (numOnes(instr.opcode) % 2 != 0) throw new ParityCheckException("This instruction is corrupted.");
    }

    public String getText() {
        StringBuilder buff = new StringBuilder();
        buff.append(MNEMONICS.get(opcode / 8));
        buff.append("  ");
        int flags = opcode & 6;
        if (flags == 2) buff.append('#');
        if (flags == 4) buff.append('@');
        if (flags == 6) buff.append('&');
        buff.append(Integer.toString(arg, 16));
        return buff.toString().toUpperCase();
    }

    public String getBinHex() {
        StringBuilder buff = new StringBuilder();
        String str = "00000000" + Integer.toString(opcode, 2);
        buff.append(str.substring(str.length() - 8));
        buff.append("  ");
        buff.append(Integer.toHexString(arg));
        return buff.toString().toUpperCase();
    }

    @Override
    public String toString() {
        return "Instruction [" + Integer.toString(opcode, 2) + ", " + Integer.toString(arg, 16) + "]";
    }
}
