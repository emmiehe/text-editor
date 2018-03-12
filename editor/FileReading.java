package editor;


import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;

/**
 * Created by Yimin on 3/7/16.
 */
public class FileReading {
    public FileReading(EventHandler k, File f) {
        try {
            // Check to make sure that the input file exists!
            if (!f.exists()) {
                System.out.println("Unable to read because file"
                        + " does not exist");
                return;
            }
            FileReader reader = new FileReader(f);
            // It's good practice to read files using a buffered reader.  A buffered reader reads
            // big chunks of the file from the disk, and then buffers them in memory.  Otherwise,
            // if you read one character at a time from the file using FileReader, each character
            // read causes a separate read from disk.  You'll learn more about this if you take more
            // CS classes, but for now, take our word for it!
            BufferedReader bufferedReader = new BufferedReader(reader);

            // Create a FileWriter to write to outputFilename. FileWriter will overwrite any data
            // already in outputFilename.


            //FileWriter writer = new FileWriter(outputFilename);

            int intRead = -1;
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            ((KeyEventHandler) k).charSoFar = new SuperLink();
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char read = (char) intRead;
                String charRead = String.valueOf(read);
                Text t;
                if (read == '\r' || read == '\n') {
                    t = new Text("\n");
                }else {
                    t = new Text(charRead);
                }

                ((KeyEventHandler) k).charSoFar.addChar(t);
                ((KeyEventHandler) k).g.getChildren().add(t);

            }
            KeyEventHandler.charSoFar.setCurrentNode(KeyEventHandler.charSoFar.getNode(0).prev);
            ((KeyEventHandler) k).render();
            Editor.c.updatePosition(Editor.textStartX, Editor.textStartY);


            //System.out.println("Successfully read file. ");

            // Close the reader and writer.
            bufferedReader.close();
            //writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }
}
