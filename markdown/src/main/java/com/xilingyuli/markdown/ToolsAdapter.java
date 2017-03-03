package com.xilingyuli.markdown;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by xilingyuli on 2017/2/28.
 */

public class ToolsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private LayoutInflater inflater;
    private MarkDownEditorView editor;
    private OnPreInsertListener listener;
    protected static int[] toolsRes =
            {R.drawable.ic_format_header_1,
            R.drawable.ic_format_header_2,
            R.drawable.ic_format_header_3,
            R.drawable.ic_insert_line,
            R.drawable.ic_format_bold,
            R.drawable.ic_format_italic,
            R.drawable.ic_format_strikethrough,
            R.drawable.ic_format_list_unordered,
            R.drawable.ic_format_list_ordered,
            R.drawable.ic_insert_image,
            R.drawable.ic_insert_link,
            R.drawable.ic_format_blockquote,
            R.drawable.ic_insert_code_single,
            R.drawable.ic_insert_code,
            R.drawable.ic_insert_table,
            R.drawable.ic_format_header_4,
            R.drawable.ic_format_header_5,
            R.drawable.ic_format_header_6};

    public ToolsAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void setEditor(@NonNull MarkDownEditorView editor)
    {
        this.editor = editor;
    }

    public void setOnPreInsertListener(OnPreInsertListener listener)
    {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ToolsViewHolder(inflater.inflate(R.layout.imgbtn_tool, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ToolsViewHolder)holder).setImage(toolsRes[position]);
    }

    @Override
    public int getItemCount() {
        return toolsRes.length;
    }

    @Override
    public void onClick(View view) {
        if(editor==null)
            return;
        int tag = (int)view.getTag();
        if (tag == R.drawable.ic_format_header_1)
            editor.header(1);
        else if (tag == R.drawable.ic_format_header_2)
            editor.header(2);
        else if (tag == R.drawable.ic_format_header_3)
            editor.header(3);
        else if (tag == R.drawable.ic_format_header_4)
            editor.header(4);
        else if (tag == R.drawable.ic_format_header_5)
            editor.header(5);
        else if (tag == R.drawable.ic_format_header_6)
            editor.header(6);
        else if (tag == R.drawable.ic_insert_line)
            editor.insertLine();
        else if (tag == R.drawable.ic_format_bold)
            editor.bold();
        else if (tag == R.drawable.ic_format_italic)
            editor.italic();
        else if (tag == R.drawable.ic_format_strikethrough)
            editor.strikethrough();
        else if (tag == R.drawable.ic_format_list_unordered)
            editor.unordered();
        else if (tag == R.drawable.ic_format_list_ordered)
            editor.ordered();
        else if (tag == R.drawable.ic_insert_image) {
            if (listener!=null)
                listener.onPreInsertImage();
        }
        else if (tag == R.drawable.ic_insert_link) {
            if (listener!=null)
                listener.onPreInsertLink();
        }
        else if (tag == R.drawable.ic_format_blockquote)
            editor.blockquote();
        else if (tag == R.drawable.ic_insert_code_single)
            editor.codeSingle();
        else if (tag == R.drawable.ic_insert_code)
            editor.code();
        else if (tag == R.drawable.ic_insert_table) {
            if (listener!=null)
                listener.onPreInsertTable();
        }
    }

    private class ToolsViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        ToolsViewHolder(View view)
        {
            super(view);
            this.view = view;
            this.view.setOnClickListener(ToolsAdapter.this);
        }
        void setImage(int resId)
        {
            ((ImageButton)view).setImageResource(resId);
            view.setTag(resId);
        }
    }
}