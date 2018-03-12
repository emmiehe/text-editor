package editor;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;


/**
 * Created by Yimin on 2/27/16.
 */
public class KeyEventHandler implements EventHandler<KeyEvent> {

    protected static int currentX;
    protected static int currentY;
    static Group g;
    public static int charHeight;
    private static final String MESSAGE_PREFIX =
            "User pressed the shortcut key (command or control, depending on the OS)";

    /** The Text to display on the screen. */
    public static int fontSize = 12;
    private static String fontName = "Verdana";
    protected static SuperLink charSoFar; // This is used to store all the chars
    protected static ArrayList line; // used to know which line you are at
    protected static TrackingList track; // undo and redo tracking

    public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
        g = new Group();
        //g = root;
        charSoFar = new SuperLink();
        line = new ArrayList();
        track = new TrackingList();

        currentX = Editor.textStartX;
        currentY = Editor.textStartY;
        //charHeight = 15;

        // Initialize some empty text and add it to root so that it will be displayed.
        // displayText = new Text(textStartX, textStartY, "");
        // Always set the text origin to be VPos.TOP! Setting the origin to be VPos.TOP means
        // that when the text is assigned a y-position, that position corresponds to the
        // highest position across all letters (for example, the top of a letter like "I", as
        // opposed to the top of a letter like "e"), which makes calculating positions much
        // simpler!
        // displayText.setTextOrigin(VPos.TOP);
        // displayText.setFont(Font.font (fontName, fontSize));

        // All new Nodes need to be added to the root in order to be displayed.

        root.getChildren().add(g);
    }


    static int fontSizeGen() {
        return fontSize;
    }

    private static int charWidth(Text t) {
        int width = (int) Math.round(t.getLayoutBounds().getWidth()); //cursor itself has 1 width
        return width;
    }

    private static int charHeight(Text t) {
        int width = (int) Math.round(t.getLayoutBounds().getHeight());
        return width;
    }

    public static int charHeightNow() {
        Text temp = new Text(" ");
        temp.setTextOrigin(VPos.TOP);
        temp.setFont(Font.font(fontName, fontSizeGen()));
        return charHeight(temp);

    }


    //  long word that cannot fit in the first line
    public static SuperLink.Node wordStartsWith(SuperLink.Node someLetterNode) {
        SuperLink.Node temp = someLetterNode;
        while (!(((Text)(temp.item)).getText()).equals(" ")) {
            temp = temp.prev;
            if (temp == charSoFar.getNode(0).prev) {
                return someLetterNode;
            }
        }
        return temp;
    }



    public static void render() {
        int X = Editor.textStartX;
        int Y = Editor.textStartY;
        if (charSoFar.isEmpty()) {
            currentX = Editor.textStartX;
            currentY = Editor.textStartY;
            Editor.c.updateSize();
            Editor.c.updatePosition(currentX, currentY);
            //System.out.println("no letter");
        } else {
            line = new ArrayList();
            //charHeight = charHeight((Text) charSoFar.get(0));
            SuperLink.Node start = charSoFar.getNode(0);
            SuperLink.Node node = start;

            // node has not reached sentinel
            while (node != start.prev) {

                // tell the stored data that this is a new line
                if (X == Editor.textStartX) {
                    int lineIndex = (Y - Editor.textStartY) / charHeightNow();
                    line.add(lineIndex, node);
                    //System.out.println(lineIndex + "-th line starts with " + ((Text)(charSoFar.getNode(i).item)).getText());
                    //System.out.println(line.size());
                }

                Text temp = (Text) node.item;
                temp.setTextOrigin(VPos.TOP);
                temp.setFont(Font.font(fontName, fontSizeGen()));
                temp.setX(X);
                temp.setY(Y);
                X += charWidth(temp);


                if (temp.getText() == "\n" ||temp.getText() == "\r\n") {
                    Y += charHeightNow();
                    X = Editor.textStartX;
                }


                // word wrap
                if ( X >= Editor.usableScreenWidth - 5 && !(temp.getText()).equals(" ")){
                    X = Editor.textStartX;
                    Y += charHeightNow();
                    node = wordStartsWith(node);
                }

                node = node.next;
            }



            if (line.size() > 32) {
                Editor.scrollBar.setMax(10);
            } else {
                Editor.scrollBar.setMax(0);
            }


            // update cursor
            SuperLink.Node cn = charSoFar.getCurrentNode();
            if (charSoFar.isAtSentinel()) {
                currentX = Editor.textStartX;
                currentY = Editor.textStartY;
            }else if (cn == charSoFar.getNode(0).prev.prev && ((Text) (cn.item)).getText() == "\n") {
                currentX = Editor.textStartX;
                currentY += charHeightNow();
            }else if (cn == charSoFar.getNode(0).prev.prev && ((Text) (cn.item)).getText() != "\n"){
                currentX = (int) (((Text) (cn.item)).getX() + charWidth((Text) cn.item));
                currentY = (int) ((Text) (cn.item)).getY();
            } else if (((Text) (cn.item)).getText() == "\n") {
                if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                    currentX = (int) ((Text) (cn.next.item)).getX();
                    currentY = (int) ((Text) (cn.next.item)).getY();
                } else {
                    currentX = (int) ((Text) (cn.item)).getX();
                    currentY = (int) ((Text) (cn.item)).getY();
                }


            } else {
                currentX = (int) (((Text) (cn.item)).getX() + charWidth((Text) cn.item));
                currentY = (int) ((Text) (cn.item)).getY();
            }
            Editor.c.updateSize();
            Editor.c.updatePosition(currentX, currentY);
            //System.out.println("currentX is " + currentX + " and currentY is " + currentY);

        }
    }




    private SuperLink.Node closestNode(SuperLink.Node start, SuperLink.Node end, int knownX) {
        int min;
        if (start == charSoFar.getNode(0).prev) {
            min = (int) ((Text) (start.next.item)).getX() - knownX;
        }else {
            min = (int) ((Text) (start.item)).getX() - knownX;
        }
        min = Math.abs(min);
        SuperLink.Node result = start;
        while (start != end.next) {
            //System.out.println("enter loop");
            start = start.next;
            int temp;
            if (start == charSoFar.getNode(0).prev){
                temp = (int) ((Text)(start.prev.item)).getX() + charWidth((Text) (start.prev.item)) - knownX;

            }else {
                temp = (int) ((Text)(start.item)).getX() - knownX;
                temp = Math.abs(temp);
            }


            if (temp < min) {
                //System.out.println("better than the first one");
                min = temp;
                result = start;
            }
        }
        //System.out.println("finished loop");
        return result;
    }


    // when cursor is not visible
    private void snapBack(){
        int currentTop = 0 - (int) g.getLayoutY();
        Number newValue = Editor.scrollBar.getValue();
        if (currentTop > currentY){
            //System.out.println("the cursor is above");
            //System.out.println("currentY is " +  currentY);
            //System.out.println("currentTop is " + currentTop);
            int wantedLayOutY = 0 - currentY;
            int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
            // int topLayoutY = 0;
            int bottomLayoutY = (Editor.WINDOW_HEIGHT - 20 - textHeight);
            double corresValue = wantedLayOutY/(bottomLayoutY / 10.0);
            //System.out.println(corresValue);
            Editor.scrollBar.setValue(corresValue); // update the scrollbar?
            //g.setLayoutY(wantedLayOutY);
        }else if (currentTop < currentY - (Editor.WINDOW_HEIGHT - 20)){
            //System.out.println("the cursor is bellow");
            //System.out.println("currentY is " +  currentY);
            //System.out.println("currentTop is " + currentTop);
            //g.setLayoutY(0 - currentY + 480);
            int wantedLayOutY = 0 - currentY + Editor.WINDOW_HEIGHT - 20;
            int textHeight = KeyEventHandler.line.size() * KeyEventHandler.charHeightNow();
            // int topLayoutY = 0;
            int bottomLayoutY = (Editor.WINDOW_HEIGHT - 20 - textHeight);
            double corresValue = wantedLayOutY/(bottomLayoutY / 10.0);
            //System.out.println(corresValue);
            Editor.scrollBar.setValue(corresValue);
            //g.setLayoutY(wantedLayOutY);

        }else{ // cursor is visible
            return;
        }
    }


    @Override
    public void handle(KeyEvent keyEvent) {
        snapBack();
        if (keyEvent.isShortcutDown()) {
            if (keyEvent.getCode() == KeyCode.P) {
                System.out.println(currentX + ", " + currentY);
            } else if (keyEvent.getCode() == KeyCode.S) {
                FileSaving file = new FileSaving(Editor.FILENAME);
                System.out.println("Saved.");
            } else if (keyEvent.getCode() == KeyCode.PLUS || keyEvent.getCode() == KeyCode.EQUALS) {
                fontSize += 4;
                render();

            } else if (keyEvent.getCode() == KeyCode.MINUS) {
                fontSize -= 4;
                if (fontSize < 4) {
                    fontSize = 4;
                }
                render();

            } else if (keyEvent.getCode() == KeyCode.Z) { //undo
                track.undoMove();
                render();

            } else if (keyEvent.getCode() == KeyCode.Y) { //redo
                track.redoMove();
                render();

            }

        } else {

            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();

                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace key, which is
                    // represented as a character of value = 8 on Windows.
                    Text current;
                    // when press enter
                    if (characterTyped.charAt(0) == '\r') {
                        current = new Text("\n");
                    } else {
                        current = new Text(characterTyped);
                    }
                    //current.setTextOrigin(VPos.TOP);
                    //current.setFont(Font.font(fontName, fontSize));

                    charSoFar.addChar(current);
                    track.add(charSoFar.getCurrentNode());

                    g.getChildren().add(current);
                    render();
                    keyEvent.consume();
                }

            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();

                if (code == KeyCode.BACK_SPACE) {
                    if (charSoFar.isAtSentinel() != true) {
                        track.add(charSoFar.getCurrentNode());
                        Text removed = (Text) charSoFar.deleteChar();
                        g.getChildren().remove(removed);
                        render();
                        keyEvent.consume();
                    }
                }


                int lineNum = (currentY - Editor.textStartY) / KeyEventHandler.charHeightNow();

                if (code == KeyCode.UP) {
                    if (lineNum != 0) {
                        SuperLink.Node startN = (SuperLink.Node) line.get(lineNum - 1);
                        //System.out.println("this line starts with letter: " + ((Text)(startN.item)).getText());

                        SuperLink.Node endN;
                        if (lineNum < line.size() - 1) {
                            endN = ((SuperLink.Node) line.get(lineNum)).prev;
                        } else {  // if clicking at a position way bellow

                            endN = charSoFar.getNode(0).prev.prev; //the last character

                        }

                        //System.out.println("next line starts with letter: " + ((Text)(endN.item)).getText());

                        SuperLink.Node n = closestNode(startN, endN, currentX);
                        //System.out.println("I think this is the nearest letter: " + ((Text) (n.item)).getText());


                        charSoFar.setCurrentNode(n.prev);// what if it does not have prev?


                        //System.out.println("the current node is : " + ((Text) (n.prev.item)).getText());

                        render();
                        SuperLink.Node cn = charSoFar.getCurrentNode();
                        if (cn != charSoFar.getNode(0).prev && cn.next != charSoFar.getNode(0).prev){
                            if (((Text) (cn.item)).getText() == "\n" || ((Text) (cn.item)).getText().equals(" ")) {
                                if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                                    currentX = (int) ((Text) (cn.next.item)).getX();
                                    currentY = (int) ((Text) (cn.next.item)).getY();
                                } else {
                                    currentX = (int) ((Text) (cn.item)).getX();
                                    currentX = (int) ((Text) (cn.item)).getX();
                                }
                            //System.out.println("this happened!!!!!!");
                            }
                        }
                        Editor.c.updateSize();
                        Editor.c.updatePosition(currentX, currentY);

                    }else{
                        return;
                    }

                } else if (code == KeyCode.DOWN) {

                    if (lineNum < line.size() - 1) {
                        //System.out.println("this is line " + lineNum);

                        SuperLink.Node startN = (SuperLink.Node) line.get(lineNum + 1);
                        //System.out.println("this line starts with letter: " + ((Text)(startN.item)).getText());

                        SuperLink.Node endN;
                        //System.out.println("line size is " +line.size());
                        if (lineNum < line.size() - 2) {
                            endN = ((SuperLink.Node) line.get(lineNum + 2)).prev;
                        } else {  // if clicking at a position way bellow

                            endN = charSoFar.getNode(0).prev.prev; //the last character
                            //System.out.println("the last line");

                        }
                        //System.out.println("next line starts with letter: " + ((Text)(endN.item)).getText());

                        SuperLink.Node n = closestNode(startN, endN, currentX);
                        //System.out.println("I think this is the nearest letter: " + ((Text) (n.item)).getText());


                        charSoFar.setCurrentNode(n.prev);// what if it does not have prev - render() takes care of it
                        //System.out.println("the current node is : " + ((Text) (n.prev.item)).getText());
                        render();

                        SuperLink.Node cn = charSoFar.getCurrentNode();
                        if (((Text) (cn.item)).getText() == "\n" || ((Text) (cn.item)).getText().equals(" ")) {
                            if (charSoFar.hasNext() && (int) ((Text) (cn.next.item)).getX() == Editor.textStartX) {
                                currentX = (int) ((Text) (cn.next.item)).getX();
                                currentY = (int) ((Text) (cn.next.item)).getY();
                            } else {
                                currentX = (int) ((Text) (cn.item)).getX();
                                currentX = (int) ((Text) (cn.item)).getX();
                            }
                        }
                        Editor.c.updateSize();
                        Editor.c.updatePosition(currentX, currentY);


                    }else{
                        return;
                    }

                } else if (code == KeyCode.LEFT) {
                    // consider what happens when it is at the first location

                    if (!charSoFar.isAtSentinel()) {
                        charSoFar.setCurrentNode(charSoFar.getCurrentNode().prev); //update the cursor position
                        //Text prev = (Text) charSoFar.getCurrentNode().next.item;
                        //System.out.println(prev.getText());
                        //currentX = (int) prev.getX();
                        //System.out.println(currentX);
                        render();
                    } else {
                        return;

                    }


                } else if (code == KeyCode.RIGHT) {
                    // consider what happens when it is at the last location at current line
                    if (charSoFar.hasNext()) {
                        charSoFar.setCurrentNode(charSoFar.getCurrentNode().next);
                        //Text next = (Text) charSoFar.getCurrentNode().item;
                        //System.out.println(next.getText());
                        //currentX = (int) next.getX();
                        //System.out.println(currentX);
                        render();
                    }else {
                        return;

                    }


                }

                if (line.isEmpty() != true && line.size() * charHeightNow() > Editor.WINDOW_HEIGHT - 30) {
                    //double r = (double) line.size() * charHeightNow() / (double)(Editor.WINDOW_HEIGHT - 30);
                    Editor.scrollBarMax = 5;
                    Editor.scrollBar.setMax(Editor.scrollBarMax);
                    //System.out.println("scroll bar max value is now " + Editor.scrollBarMax);
                }

                keyEvent.consume();
                //Editor.c.updatePosition(currentX, currentY);
            }
        }
    }

}
