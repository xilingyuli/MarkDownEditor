package com.xilingyuli.markdowneditor;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.xilingyuli.markdown.MarkDownController;
import com.xilingyuli.markdown.MarkDownEditorView;
import com.xilingyuli.markdown.MarkDownPreviewView;
import com.xilingyuli.markdown.OnPreInsertListener;
import com.xilingyuli.markdown.ToolsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Markdown Blog Edit And Preview
 */
public class MainActivity extends AppCompatActivity implements OnPreInsertListener {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    @BindView(R.id.tools)
    RecyclerView tools;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    EditorFragment editorFragment;
    PreviewFragment previewFragment;
    MarkDownEditorView editorView;
    MarkDownPreviewView previewView;
    ToolsAdapter toolsAdapter;
    MarkDownController markDownController;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //保持viewPager随toolbar动画移动
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewGroup appappBarLayout = (ViewGroup)findViewById(R.id.appBar_layout);
            appappBarLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGE_DISAPPEARING, 0);

            ViewGroup viewGroup = (ViewGroup)findViewById(R.id.activity_editor);
            LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

        //init fragments
        String title = getIntent().getStringExtra(TITLE)==null?"":getIntent().getStringExtra(TITLE);
        String content = getIntent().getStringExtra(CONTENT)==null?"":getIntent().getStringExtra(CONTENT);
        editorFragment = EditorFragment.newInstance(title,content);
        previewFragment = PreviewFragment.newInstance();

        //init toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        toolsAdapter = new ToolsAdapter(getLayoutInflater());
        tools.setAdapter(toolsAdapter);

        //init viewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(markDownController==null)
                    return;
                if(position==1) {
                    tools.setVisibility(View.GONE);
                    previewFragment.setTitle(editorFragment.getTitle());
                    markDownController.preview();
                }
                else {
                    tools.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //显示/隐藏软键盘，在onPageSelected中调用会引起滑动动画冲突
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    editorView.requestFocus();
                    if (viewPager.getCurrentItem() == 1) {
                        imm.hideSoftInputFromWindow(editorView.getWindowToken(), 0);
                    }
                    /*else {
                        imm.showSoftInput(editorView, 0);
                    }*/
                }
            }
        });
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position)
                {
                    case 0:
                        return editorFragment;
                    case 1:
                        return previewFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

        });
    }

    public void setEditorView(MarkDownEditorView editorView)
    {
        this.editorView = editorView;
        if(editorView!=null&&previewView!=null&&toolsAdapter!=null)
            initMarkDownController();
    }

    public void setPreviewView(MarkDownPreviewView previewView)
    {
        this.previewView = previewView;
        if(editorView!=null&&previewView!=null&&toolsAdapter!=null)
            initMarkDownController();
    }

    private void initMarkDownController()
    {
        markDownController = new MarkDownController(editorView, previewView, toolsAdapter, false);
        markDownController.setOnPreInsertListener(this);
    }

    @Override
    public void onPreInsertImage() {

    }

    @Override
    public void onPreInsertLink() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_insert_link,null);
        final TextInputEditText name = (TextInputEditText)view.findViewById(R.id.linkName);
        final TextInputEditText url = (TextInputEditText)view.findViewById(R.id.linkUrl);
        new AlertDialog.Builder(this)
                .setTitle("插入链接")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(markDownController!=null)
                            markDownController.insertLink(new Pair<String, String>(name.getText()+"",url.getText()+""));
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    @Override
    public void onPreInsertTable() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_insert_table,null);
        final TextInputEditText row = (TextInputEditText)view.findViewById(R.id.row);
        final TextInputEditText column = (TextInputEditText)view.findViewById(R.id.column);
        new AlertDialog.Builder(this)
                .setTitle("插入表格")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(markDownController!=null)
                            markDownController.insertTable(new Pair<Integer, Integer>(
                                    Integer.parseInt(row.getText()+""),
                                    Integer.parseInt(column.getText()+"")));
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }
}
