package com.xilingyuli.markdown;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;

/**
 * Created by xilingyuli on 2017/2/28.
 */

public class MarkDownController implements TextWatcher {

    private final MarkDownEditorView editorView;
    private final MarkDownPreviewView previewView;
    private final ToolsAdapter toolsAdapter;
    private boolean autoPreview = false;

    MarkDownController(@NonNull MarkDownEditorView editorView,@NonNull  MarkDownPreviewView previewView,@NonNull  ToolsAdapter toolsAdapter)
    {
        this(editorView,previewView,toolsAdapter,true);
    }

    public MarkDownController(@NonNull MarkDownEditorView editorView, @NonNull MarkDownPreviewView previewView, @NonNull ToolsAdapter toolsAdapter, boolean autoPreview)
    {
        this.editorView = editorView;
        this.previewView = previewView;
        this.toolsAdapter = toolsAdapter;

        //bind toolsAdapter to editorView
        this.toolsAdapter.setEditor(this.editorView);

        //preview
        preview();

        //set autoPreview true or false
        setAutoPreview(autoPreview);
    }

    public void setAutoPreview(boolean autoPreview)
    {
        if(this.autoPreview==autoPreview)
            return;
        this.autoPreview = autoPreview;
        if(autoPreview)
            editorView.addTextChangedListener(this);
        else
            editorView.removeTextChangedListener(this);
    }

    public void setOnPreInsertListener(OnPreInsertListener listener)
    {
        toolsAdapter.setOnPreInsertListener(listener);
    }

    public void insertImage(String url)
    {
        editorView.insertImage(url);
    }

    public void insertLink(Pair<String, String> info)
    {
        editorView.insertLink(info);
    }

    public void insertTable(Pair<Integer,Integer> size)
    {
        editorView.insertTable(size);
    }

    public void preview()
    {
        previewView.preview(editorView.getText()+"");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        preview();
    }
}
