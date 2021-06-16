package ua.yuriih.task4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LockFreeSkipListTest {
    private static final int TEST_LENGTH = 1000;

    @Test
    void insert_singleThread() {
        LockFreeSkipList<Integer> list = new LockFreeSkipList<Integer>(3);
        assertFalse(list.contains(1));
        assertFalse(list.contains(2));
        assertFalse(list.contains(3));

        assertTrue(list.add(3));
        assertTrue(list.contains(3));

        assertFalse(list.add(3));

        assertTrue(list.add(1));
        assertTrue(list.contains(1));
        assertTrue(list.contains(3));

        assertTrue(list.add(2));
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(1));
        
        assertFalse(list.add(3));
    }

    @Test
    void insert_multiThread() {
        LockFreeSkipList<Integer> list = new LockFreeSkipList<Integer>(3);

        //Insert odd numbers
        Thread threadOdd = new Thread(() -> {
            for (int i = 1; i < TEST_LENGTH; i += 2)
                assertTrue(list.add(i));
        });

        threadOdd.start();

        //Insert even numbers
        for (int i = 0; i < TEST_LENGTH; i += 2)
            assertTrue(list.add(i));

        assertDoesNotThrow(() -> threadOdd.join());

        for (int i = 0; i < TEST_LENGTH; i++)
            assertTrue(list.contains(i));
    }

    @Test
    void insert_delete_multiThread() {
        LockFreeSkipList<Integer> list = new LockFreeSkipList<Integer>(3);

        //Insert odd numbers
        for (int i = 1; i < TEST_LENGTH; i += 2)
            assertTrue(list.add(i), "Adding " + i);
        
        //Remove odd numbers
        Thread threadRemove = new Thread(() -> {
            for (int i = 1; i < TEST_LENGTH; i += 2)
                assertTrue(list.remove(i), "Removing " + i);
        });

        threadRemove.start();

        //Add even numbers
        for (int i = 0; i < TEST_LENGTH; i += 2)
            assertTrue(list.add(i), "Removing " + i);

        assertDoesNotThrow(() -> threadRemove.join());

        //Check: odd numbers all removed
        for (int i = 1; i < TEST_LENGTH; i += 2)
            assertFalse(list.contains(i), "Does not contain " + i);

        //Check: even numbers all added
        for (int i = 0; i < TEST_LENGTH; i += 2)
            assertTrue(list.contains(i), "Contains " + i);
    }
}