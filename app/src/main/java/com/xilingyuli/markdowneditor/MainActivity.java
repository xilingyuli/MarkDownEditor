package com.xilingyuli.markdowneditor;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.xilingyuli.markdown.MarkDownController;
import com.xilingyuli.markdown.MarkDownEditorView;
import com.xilingyuli.markdown.MarkDownPreviewView;
import com.xilingyuli.markdown.OnPreInsertListener;
import com.xilingyuli.markdown.ToolsAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Markdown Blog Edit And Preview
 */
public class MainActivity extends AppCompatActivity implements OnPreInsertListener {

    public static String ROOT_PATH;
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

        ROOT_PATH = Environment.getExternalStorageDirectory()+ File.separator + "markdown" + File.separator;
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

    @OnClick(R.id.fab)
    public void onChangeClick()
    {
        if(viewPager!=null) {
            int index = 1 - viewPager.getCurrentItem();
            viewPager.setCurrentItem(index, false);  //设为true不能正常切换，原因待探寻
        }
    }

    @OnClick(R.id.save)
    public void onSaveClick()
    {
        saveFile();
    }

    public boolean saveFile()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"需要SD卡读写权限，请重试",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        if (editorFragment.getTitle().isEmpty())
        {
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        FileOutputStream fos = null;
        try {
            File dir = new File(ROOT_PATH);
            if(!dir.exists())
                dir.mkdir();
            File file = new File(ROOT_PATH+editorFragment.getTitle()+".md");
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(editorView.getText().toString().getBytes());
            fos.flush();
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this,"存储失败",Toast.LENGTH_SHORT).show();
            return false;
        }
        finally {
            try {
                if(fos!=null)
                    fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("是否保存为MarkDown文件?")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(saveFile())
                            MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK)
            if(markDownController!=null)
                markDownController.insertImage(data.getData().toString());
    }

    @Override
    public void onPreInsertImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);// Pick an item fromthe
        intent.setType("image/*");// 从所有图片中进行选择
        startActivityForResult(intent, 0);
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
