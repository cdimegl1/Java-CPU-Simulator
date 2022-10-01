package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class Loader {
    public static String load(MachineModel model, File program) {
        if (model == null || program == null) return null;

        ByteBuffer buff = null;
        try (FileInputStream fStream = new FileInputStream(program);
             FileChannel fChan = fStream.getChannel()) {
            long fSize = fChan.size();
            buff = ByteBuffer.allocate((int) fSize);
            fChan.read(buff);
        } catch (FileNotFoundException e) {
            return ("File " + program.getName() + " Not Found");
        } catch (IOException e) {
            return ("Unexpected IO exception in loading " + program.getName());
        }

        if (buff != null) {
            buff.rewind();
            int codeIndex = 0;
            while (buff.hasRemaining()) {
                byte b = buff.get();
                if (b < 0) break;
                Instruction instr = new Instruction(b, 0);
                if (!Instruction.noArgument(instr)) {
                    instr = new Instruction(b, buff.getInt());
                }
                model.setCode(codeIndex++, instr);
            }
            while (buff.hasRemaining()) {
                model.setData(buff.getInt(), buff.getInt());
            }
            return "success";
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Enter the name of the file without extension: ");
        try (Scanner keyboard = new Scanner(System.in)) {
            String filename = keyboard.nextLine();
            MachineModel test = new MachineModel(() -> System.exit(0));
            System.out.println(Loader.load(test, new File(filename + ".pexe")));

            System.out.println("Code\n" +
                    "0 => " + test.getCode(0) +
                    "; 1 => " + test.getCode(1) +
                    "; 1 => " + test.getCode(2) +
                    "; 2 => " + test.getCode(3) +
                    "; 3 => " + test.getCode(4) +
                    "; 4 => " + test.getCode(5) +
                    "; 5 => " + test.getCode(6) +
                    "; 6 => " + test.getCode(7));


            while (true) {
                test.step();
                System.out.println("step " + test.getPC());
                System.out.println(
                        "0 => " + test.getData(0) +
                                "; 1 => " + test.getData(1) +
                                "; 2 => " + test.getData(2) +
                                "; 3 => " + test.getData(3) +
                                "; 4 => " + test.getData(4) +
                                "; 5 => " + test.getData(5) +
                                "; 6 => " + test.getData(6));
            }
        }
    }
}