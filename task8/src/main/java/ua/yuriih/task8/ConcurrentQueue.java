package ua.yuriih.task8;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ConcurrentQueue<T> {
    private static final Unsafe unsafe;

    static {
        Unsafe theUnsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        unsafe = theUnsafe;
    }

    private static final class Node<T> {
        public Node<T> next;
        public static final long nextOffset;
        public final T value;

        static {
            long offset = 0;
            try {
                offset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            nextOffset = offset;
        }

        Node(Node<T> next, T value) {
            this.next = next;
            this.value = value;
        }
    }

    private Node<T> head;
    private Node<T> tail;

    private static final long headOffset;
    private static final long tailOffset;

    static {
        long offset = 0;
        try {
            offset = unsafe.objectFieldOffset(ConcurrentQueue.class.getDeclaredField("head"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        headOffset = offset;

        try {
            offset = unsafe.objectFieldOffset(ConcurrentQueue.class.getDeclaredField("tail"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        tailOffset = offset;
    }

    public ConcurrentQueue() {
        Node<T> dummy = new Node<>(null, null);
        head = dummy;
        tail = dummy;
    }

    public void enqueue(T value) {
        Node<T> node = new Node<>(null, value);
        Node<T> currentTail;
        while (true) {
            currentTail = tail;
            Node<T> next = currentTail.next;

            if (currentTail == tail) {
                if (next == null) {
//                    if (currentTail.get().next.compareAndSet(null, node))
                    if (unsafe.compareAndSwapObject(currentTail, Node.nextOffset, null, node))
                        break;
                } else {
                    //Tail was not pointing to the last node,
                    //try to swing tail to the next node
//                    tail.compareAndSet(currentTail.get(), next.get());
                    unsafe.compareAndSwapObject(this, tailOffset, currentTail, next);
                }
            }
        }
        //Done, try to swing tail to the inserted node
//        tail.compareAndSet(currentTail.get(), node);
        unsafe.compareAndSwapObject(this, tailOffset, currentTail, node);
    }

    public T dequeue() {
        T returnedValue;

        while (true) {
            Node<T> currentHead = head;
            Node<T> currentTail = tail;
            Node<T> next = currentHead.next;

            if (currentHead == head) {
                //is queue empty or is tail falling behind?
                if (currentHead == tail) {
                    if (next == null) {
                        //Queue is empty
                        return null;
                    }
                    //Tail is falling behind, advance it
//                    tail.compareAndSet(currentTail.get(), next.get());
                    unsafe.compareAndSwapObject(this, tailOffset, currentTail, next);
                } else {
                    returnedValue = next.value;
                    //Try to swing Head to the next node
//                    if (head.compareAndSet(currentHead.get(), next.get()))
                    if (unsafe.compareAndSwapObject(this, headOffset, currentHead, next))
                        break;
                }
            }
        }
        return returnedValue;
    }
}
