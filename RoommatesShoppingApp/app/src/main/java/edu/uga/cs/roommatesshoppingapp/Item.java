package edu.uga.cs.roommatesshoppingapp;

/**
 * This POJO class represents a item, including the item name,
 * price, amount, and roommate names.
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class Item {

    private String itemName;
    private String itemPrice;
    private String itemAmount;
    private String names;

    public Item()
    {
        this.itemName = null;
        this.itemPrice = null;
        this.itemAmount = null;
        this.names = null;
    }

    public Item(String itemName, String itemPrice, String itemAmount, String names) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemAmount = itemAmount;
        this.names = names;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String toString() {
        return itemName + " " + itemPrice + " " + itemAmount + " " + names;
    }
}
