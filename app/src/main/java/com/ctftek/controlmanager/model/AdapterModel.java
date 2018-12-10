package com.ctftek.controlmanager.model;

/**
 * Created by renrui on 2017/6/2 0002.
 */

public class AdapterModel {

    private int row;
    private int column;
    private int gridViewWidth;
    private int gridViewHeight;
    private boolean clicked[][];

    public boolean[][] getClicked() {
        return clicked;
    }

    public void setClicked(boolean[][] clicked) {
        this.clicked = clicked;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getGridViewWidth() {
        return gridViewWidth;
    }

    public void setGridViewWidth(int gridViewWidth) {
        this.gridViewWidth = gridViewWidth;
    }

    public int getGridViewHeight() {
        return gridViewHeight;
    }

    public void setGridViewHeight(int gridViewHeight) {
        this.gridViewHeight = gridViewHeight;
    }
}
