package com.sumavision.talktv2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.VideoSourceAdapter;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.SourcePlatform;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PlatformSourceDialogFragment extends BaseDialogFragment {

//    private OnPlatformSourceSelectedListener mListener;

    public static PlatformSourceDialogFragment newInstance(List<SourcePlatform> data) {
        PlatformSourceDialogFragment fragment = new PlatformSourceDialogFragment(data);
        Bundle args = new Bundle();
        args.putInt("resId", R.layout.fragment_platform_source_dialog);
        fragment.setArguments(args);
        return fragment;
    }

    public PlatformSourceDialogFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public PlatformSourceDialogFragment(List<SourcePlatform> data) {
        platformDatas = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resId = getArguments().getInt("resId");
        }
    }
    private ListView platformList;
    public List<SourcePlatform> platformDatas = new ArrayList<SourcePlatform>();
    public VideoSourceAdapter platformAdapter;
    @Override
    protected void initViews(View view) {
        platformList = (ListView) view.findViewById(R.id.platform_list);
        platformAdapter = new VideoSourceAdapter(getActivity(),platformDatas);
        platformList.setAdapter(platformAdapter);
        platformList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (mListener != null) {
//                    mListener.onPlatformSelected(i);
//                }
                EventMessage message = new EventMessage("ProgramDetailHalfActivity");
                message.pos = i;
                EventBus.getDefault().post(message);
                dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
