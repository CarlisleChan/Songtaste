package com.baidao.superrecyclerview;

public interface OnMoreListener {
    /**
     * @param totalCount
     * @param currentPosition for staggered grid this is max of all spans
     */
    public void onMoreAsked(int totalCount, int currentPosition);
}
