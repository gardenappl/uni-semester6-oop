package ua.yuriih.task8;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentQueueTest {
    private static final int TEST_LENGTH = 10000;

    @Test
    void enqueue_dequeue_simple() {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();
        
        queue.enqueue(0);
        assertEquals(0, queue.dequeue());
        assertNull(queue.dequeue());

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.dequeue());
        queue.enqueue(4);

        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertEquals(4, queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    void enqueue_multiThread() throws InterruptedException {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();

        //insert numbers from 3 threads

        Thread thread1 = new Thread(() -> assertDoesNotThrow(() -> {
            for (int i = 0; i < TEST_LENGTH; i++)
                queue.enqueue(i);
        }));
        Thread thread2 = new Thread(() -> assertDoesNotThrow(() -> {
            for (int i = TEST_LENGTH; i < TEST_LENGTH * 2; i++)
                queue.enqueue(i);
        }));
        Thread thread3 = new Thread(() -> assertDoesNotThrow(() -> {
            for (int i = TEST_LENGTH; i < TEST_LENGTH * 3; i++)
                queue.enqueue(i);
        }));

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        Set<Integer> resultSet = new HashSet<>();
        Integer result;
        while (true) {
            result = queue.dequeue();
            if (result == null)
                break;
            resultSet.add(result);
        }
        assertNull(queue.dequeue());

        //Check that all numbers were dequeued (in no particular order)
        for (int i = 0; i < TEST_LENGTH; i++)
            assertTrue(resultSet.contains(i));
    }


    @Test
    void enqueue_dequeue_multiThread() {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();

        //insert numbers from 1 thread

        for (int i = 0; i < TEST_LENGTH; i++)
            queue.enqueue(i);

        //insert and remove numbers from 2 threads
        ExecutorService service = Executors.newFixedThreadPool(2);

        Future<?> future1 = service.submit(() -> assertDoesNotThrow(() -> {
            for (int i = 0; i < TEST_LENGTH; i++)
                assertEquals(i, queue.dequeue());
        }));
        Future<?> future2 = service.submit(() -> assertDoesNotThrow(() -> {
            for (int i = TEST_LENGTH; i < TEST_LENGTH * 2; i++)
                queue.enqueue(i);
        }));

        //join
        assertDoesNotThrow(() -> future1.get());
        assertDoesNotThrow(() -> future2.get());

        for (int i = TEST_LENGTH; i < TEST_LENGTH * 2; i++) {
            assertEquals(i, queue.dequeue());
        }

        assertNull(queue.dequeue());
    }
}