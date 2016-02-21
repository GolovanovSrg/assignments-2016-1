package ru.spbau.mit;

/**
 * Created by golovanov on 21.02.16.
 */
public class StringSetImpl implements StringSet {

    private final StringSetNode root;

    private class StringSetNode {
        StringSetNode[] children;
        int sizeSubTree;
        boolean isUsed;

        public StringSetNode() {
            children =  new StringSetNode[52];
            sizeSubTree = 0;
            isUsed = false;
        }

        private int index(char ch) {
            final byte lowerOffset = 71; //ASCII
            final byte upperOffset = 65; //ASCII
            final byte aNo = 97; //ASCII

            if (ch >= aNo) {
                return ch - lowerOffset;
            } else {
                return ch - upperOffset;
            }
        }

        public StringSetNode getNextNode(char ch) {
            int idxNextNode = index(ch);
            StringSetNode nextNode = children[idxNextNode];

            if (nextNode == null) {
                nextNode = children[idxNextNode] = new StringSetNode();
            }

            return nextNode;
        }

        public StringSetNode findNextNode(char ch) {
            int idxNextNode = index(ch);
            StringSetNode nextNode = children[idxNextNode];

            if (nextNode == null) {
                return null;
            }

            return nextNode;
        }
    }

    public StringSetImpl() {
        root = new StringSetNode();
    }

    public StringSetImpl(String element) {
        this();
        add(element);
    }

    @Override
    public boolean add(String element) {
        StringSetNode curNode = root;

        for (char ch : element.toCharArray()) {
            curNode = curNode.getNextNode(ch);
        }

        if (!curNode.isUsed) {
            curNode.isUsed = true;

            curNode = root;
            for (char ch : element.toCharArray()) {
                curNode.sizeSubTree += 1;
                curNode = curNode.getNextNode(ch);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean contains(String element) {
        StringSetNode curNode = root;

        for (char ch : element.toCharArray()) {
            curNode = curNode.findNextNode(ch);

            if (curNode == null) {
                return false;
            }
        }

        return curNode.isUsed;
    }

    @Override
    public boolean remove(String element) {
        if (contains(element)) {
            StringSetNode curNode = root;

            for (char ch : element.toCharArray()) {
                int idxNextNode = curNode.index(ch);
                curNode.sizeSubTree -= 1;

                if (curNode.sizeSubTree == 0) {
                    curNode.children[idxNextNode] = null;
                    return true;
                }

                curNode = curNode.children[idxNextNode];
            }
            curNode.isUsed = false;
            return true;
        }

        return false;
    }

    @Override
    public int size() {
        return root.sizeSubTree;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        if (prefix.equals("")) {
            return size();
        }

        StringSetNode curNode = root;

        for (char ch : prefix.toCharArray()) {
            curNode = curNode.findNextNode(ch);

            if (curNode == null) {
                return 0;
            }
        }

        return curNode.isUsed ? 1 + curNode.sizeSubTree : curNode.sizeSubTree;
    }
}
