package editor_ori;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;


public class Editor extends Application {

    /** An EventHandler to handle keys that get pressed. */
    private class KeyEventHandler implements EventHandler<KeyEvent> {
        int textStartX;
        int textStartY;

        /** The Text to display on the screen. */
        public Text displayText = new Text(250, 250, "");
        public int fontSize = 20;

        private String fontName = "Verdana";
        private LinkedList charSoFar = new LinkedList(); // This is used to store all the chars

        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            textStartX = 5;
            textStartY = 0;

            // Initialize some empty text and add it to root so that it will be displayed.
            displayText = new Text(textStartX, textStartY, "");
            // Always set the text origin to be VPos.TOP! Setting the origin to be VPos.TOP means
            // that when the text is assigned a y-position, that position corresponds to the
            // highest position across all letters (for example, the top of a letter like "I", as
            // opposed to the top of a letter like "e"), which makes calculating positions much
            // simpler!
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font (fontName, fontSize));

            // All new Nodes need to be added to the root in order to be displayed.
            root.getChildren().add(displayText);
        }

        // A helper function that convert a LinkedList to a String (easier to display)
        private String listToString(LinkedList ll){
            String str = "";
            for (int i = 0; i < ll.size(); i++){
                str += ll.get(i);
            }
            return str;
        }


        @Override
        public void handle(KeyEvent keyEvent) {
                if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                    // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                    // the KEY_TYPED event, javafx handles the "Shift" key and associated
                    // capitalization.
                    String characterTyped = keyEvent.getCharacter();

                    if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                        // Ignore control keys, which have non-zero length, as well as the backspace key, which is
                        // represented as a character of value = 8 on Windows.
                        charSoFar.addLast(characterTyped);
                        String result = listToString(charSoFar);
                        displayText.setText(result);
                        keyEvent.consume();
                    }

                    //centerText();
                } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                    // events have a code that we can check (KEY_TYPED events don't have an associated
                    // KeyCode).
                    KeyCode code = keyEvent.getCode();
                    // the following code are from: https://community.oracle.com/thread/2473397?tstart=0
                    // not sure whether Back_SPACE or DELETE works in this situation; I'll just leave it here
                    switch (code) {
                        case DELETE:
                            displayText.setText(""); // not seems to be activated

                        case BACK_SPACE:
                            // in case there is nothing to delete
                            if (charSoFar.size() > 0) {
                                charSoFar.removeLast();
                                String result = listToString(charSoFar);
                                displayText.setText(result);
                                keyEvent.consume();
                            }
                    }

                    if (code == KeyCode.UP) {
                        fontSize += 5;
                        displayText.setFont(Font.font(fontName, fontSize));
                        //centerText();
                    } else if (code == KeyCode.DOWN) {
                        fontSize = Math.max(0, fontSize - 5);
                        displayText.setFont(Font.font(fontName, fontSize));
                        //centerText();
                    }

            }
        }

        // This function become less useful since in our case we don't want the text to be centered
        private void centerText() {
            // Figure out the size of the current text.
            double textHeight = displayText.getLayoutBounds().getHeight();
            double textWidth = displayText.getLayoutBounds().getWidth();

            // Calculate the position so that the text will be center on the screen.
            double textTop = textStartY - textHeight / 2;
            double textLeft = textStartX - textWidth / 2;

            // Re-position the text.
            displayText.setX(textLeft);
            displayText.setY(textTop);

            // Make sure the text appears in front of any other objects you might add.
            displayText.toFront();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        int windowWidth = 500;
        int windowHeight = 500;
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        primaryStage.setTitle("Yimin's Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}