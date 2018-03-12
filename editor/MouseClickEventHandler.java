package editor;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by Yimin on 2/27/16.
 */
public class MouseClickEventHandler implements EventHandler<MouseEvent> {
    /** A Text object that will be used to print the current mouse position. */
    Text positionText;

    MouseClickEventHandler(Group root) {
        // For now, since there's no mouse position yet, just create an empty Text object.
        positionText = new Text("");
        // We want the text to show up immediately above the position, so set the origin to be
        // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
        // text).
        positionText.setTextOrigin(VPos.BOTTOM);

        // Add the positionText to root, so that it will be displayed on the screen.
        root.getChildren().add(positionText);
    }

    private SuperLink.Node closestNode(SuperLink.Node start, SuperLink.Node end, int knownX) {
        if (start == null) {
            return end;
        }
        int min = (int) ((Text)(start.item)).getX() - knownX;
        min = Math.abs(min);
        SuperLink.Node result = start;
        while (start != end) {
            start = start.next;
            int temp = (int) ((Text)(start.item)).getX() - knownX;
            temp = Math.abs(temp);
            if (temp < min) {
                min = temp;
                result = start;
            }
        }
        return result;
    }


    @Override
    public void handle(MouseEvent mouseEvent) {
        // Because we registered this EventHandler using setOnMouseClicked, it will only called
        // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
        // generated anytime the mouse is pressed and released on the same JavaFX node.
        double mousePressedX = mouseEvent.getX();
        double mousePressedY = mouseEvent.getY() - Editor.textStartY;

        if (KeyEventHandler.charSoFar.isEmpty()){
            KeyEventHandler.currentX = Editor.textStartX;
            KeyEventHandler.currentY = Editor.textStartY;

        }else {

            // calculate the nearest Text item
            int lineNum = (int) (mousePressedY - Editor.textStartY) / KeyEventHandler.charHeightNow(); // which line are you clicking on
            //System.out.println("currently at line " + lineNum);

            int roundX = (int) Math.round(mousePressedX);
            //System.out.println("roundX is " + roundX);


            SuperLink.Node startN;
            //System.out.println("this line starts with letter: " + ((Text)(startN.item)).getText());

            SuperLink.Node endN;
            if (lineNum < (KeyEventHandler.line).size() - 1) {
                startN = (SuperLink.Node) KeyEventHandler.line.get(lineNum);
                endN = (SuperLink.Node) KeyEventHandler.line.get(lineNum + 1);
            } else {  // if clicking at a position way bellow
                startN = null;
                endN = KeyEventHandler.charSoFar.getNode(0).prev.prev; //the last character

            }
            //System.out.println("next line starts with letter: " + ((Text)(endN.item)).getText());

            SuperLink.Node n = closestNode(startN, endN, roundX);
            //System.out.println("I think this is the nearest letter: " + ((Text) (n.item)).getText());


            KeyEventHandler.charSoFar.setCurrentNode(n.prev);// what if it does not have prev?

            //System.out.println("the current node is : " + ((Text) (n.prev.item)).getText());
            KeyEventHandler.render();
        }

        //positionText.setText("(" + mousePressedX + ", " + mousePressedY + ")");
        positionText.setX(mousePressedX);
        positionText.setY(mousePressedY);
    }
}
