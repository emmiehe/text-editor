package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by Yimin on 2/27/16.
 */
public class BlinkCursor {
    public final Rectangle br;

    // is it necessary?
    public BlinkCursor() {
        // Create a skinny rectangle that gets displayed.
        br = new Rectangle(1, 15);
        br.setX(Editor.textStartX);
        br.setY(Editor.textStartY);
    }


    public BlinkCursor(int x, int y) {
        // Create a skinny rectangle that gets displayed.
        br = new Rectangle(1, 15);
        br.setX(x);
        br.setY(y);

    }


    /** An EventHandler to handle changing the color of the rectangle. */
    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            br.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }


    /** Makes the text bounding box change color periodically. */
    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void updatePosition(int x, int y){
        br.setX(x);
        br.setY(y);
    }

    public void updateSize(){
        br.setHeight(KeyEventHandler.charHeightNow());
    }

}
