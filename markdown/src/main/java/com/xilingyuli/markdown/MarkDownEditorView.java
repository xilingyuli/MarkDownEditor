package com.xilingyuli.markdown;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by xilingyuli on 2017/2/28.
 */

public class MarkDownEditorView extends EditText {
    Pattern orderPattern = Pattern.compile("^[0-9]+\\. ");
    public MarkDownEditorView(Context context) {
        super(context);
        init();
    }

    public MarkDownEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkDownEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkDownEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //新增字符
                if(i1==0)
                {
                    String str = charSequence+"";
                    //新增的是回车符
                    if(!str.isEmpty()&&str.charAt(i)=='\n')
                    {
                        String lastLine = getLastLine();
                        if(lastLine.startsWith("* ")) {
                            MarkDownEditorView.this.getText().insert(i+1,"* ");
                        }
                        if(lastLine.startsWith("> ")) {
                            MarkDownEditorView.this.getText().insert(i+1,"> ");
                        }
                        if(orderPattern.matcher(lastLine).lookingAt())
                        {
                            int num = Integer.parseInt(lastLine.substring(0, lastLine.indexOf(".")))+1;
                            MarkDownEditorView.this.getText().insert(i+1,num+". ");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void header(int level) {
        String newStr = "######".substring(0, level) + " ";
        lineStyle(newStr);
    }

    public void insertLine()
    {
        String source = getText()+"";
        int start = getSelectionStart();

        String newStr = "---";
        if(start==0 || source.charAt(start-1)!='\n')
            newStr = "\n"+newStr;
        if(start==source.length() || source.charAt(start)!='\n')
            newStr += "\n";
        getText().insert(start, newStr);
    }

    public void bold()
    {
        textStyle("**");
    }

    public void italic()
    {
        textStyle("_");
    }

    public void strikethrough()
    {
        textStyle("~~");
    }

    public void unordered()
    {
        lineStyle("* ");
    }

    public void ordered()
    {
        String source = getText()+"";
        int begin = getLineBeginIndex();
        int end = getNextLineBeginIndex();
        String result = source.substring(begin, end);

        //取消标号
        if(orderPattern.matcher(result).lookingAt())
        {
            result = result.replaceFirst("[0-9]+\\. ","");
        }
        //添加标号
        else
        {
            int num = 1;
            String lastLine = getLastLine();
            if (orderPattern.matcher(lastLine).lookingAt()) {
                num = Integer.parseInt(lastLine.substring(0, lastLine.indexOf(".")))+1;
            }
            result = num+". "+result;
        }
        getText().replace(begin, end, result);
        setSelection(begin+result.length());
    }

    public void blockquote()
    {
        lineStyle("> ");
    }

    public void codeSingle()
    {
        textStyle("`");
    }

    public void code()
    {
        textStyle("\n```\n");
    }

    public void insertImage(String url)
    {
        insert("![image](" + url + ")");
    }

    public void insertLink(Pair<String, String> info)
    {
        insert("["+info.first+"]("+info.second+")");
    }

    public void insertTable(Pair<Integer,Integer> size)
    {
        int end = getNextLineBeginIndex();
        int row = size.first;
        int column = size.second;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < column; i++) {
            stringBuilder.append("| Header ");
        }
        stringBuilder.append("|\n");
        for (int i = 0; i < column; i++) {
            stringBuilder.append("|:----------:");
        }
        stringBuilder.append("|\n");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                stringBuilder.append("|            ");
            }
            stringBuilder.append("|\n");
        }
        String result = stringBuilder.toString();
        getText().insert(end, result);
        setSelection(end + result.length());
    }

    private void textStyle(String str)
    {
        int num = str.length();
        String source = getText()+"";
        int start = getSelectionStart();
        int end = getSelectionEnd();

        String result = source.substring(start, end);
        //取消样式
        if(source.substring(0,start).endsWith(str) && source.substring(end).startsWith(str)) {
            getText().replace(start - num, end + num, result);
            this.setSelection(start - num, end - num);
        }
        //添加样式
        else {
            getText().replace(start, end, str + result + str);
            this.setSelection(start + num);
        }
    }

    private void lineStyle(String str)
    {
        String source = getText()+"";
        int begin = getLineBeginIndex();
        int end = getNextLineBeginIndex();

        String result = source.substring(begin, end);

        //取消样式
        if(result.startsWith(str))
            result = result.replace(str,"");
        //添加样式
        else
            result = str + result;
        getText().replace(begin, end, result);
        int select = begin+result.length();
        if(result.endsWith("\n"))
            select--;
        setSelection(select);
    }

    private void insert(String str)
    {
        int selection = getSelectionStart();
        getText().insert(selection, str);
        setSelection(selection+str.length());
    }

    public String getLastLine()
    {
        String source = getText()+"";
        int end = source.substring(0,getSelectionStart()).lastIndexOf('\n')+1;
        int begin = (end==0?-1:source.substring(0, end-1).lastIndexOf('\n'))+1;
        return source.substring(begin, end);
    }

    public int getLineBeginIndex()
    {
        String source = getText()+"";
        return source.substring(0,getSelectionStart()).lastIndexOf('\n')+1;
    }

    public int getNextLineBeginIndex()
    {
        String source = getText()+"";
        int nextEnter = source.indexOf('\n',getSelectionStart());
        return nextEnter==-1?source.length():nextEnter+1;
    }

}
