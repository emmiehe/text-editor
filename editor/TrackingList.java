package editor;

import javafx.scene.Node;

/**
 * Created by Yimin on 3/7/16.
 */
public class TrackingList<Item> {
    private Item[] items;
    private int size;
    private int currentPos;
    private int nextFirst;
    private int nextLast;
    //private boolean undid;
    private int countNewUndoMove = 0;

    public TrackingList() {
        size = 0; // does size matter?
        currentPos = 0;
        nextFirst = 0;
        nextLast = 1;
        items = (Item[]) new Object[100];
        //undid = false;
    }


    // This modulo helper function is borrowed from stackoverflow:
    // http://stackoverflow.com/questions/90238/whats-the-syntax-for-mod-in-java
    private int mod(int x, int y){
        int result = x % y;
        if (result < 0){
            result += y;
        }
        return result;
    }

    // this list does not remove; it only adds
    // if type in a character or delete one, that node is going to be added here
    public void add(Item x) {
        items[currentPos] = x;
        currentPos = mod(currentPos + 1, 100);
        size += 1;
        if (size > 100) {
            size = 100;
        }
        countNewUndoMove = 0;
        //undid = false;
    }

    public void undoMove(){
        if (!isEmpty() && currentPos > 0) {
            currentPos = mod(currentPos - 1, 100);
            Item target = items[currentPos];
            // undo
            if (((SuperLink.Node) target).next.prev == target) {
                ((SuperLink.Node) target).prev.next = (((SuperLink.Node) target).next);
                ((SuperLink.Node) target).next.prev = (((SuperLink.Node) target).prev);
                if (KeyEventHandler.charSoFar.getCurrentNode() == target) {
                    KeyEventHandler.charSoFar.setCurrentNode(((SuperLink.Node) target).prev);
                }
                KeyEventHandler.charSoFar.size -= 1;
                KeyEventHandler.g.getChildren().remove(((SuperLink.Node) target).item);
            } else {
                ((SuperLink.Node) target).prev.next = (((SuperLink.Node) target));
                ((SuperLink.Node) target).next.prev = (((SuperLink.Node) target));

                KeyEventHandler.charSoFar.setCurrentNode((SuperLink.Node) target);

                KeyEventHandler.charSoFar.size += 1;
                KeyEventHandler.g.getChildren().add((Node) ((SuperLink.Node) target).item);

            }
            //undid = true;
            countNewUndoMove += 1;
        }

    }

    public void redoMove(){
        if (!isEmpty() && items[currentPos] != null && countNewUndoMove != 0) {
            Item target = items[currentPos];
            // red
            if (((SuperLink.Node) target).next.prev == target) {
                ((SuperLink.Node) target).prev.next = ((SuperLink.Node) target).next;
                ((SuperLink.Node) target).next.prev = ((SuperLink.Node) target).prev;
                if (KeyEventHandler.charSoFar.getCurrentNode() == target) {
                    KeyEventHandler.charSoFar.setCurrentNode(((SuperLink.Node) target).prev);
                }
                KeyEventHandler.charSoFar.size -= 1;
                KeyEventHandler.g.getChildren().remove(((SuperLink.Node) target).item);

            } else {
                ((SuperLink.Node) target).prev.next = ((SuperLink.Node) target);
                ((SuperLink.Node) target).next.prev = ((SuperLink.Node) target);
                KeyEventHandler.charSoFar.setCurrentNode((SuperLink.Node) target);
                KeyEventHandler.charSoFar.size += 1;
                KeyEventHandler.g.getChildren().add((Node) ((SuperLink.Node) target).item);


            }
            currentPos = mod(currentPos + 1, 100);
            countNewUndoMove -= 1;
        }


    }



    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }


    public void printDeque(){
        int curr = mod(nextFirst + 1, 100);
        for (int i = 0; i < size; i++){
            System.out.print(items[curr] + " ");
            curr = mod(curr + 1, 100);
        }
    }




    public Item get(int index){
        if (isEmpty() == true){
            return null;
        }
        int curr = mod(nextFirst + 1, items.length);
        int ind = mod(curr + index, items.length);
        return items[ind];
    }



}
