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
        // =============================================
        // Step 1: Handle insertion at the very beginning
        // =============================================
        if (item.getLeft() == null) {
            String firstItemId = firstItem == null ? null : firstItem.getId();
            String rightItemId = item.getRight() == null ? null : item.getRight().getId();

            // If the new item's clientId has higher precedence than the first item
            // then it should come before the current firstItem
            if (!Objects.equals(firstItemId, rightItemId)
                    && firstItem.getId().split("@")[1].compareTo(item.getId().split("@")[1]) > 0) {
                // Only set new item's right pointer for now
                item.setRight(firstItem);
                // We do NOT modify firstItem.left here to avoid breaking the linked list yet
            } else {
                // Normal case: insert at beginning
                item.setRight(firstItem);
                if (firstItem != null) {
                    firstItem.setLeft(item);
                }
                firstItem = item;             // Update head of the list
                crdtMap.put(item.getId(), item); // Add to map for fast lookup
                return;                       // Done inserting
            }
        }

        // ==================================================
        // Step 2: Insert somewhere in the middle or end
        // ==================================================
        // Slide 'item.getLeft()' forward until we find the correct left neighbor
        // based on clientId ordering (tie-breaker for concurrent inserts)
        while (item.getLeft().getRight() != item.getRight()
                && item.getLeft().getLeft().getId().split("@")[1]
                .compareTo(item.getId().split("@")[1]) > 0) {
            // Move left pointer to next item to respect deterministic order
            item.setLeft(item.getLeft().getRight());
        }

        // Step 3: Link the new item in the list
        item.setRight(item.getLeft().getRight());   // Set item's right pointer
        crdtMap.put(item.getId(), item);            // Add to map for quick lookup
        item.getLeft().setRight(item);              // Update left neighbor's right pointer
        if (item.getRight() != null) {              // Update right neighbor's left pointer
            item.getRight().setLeft(item);
        }
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
            throw new RuntimeException("Failed to serialize Crdt: ", e);
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
























