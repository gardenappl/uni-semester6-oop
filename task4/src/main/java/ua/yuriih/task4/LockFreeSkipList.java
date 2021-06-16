package ua.yuriih.task4;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeSkipList<T> {
    private static final class Node<T> {
        final T keyObject;
        final long key;
        final AtomicMarkableReference<Node<T>>[] nexts;
        final int height;
        
        public Node(T keyObject, int height) {
            this(keyObject, keyObject.hashCode(), height);
        }
        
        public Node(long key, int height) {
            this(null, key, height);
        }
        
        private Node(T keyObject, long key, int height) {
            this.keyObject = keyObject;
            this.key = key;
            this.nexts = new AtomicMarkableReference[height];
            for (int i = 0; i < height; i++)
                this.nexts[i] = new AtomicMarkableReference<>(null, false);
            this.height = height;
        }
    }

    

    private final int levels;
    private final Node<T> first;
    private final Node<T> last;
    
    public LockFreeSkipList(int levels) {
        if (levels < 1)
            throw new IllegalArgumentException("Skip list must have at least 1 level");

        this.levels = levels;
        first = new Node<>(Long.MIN_VALUE, levels);
        last = new Node<>(Long.MAX_VALUE, levels);
        for (int i = 0; i < levels; i++) {
            first.nexts[i].set(last, false);
        }
    }
    
    private boolean find(T element, Node<T>[] predecessors, Node<T>[] successors) {
        Boolean result = null;
        while (result == null) {
            result = tryFind(element, predecessors, successors);
        }
        return result;
    }
    
    private Boolean tryFind(T element, Node<T>[] predecessors, Node<T>[] successors) {
        int key = element.hashCode();

        boolean found = false;
        Node<T> previous = first;
        boolean[] currentMarked = new boolean[1];

        //Search for place to insert, starting from top level
        for (int i = levels - 1; i >= 0; i--) {
            Node<T> current = previous.nexts[i].getReference();
            
            while (true) {
                //if node marked for deletion, delete it and restart
                Node<T> next = current.nexts[i].get(currentMarked);
                if (currentMarked[0]) {
                    previous.nexts[i].compareAndSet(current, next, false, false);
                    return null;
                }
                
                if (key > current.key) {
                    previous = current;
                    current = previous.nexts[i].getReference();
                } else {
                    break;
                }
            }
            
            if (current.key == key) {
                //Found element; don't stop, populate predecessors and successors
                found = true;
            }

            predecessors[i] = previous;
            successors[i] = current;
        }
        return found;
    }

    public boolean contains(T element) {
        if (element == null)
            return false;
        Node<T>[] preds = new Node[levels];
        Node<T>[] succs = new Node[levels];

        return find(element, preds, succs);
    }
    
    private int getHeightForNewNode() {
        return 1 + (int)(Math.random() * levels);
    }
    
    public boolean add(T element) {
        int newNodeHeight = getHeightForNewNode();

        Node<T>[] predecessors = new Node[levels];
        Node<T>[] successors = new Node[levels];

        //keep retrying if another element had been inserted here
        while (true) {
            if(find(element, predecessors, successors))
                return false;

            //create new node
            Node<T> newNode = new Node<>(element, newNodeHeight);
            for (int i = 0; i < newNodeHeight; i++) {
                newNode.nexts[i].set(successors[i], false);
            }
            Node<T> predecessor = predecessors[0];
            Node<T> successor = successors[0];

            //insert at main level
            if (!predecessor.nexts[0].compareAndSet(successor, newNode,
                    false, false)) {
                continue;
            }

            //insert references at higher levels (bottom-up)
            for (int i = 1; i < newNodeHeight; i++) {
                while (true) {
                    if (predecessors[i].nexts[i].compareAndSet(successors[i],
                            newNode, false, false)) {
                        break;
                    } else {
                        //predecessor array is invalid, re-create
                        find(element, predecessors, successors);
                    }
                }
            }
            return true;
        }
    }

    public boolean remove(T element) {
        Node<T>[] predecessors = new Node[levels];
        Node<T>[] successors = new Node[levels];

        boolean[] isMarked = new boolean[1];

        //keep retrying if another element had been removed here
        while (true) {
            if(!find(element, predecessors, successors))
                return false;

            //mark for deletion (from top to bottom)
            Node<T> removedNode = successors[0];
            for (int i = removedNode.height - 1; i >= 0; i--) {
                Node<T> next = removedNode.nexts[i].get(isMarked);
                while (!isMarked[0]) {
                    removedNode.nexts[i].attemptMark(next, true);
                    next = removedNode.nexts[i].get(isMarked);
                }
            }
            return true;
        }
    }
}
