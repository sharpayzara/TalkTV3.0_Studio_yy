package com.sumavision.talktv2.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.FeedbackAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.FeedbackQuestionData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.FeedbackDetailParser;
import com.sumavision.talktv2.http.json.FeedbackDetailRequest;
import com.sumavision.talktv2.http.listener.OnFeedbackListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/5/8.
 */
public class FeedbackFragment extends FocusLayoutFragment implements
        OnFeedbackListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private StaticGridView progblemGrid;
    private EditText contentText;
    private EditText userText, pnameTxt;
    private TextView questionTag,questionsText;
    private LinearLayout rcmdLayout;
    String[] problemArr;
    public static FeedbackFragment newInstance(){
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resId", R.layout.fragment_feedback_new);
        fragment.setArguments(bundle);
        return fragment;
    }
    public FeedbackFragment(){
        super();
        resId = R.layout.fragment_feedback_new;
    }

    @Override
    protected void initViews(View view) {
        initLoadingLayout();
        initStarsLayout();
        hideStarsLayout();
        rcmdLayout = (LinearLayout) view.findViewById(R.id.rcmd_layout);
        contentText = (EditText) view.findViewById(R.id.content_text);
        progblemGrid = (StaticGridView) view.findViewById(R.id.grid_problem);
        userText = (EditText) view.findViewById(R.id.edt_contact);
        pnameTxt = (EditText) view.findViewById(R.id.edt_video_name);
        view.findViewById(R.id.btn_submit).setOnClickListener(this);
        problemArr = getResources().getStringArray(R.array.problems);
        FeedbackAdapter adapter = new FeedbackAdapter(getActivity(), problemArr);
        progblemGrid.setAdapter(adapter);
        progblemGrid.setOnItemClickListener(this);
        questionsText = (TextView) view.findViewById(R.id.feedback_questions);
        questionTag = (TextView) view.findViewById(R.id.feedback_questions_tag);
        rcmdLayout.addView(headerView);
        getInfo();
    }
    private FeedbackDetailParser parser = new FeedbackDetailParser();
    public void getInfo(){
        showLoadingLayout();
        VolleyHelper.post(new FeedbackDetailRequest().make(), new ParseListener(parser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                if (parser.errCode == JSONMessageType.SERVER_CODE_OK){
                    updateDetailViews();
                }
            }
        }, new OnHttpErrorListener() {
            @Override
            public void onError(int code) {
                showErrorLayout();
            }
        });
    }

    @Override
    public void reloadData() {
        super.reloadData();
        getInfo();
    }

    private void updateDetailViews(){
        if (parser.listRecommend.size()>0){
            listRecommend = parser.listRecommend;
            updateStarsLayout(listRecommend);
            showStarsLayout();
            rcmdLayout.setVisibility(View.VISIBLE);
        }else{
            rcmdLayout.setVisibility(View.GONE);
        }
        if (parser.fqs.size()>0){
            StringBuilder sb = new StringBuilder();
            FeedbackQuestionData temp;
            for (int i=0; i<parser.fqs.size(); i++){
                temp = parser.fqs.get(i);
                sb.append(temp.question);
                sb.append("<br>");
                sb.append("<font color=#888888>"+temp.answer+"</font>");
                sb.append("<br>");
            }
            questionsText.setText(Html.fromHtml(sb.toString()));
        }else{
            questionsText.setVisibility(View.GONE);
            questionTag.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            commit();
        }
    }
    private void commit() {
        String content = contentText.getText().toString();
        String connect = userText.getText().toString();
        String source = CommentData.COMMENT_SOURCE;
        if (StringUtils.isEmpty(content) && problems.size() == 0) {
            DialogUtil.alertToast(getActivity(),
                    getString(R.string.input_problem));
            contentText.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), R.anim.shake_x));
        } else if (StringUtils.isEmpty(connect)) {
            DialogUtil.alertToast(getActivity(),
                    getString(R.string.input_contact));
            userText.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), R.anim.shake_x));
        } else {
            hideSoftPad();
            FeedbackData feedback = new FeedbackData();
            StringBuffer fcontent = new StringBuffer();
            if (problems.size() > 0) {
                fcontent.append("反馈问题:\n");
                for (String p : problems) {
                    fcontent.append(p).append("\n");
                }
            }
            if (!TextUtils.isEmpty(content)) {
                fcontent.append("用户问题:").append(content);
            }
            feedback.content = fcontent.toString();
            feedback.source = source;
            feedback.contactNum = connect;
            feedback.programName = pnameTxt.getText().toString();
            sendFeedBackInfo(feedback);
        }
    }

    private void hideSoftPad() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void sendFeedBackInfo(FeedbackData feedBack) {
        showLoadingLayout();
        VolleyUserRequest.feedback(feedBack, this, this);
    }

    @Override
    public void feedbackResult(int errCode) {
        hideLoadingLayout();
        switch (errCode) {
            case JSONMessageType.SERVER_CODE_ERROR:
                DialogUtil.alertToast(getActivity(),
                        getString(R.string.feedback_failed));
                break;
            case JSONMessageType.SERVER_CODE_OK:
                DialogUtil.alertToast(getActivity(),
                        getString(R.string.feedback_succeed));
                getActivity().finish();
                break;
            default:
                break;
        }

    }
    private ArrayList<String> problems = new ArrayList<String>();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ImageView checkedView = (ImageView) view.findViewById(R.id.imgv_checkd);
        checkedView
                .setVisibility((checkedView.getVisibility() == View.GONE) ? View.VISIBLE
                        : View.GONE);
        if (checkedView.getVisibility() == View.VISIBLE) {
            problems.add(problemArr[position]);
        } else {
            problems.remove(problemArr[position]);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.cancelRequest(Constants.feedbackAdd);
        VolleyHelper.cancelRequest(Constants.feedbackDetail);
    }
}
