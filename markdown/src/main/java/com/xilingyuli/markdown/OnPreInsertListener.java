package com.xilingyuli.markdown;

/**
 * Created by xilingyuli on 2017/3/2.
 */

public interface OnPreInsertListener {
    public void onPreInsertImage();
    public void onPreInsertLink();
    public void onPreInsertTable();
}
