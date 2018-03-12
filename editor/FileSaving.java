package editor;

import javafx.scene.text.Text;

import java.io.*;

/**
 * Created by Yimin on 3/7/16.
 */
public class FileSaving {

    public FileSaving(String name) {
        try {
            FileWriter writer = new FileWriter(name);
            SuperLink.Node n = KeyEventHandler.charSoFar.getNode(0);
            for (int i =0; i < KeyEventHandler.charSoFar.size(); i++) {
                String charRead= ((Text)(n.item)).getText();


                if (charRead == "\n" || charRead == "\r") {
                    //System.out.print("NEWLINE");
                }else {
                    //System.out.print(charRead);
                }

                writer.write(charRead);
                n = n.next;
            }
            writer.close();
        } catch (IOException ioException){
            System.out.println("Error when copying; exception was: " + ioException);
        }

    }

}
