package com.xilingyuli.markdowneditor;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xilingyuli.markdown.MarkDownPreviewView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviewFragment extends Fragment {

    private View view;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.content)
    MarkDownPreviewView contentView;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance() {
        PreviewFragment fragment = new PreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if(view!=null)
            return view;
        view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).setPreviewView(contentView);
        return view;
    }

    public void setTitle(String title)
    {
        titleView.setText(title);
    }

}
