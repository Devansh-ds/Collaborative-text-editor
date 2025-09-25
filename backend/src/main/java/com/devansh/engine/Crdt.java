package com.devansh.engine;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
public class Crdt {

    private HashMap<String, Item> crdtMap;
    private Item firstItem;

    public Crdt() {
        crdtMap = new HashMap<>();
    }

    public Crdt(byte[] bytes) {
        InitCrdt(bytes);
    }

    // this is to run when first time loading the doc in memory from database.
    public void InitCrdt(byte[] bytes) {
        List<Item> items = (List<Item>) getDeserializedCrdt(bytes);
        crdtMap = getCrdtMap(items);
        if (!items.isEmpty()) {
            firstItem = items.get(0);
        } else {
            firstItem = null;
        }
    }

    public Item getItem(String id) {
        return crdtMap.getOrDefault(id, null);
    }

    public void insert(Item item) {
        // Case 1: Insert at the beginning
        if (item.getLeft() == null) {
            item.setRight(firstItem);
            if (firstItem != null) {
                firstItem.setLeft(item);
            }
            firstItem = item;
            crdtMap.put(item.getId(), item);
            return;
        }

        // Case 2: Insert after some item
        Item left = item.getLeft();
        Item right = left.getRight();

        // Slide forward until we find the correct spot (for concurrent inserts)
        while (right != null && compare(item, right) > 0) {
            left = right;
            right = right.getRight();
        }

        // Link new item
        item.setLeft(left);
        item.setRight(right);
        left.setRight(item);
        if (right != null) {
            right.setLeft(item);
        }

        crdtMap.put(item.getId(), item);
    }

    /**
     * Compare two items to decide deterministic order for concurrent inserts.
     * Return <0 if a should come before b, >0 if a after b, 0 if equal.
     * This uses clientId or timestamp (you can customize).
     */
    private int compare(Item a, Item b) {
        // Tie-breaker: use clientId after '@'
        String aClient = a.getId().split("@")[1];
        String bClient = b.getId().split("@")[1];
        return aClient.compareTo(bClient);
    }



    public void delete(String key) {
        Item item = crdtMap.get(key);
        item.setDeleted(true);
        item.setOperation("delete");
    }

    public void format(String key, boolean isBold, boolean isItalic) {
        Item item = crdtMap.get(key);
        item.setBold(isBold);
        item.setItalic(isItalic);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Item currentItem = firstItem;
        while (currentItem != null) {
            if (!currentItem.isDeleted()) {
                sb.append(currentItem.getContent());
            }
            currentItem = currentItem.getRight();
        }
        return sb.toString();
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        Item currentItem = firstItem;
        while (currentItem != null) {
            items.add(currentItem);
            currentItem = currentItem.getRight();
        }
        return items;
    }

    public byte[] getSerializedCrdt() {
        Object obj = getClearData();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Crdt: " +  e);
        }
    }

    // helper functions below

    private List<Item> getClearData() {
        List<Item> items = new ArrayList<>();
        int index = 0;
        Item currentItem = firstItem;
        while (currentItem != null) {
            if (!currentItem.isDeleted()) {
                Item left = index == 0? null : items.get(index - 1);
                Item item = Item.builder()
                        .id(index + "@_")
                        .content(currentItem.getContent()).operation(currentItem.getOperation())
                        .isBold(currentItem.isBold()).isItalic(currentItem.isItalic())
                        .isDeleted(currentItem.isDeleted())
                        .right(null)
                        .left(left)
                        .build();
                if (index != 0) {
                    items.get(index - 1).setRight(item);
                }
                items.add(item);
                index++;
            }
            currentItem = currentItem.getRight();
        }
        return items;
    }

    private Object getDeserializedCrdt(byte[] bytes) {
        if (bytes.length == 0) {
            return new ArrayList<>();
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }

    private HashMap<String, Item> getCrdtMap(List<Item> items) {
        HashMap<String, Item> crdtMap = new HashMap<>();
        for (Item item : items) {
            crdtMap.put(item.getId(), item);
        }
        return crdtMap;
    }

}
























