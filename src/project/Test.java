package project;

public class Test {
    public static void main(String[] args) {
        Instruction instr1 = new Instruction((byte)1, 4);
        Instruction instr2 = new Instruction((byte)3, 5);
        Instruction.checkParity(instr2);
        Instruction.checkParity(instr1);
    }
}
