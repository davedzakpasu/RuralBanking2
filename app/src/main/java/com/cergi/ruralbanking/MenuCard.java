package com.cergi.ruralbanking;

/**
 * Created by david.dzakpasu on 13/07/2016.
 */
public class MenuCard {
    private String label;
    private int thumbnail;

    public MenuCard() {
    }

    public MenuCard(String name, int thumbnail) {
        this.label = name;
        this.thumbnail = thumbnail;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
