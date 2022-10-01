package project;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public interface Assembler {
    class DataPair {
        protected int address;
        protected int value;

        DataPair(int anAddress, int aValue) {
            address = anAddress;
            value = aValue;
        }

        @Override
        public String toString() {
            return "DataPair (" + address + ", " + value + ")";
        }
    }
    Set<String> noArgument = new TreeSet<>(Arrays.asList("HALT", "NOP", "NOT"));

    abstract int assemble(String inputFileName, String outputFileName, StringBuilder error);
}
