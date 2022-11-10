package me.lucidus.pathfindingnpc.entity.ai.astar;

//Allow this to use objects other than nodes
public class Heap {

    private final Node[] nodes;
    private int size;

    public Heap(int heapSize) {
        nodes = new Node[heapSize];
    }

    public void add(Node node) {
        node.heapIndex = size;
        nodes[size] = node;
        sortUp(node);
        size++;
    }

    public Node removeFirst() {
        Node first = nodes[0];
        size--;
        nodes[0] = nodes[size];
        nodes[0].heapIndex = 0;
        sortDown(nodes[0]);
        return first;
    }

    public void updateItem(Node node) {
        sortUp(node);
    }

    public int size() {
        return size;
    }

    public boolean contains(Node node) {
        return nodes[node.heapIndex].equals(node);
    }

    private void sortDown(Node node) {
        while (true) {
            int indexLeft = node.heapIndex * 2 + 1;
            int indexRight = node.heapIndex * 2 + 2;
            int swapIndex;

            if (indexLeft < size) {
                swapIndex = indexLeft;

                if (indexRight < size) {
                    if (nodes[indexLeft].compareTo(nodes[indexRight]) < 0) {
                        swapIndex = indexRight;
                    }
                }
                if (node.compareTo(nodes[swapIndex]) < 0)
                    swap(node, nodes[swapIndex]);
                else
                    return;
            } else
                return;
        }
    }

    private void sortUp(Node node) {
        while (true) {
            int parentIndex = (node.heapIndex - 1) / 2;
            Node parentNode = nodes[parentIndex];

            if (node.compareTo(parentNode) > 0)
                swap(node, parentNode);
            else
                break;
        }
    }

    private void swap(Node nodeA, Node nodeB) {
        nodes[nodeA.heapIndex] = nodeB;
        nodes[nodeB.heapIndex] = nodeA;

        int nodeAIndex = nodeA.heapIndex;
        nodeA.heapIndex = nodeB.heapIndex;
        nodeB.heapIndex = nodeAIndex;
    }
}
