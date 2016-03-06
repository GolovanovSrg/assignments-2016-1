package ru.spbau.mit;

import java.io.*;

/**
 * Created by golovanov on 21.02.16.
 */
public class StringSetImpl implements StringSet, StreamSerializable {

    private StringSetNode root;
    private static final byte numChar = 52;

    private static class StringSetNode implements Serializable {
        StringSetNode[] children;
        int sizeSubTree;
        boolean isUsed;

        public StringSetNode() {
            children =  new StringSetNode[numChar];
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
                nextNode = children[idxNextNode] = new StringSetNode();
            }

            return nextNode;
        }

        public StringSetNode findNextNode(char ch) {
            int idxNextNode = indexChar(ch);
            return children[idxNextNode];
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
        return (curNode == null) ? 0 : curNode.sizeSubTree;
    }

    @Override
    public void serialize(OutputStream out) throws SerializationException {
        try (ObjectOutputStream objOut = new ObjectOutputStream(out)) {
            objOut.writeObject(root);
        } catch (IOException e) {
            throw new SerializationException();
        }

    }

    @Override
    public void deserialize(InputStream in) throws SerializationException {
        try (ObjectInputStream objIn = new ObjectInputStream(in)) {
            root = (StringSetNode)objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException();
        }
    }
}
