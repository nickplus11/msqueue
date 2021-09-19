package msqueue;

import kotlinx.atomicfu.AtomicRef;

public class MSQueue implements Queue {
    private AtomicRef<Node> head;
    private AtomicRef<Node> tail;

    public MSQueue() {
        Node dummy = new Node(0);
        this.head = new AtomicRef<>(dummy);
        this.tail = new AtomicRef<>(dummy);
    }

    @Override
    public int peek() {
        for (; ; ) {
            Node curHead = head.getValue();
            Node curTail = tail.getValue();
            if (curHead == curTail)
                if (curTail.next.getValue() == null)
                    return Integer.MIN_VALUE;
                else
                    updateTail(curTail, curTail.next.getValue());
            else
                return curHead.next.getValue().x;
        }
    }

    @Override
    public void enqueue(int x) {
        Node newTail = new Node(x);
        for (; ; ) {
            Node curTail = tail.getValue();
            if (curTail.next.compareAndSet(null, newTail)) {
                updateTail(curTail, newTail);
                return;
            } else {
                updateTail(curTail, curTail.next.getValue());
            }
        }
    }

    @Override
    public int dequeue() {
        for (; ; ) {
            Node curHead = head.getValue();
            Node curTail = tail.getValue();
            if (curHead == curTail)
                if (curTail.next.getValue() == null)
                    return Integer.MIN_VALUE;
                else
                    updateTail(curTail, curTail.next.getValue());
            else if (head.compareAndSet(curHead, curHead.next.getValue()))
                return curHead.next.getValue().x;
        }
    }

    private void updateTail(Node curTail, Node newTail) {
        tail.compareAndSet(curTail, newTail);
    }

    private class Node {
        final int x;
        AtomicRef<Node> next = new AtomicRef<>(null);

        Node(int x) {
            this.x = x;
        }
    }
}