package com.devansh.engine;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    private String id;
    private String content;
    private Item right;
    private Item left;
    private String operation;
    private boolean isDeleted;
    private boolean isBold;
    private boolean isItalic;

    public Item(String id, String content) {
        this.id = id;
        this.content = content;
        this.right = null;
        this.left = null;
        this.isDeleted = false;
    }

    @Override
    public String toString() {
        return "Item { " +
        "id=" + id
        + ", content=" + content
        + ", right=" + (right != null ? right.getId() : null)
        + ", left=" + (left != null ? left.getId() : null)
        + ", operation=" + operation
        + ", isDeleted=" + isDeleted
        + ", isBold=" + isBold
        + ", isItalic=" + isItalic
        + " }";
    }

}


















