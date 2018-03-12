package editor;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

/**
 * Created by Yimin on 2/27/16.
 */
public class Editor extends Application {
    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;
    public static double usableScreenWidth;
    public static int textStartX;
    public static int textStartY;
    public  static double scrollBarMax;
    public static BlinkCursor c;
    public static ScrollBar scrollBar;
    public static Number scrollbarLoc;
    public final int MARGIN = 10;
    public static String FILENAME;
    public static File input;
    //public static SuperLink data;

    /*
    public Editor(){
        c = new BlinkCursor(cursorX, cursorY);
    }
    */

    // This modulo helper function is borrowed from stackoverflow:
    // http://stackoverflow.com/questions/90238/whats-the-syntax-for-mod-in-java
    private int mod(int x, int y){
        int result = x % y;
        if (result < 0){
            result += y;
        }
        return result;
    }


    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        WINDOW_WIDTH = 500;
        WINDOW_HEIGHT = 500;
        textStartX = 5;
        textStartY = 0;

        Application.Parameters p = getParameters();
        FILENAME = p.getRaw().get(0);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        primaryStage.setTitle("Emmie's Editor - " + FILENAME);

        //scene.setOnMouseClicked(new MouseClickEventHandler(root));
        scene.setOnMouseClicked(new MouseClickEventHandler(KeyEventHandler.g));

        c = new BlinkCursor(textStartX, textStartY);
        //root.getChildren().add(c.br);
        KeyEventHandler.g.getChildren().add(c.br);
        c.makeRectangleColorChange();



        // Make a vertical scroll bar on the right side of the screen.
        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        // Set the range of the scroll bar.
        scrollBarMax = 1;
        scrollBar.setMin(0);
        scrollBar.setMax(0);
        scrollBar.setValue(0);
        scrollbarLoc = 0;


        // Add the scroll bar to the scene graph, so that it appears on the screen.
        root.getChildren().add(scrollBar);

        usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        File file = new File(FILENAME);
        if (file.exists()) {
            FileReading fr = new FileReading(keyEventHandler, file);
        }


        /** When the scroll bar changes position, change the height of Text. */
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {

                int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
                int topLayoutY = 0;
                int bottomLayoutY = (WINDOW_HEIGHT - 20 - textHeight);
                //System.out.println(textHeight);
                //newValue = Math.round(newValue.doubleValue());

                if (newValue.doubleValue() == 0){
                    KeyEventHandler.g.setLayoutY(0);
                } else if (newValue.doubleValue() == 10){
                    KeyEventHandler.g.setLayoutY(bottomLayoutY);
                } else {
                    double currentLayoutY = (bottomLayoutY / 10.0) * newValue.doubleValue();
                    KeyEventHandler.g.setLayoutY(currentLayoutY);
                }

                //Editor.textStartY = (int) (0 - (textHeight - 490) * newValue.doubleValue()/ 10.0);
                Editor.textStartY = (int) KeyEventHandler.g.getLayoutY(); // when to update
                KeyEventHandler.render();
                scrollbarLoc = newValue;

                //SuperLink.Node cur = KeyEventHandler.charSoFar.getCurrentNode();
            }
        });


        // Register listeners that resize Allen when the window is re-sized.
        // We're using some new syntax here to create a ChangeListener with an overridden
        // changed() method; this is called instantiating an "anonymous class."  If you're curious
        // to learn more about this syntax, try Googling "Java anonymous class".  Beware that
        // IntelliJ sometimes collapses code blocks like this! If this happens, you can click on
        // the "+" icon that's to the left of the code (and to the right of the line numbers) to
        // expand the code again.
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                WINDOW_WIDTH = newScreenWidth.intValue();
                usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
                scrollBar.setLayoutX(usableScreenWidth);
                KeyEventHandler.render();

            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                WINDOW_HEIGHT = newScreenHeight.intValue();
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                KeyEventHandler.render();

            }
        });





        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Expected usage: File <filename>");
            System.exit(1);
        }

        launch(args);
    }
}
