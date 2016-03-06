/**
 * Created by golovanov on 01.03.16.
 */

package ru.spbau.cp;
import java.io.*;

public class Cp {
    private static final int sizeBuf = 4096;

    public static void main(String [] args) {
        if (args.length != 2) {
            System.err.println("Noooooo! Only 2 args!");
            return;
        }

        try (BufferedInputStream input =
                     new BufferedInputStream(new FileInputStream(args[0]), sizeBuf)) {

            File f = new File(args[1]);
            if (!f.exists()) {
                f.createNewFile();
            }

            try (BufferedOutputStream output =
                         new BufferedOutputStream(new FileOutputStream(f), sizeBuf)) {
                int b;
                while ((b = input.read()) != -1) {
                    output.write(b);
                }
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't find file: " + args[1]);
        } catch (IOException e) {
            System.err.println("Strange IOException happened. Message: " + e.getMessage());
        }
    }
}
