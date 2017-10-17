package ru.spbau.mit.java;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeListImpl<KeyType> implements LockFreeList<KeyType> {

    private final Node<KeyType> head;
    private final Node<KeyType> tail;

    LockFreeListImpl() {
        tail = new Node<>();
        head = new Node<>(tail);
    }

    @Override
    public boolean isEmpty() {
        return head.next.getReference() == tail;
    }

    @Override
    public void append(KeyType value) {
        Node<KeyType> newNode = new Node<>(value);
        Node<KeyType> previousNode;
        while (true) {
            previousNode = searchTheLastElement();
            newNode.next = new AtomicMarkableReference<>(tail, false);
            if (previousNode.next.compareAndSet(tail, newNode, false, false)) {
                return;
            }
        }
    }

    private Node<KeyType> searchTheLastElement() {
        boolean[] mark = {false};
        Node<KeyType> tmpNode = head;
        while (tmpNode.next.getReference() != tail) {
            tmpNode = tmpNode.next.get(mark);
        }
        if (mark[0])
            return searchTheLastElement();
        else
            return tmpNode;
    }

    @Override
    public boolean remove(KeyType value) {
        Node<KeyType> currentNode;
        Node<KeyType> currentNodeNext;
        Node<KeyType> previousNode;
        while (true) {
            Neighbours<KeyType> neighbours = searchNeighbours(value);
            previousNode = neighbours.previous;
            currentNode = neighbours.current;

            if (currentNode == tail) {
                return false;
            }

            currentNodeNext = currentNode.next.getReference();
            if (currentNode.next.compareAndSet(currentNodeNext, currentNodeNext, false, true)) {
                break;
            }
        }

        // We will request searchNeighbours to change links if previousNode.next is marked.
        if (!previousNode.next.compareAndSet(currentNode, currentNodeNext, false, false)) {
            searchNeighbours(value);
        }
        return true;
    }

    private Neighbours<KeyType> searchNeighbours(KeyType keyToSearch) {
        Node<KeyType> previousNode = head;
        Node<KeyType> previousNextNode = head.next.getReference();
        Node<KeyType> currentNode;

        while (true) {
            Node<KeyType> tmpNode = head;
            boolean[] isNextNodeMarked = {false};
            Node<KeyType> tmpNodeNext = head.next.get(isNextNodeMarked);

            do {
                if (!isNextNodeMarked[0]) {
                    previousNode = tmpNode;
                    previousNextNode = tmpNodeNext;
                }
                tmpNode = tmpNodeNext;
                if (tmpNode == tail) {
                    break;
                }
                tmpNodeNext = tmpNode.next.get(isNextNodeMarked);
            } while (isNextNodeMarked[0] || !tmpNode.key.equals(keyToSearch));
            currentNode = tmpNode;

            if (previousNode.next.compareAndSet(previousNextNode, currentNode, false, false)) {
                if (currentNode != tail && currentNode.next.isMarked()) {
                    return searchNeighbours(keyToSearch);
                } else {
                    return new Neighbours<>(previousNode, currentNode);
                }
            }
        }
    }

    @Override
    public boolean contains(KeyType value) {
        Node<KeyType> rightNode;
        rightNode = searchNeighbours(value).current;
        return rightNode != tail && rightNode.key.equals(value);
    }

    private static class Neighbours<KeyType> {
        Node<KeyType> previous;
        Node<KeyType> current;

        Neighbours(Node<KeyType> previous, Node<KeyType> current) {
            this.previous = previous;
            this.current = current;
        }
    }

    private static class Node<KeyType> {
        private KeyType key;
        private AtomicMarkableReference<Node<KeyType>> next;

        Node() {
        }

        Node(Node<KeyType> next) {
            this.next = new AtomicMarkableReference<>(next, false);
        }

        Node(KeyType key) {
            this.key = key;
        }
    }
}
