package ru.spbau.mit;

import java.io.*;

/**
 * Created by golovanov on 21.02.16.
 */
public class StringSetImpl implements StringSet, StreamSerializable {

    private StringSetNode root;
    private static final byte NUM_CHAR = 52;

    private static class StringSetNode {
        private StringSetNode[] children;
        private int sizeSubTree;
        private boolean isUsed;

        StringSetNode() {
            children =  new StringSetNode[NUM_CHAR];
            sizeSubTree = 0;
            isUsed = false;
        }

        private int indexChar(char ch) {
            if (ch <= 'Z') {
                return ch - 'A';
            } else {
                return ('Z' - 'A' + 1) + (ch - 'a');
            }
        }

        public StringSetNode getNextNode(char ch) {
            int idxNextNode = indexChar(ch);
            StringSetNode nextNode = children[idxNextNode];

            if (nextNode == null) {
                children[idxNextNode] = new StringSetNode();
                nextNode = children[idxNextNode];
            }

            return nextNode;
        }

        public StringSetNode findNextNode(char ch) {
            int idxNextNode = indexChar(ch);
            return children[idxNextNode];
        }

        public void serializeNode(DataOutputStream output) throws IOException {
            output.writeInt(sizeSubTree);
            output.writeBoolean(isUsed);

            for (StringSetNode child : children) {
                if (child != null) {
                    output.writeBoolean(true);
                    child.serializeNode(output);
                } else {
                    output.writeBoolean(false);
                }
            }
        }

        public void deserializeNode(DataInputStream input) throws IOException {
            sizeSubTree = input.readInt();
            isUsed = input.readBoolean();

            for (int idx = 0; idx < children.length; idx++) {
                if (input.readBoolean()) {
                    children[idx] = new StringSetNode();
                    children[idx].deserializeNode(input);
                }
            }
        }
    }

    public StringSetImpl() {
        root = new StringSetNode();
    }

    public StringSetImpl(String element) {
        this();
        add(element);
    }

    private StringSetNode getElemNode(String element) {
        StringSetNode curNode = root;

        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);
            curNode = curNode.getNextNode(ch);
        }

        return curNode;
    }

    private StringSetNode findElemNode(String element) {
        StringSetNode curNode = root;

        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);
            curNode = curNode.findNextNode(ch);

            if (curNode == null) {
                break;
            }
        }

        return curNode;
    }


    @Override
    public boolean add(String element) {
        StringSetNode curNode = getElemNode(element);

        if (curNode.isUsed) {
            return false;
        }

        curNode.isUsed = true;

        curNode = root;
        curNode.sizeSubTree++;

        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);
            curNode = curNode.findNextNode(ch);
            curNode.sizeSubTree++;
        }

        return true;
    }

    @Override
    public boolean contains(String element) {
        StringSetNode curNode = findElemNode(element);
        return (curNode != null) && curNode.isUsed;
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        StringSetNode curNode = root;
        curNode.sizeSubTree--;

        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);

            if (curNode.sizeSubTree == 0) {
                int idxNextNode = curNode.indexChar(ch);
                curNode.children[idxNextNode] = null;
                return true;
            }

            curNode = curNode.findNextNode(ch);
            curNode.sizeSubTree--;
        }
        curNode.isUsed = false;

        return true;
    }

    @Override
    public int size() {
        return root.sizeSubTree;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        StringSetNode curNode = findElemNode(prefix);
        if (curNode == null) {
            return 0;
        } else {
            return curNode.sizeSubTree;
        }
    }

    @Override
    public void serialize(OutputStream out) throws SerializationException {
        try (DataOutputStream output = new DataOutputStream(out)) {
            root.serializeNode(output);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    @Override
    public void deserialize(InputStream in) throws SerializationException {
        try (DataInputStream input = new DataInputStream(in)) {
            root.deserializeNode(input);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }
}
