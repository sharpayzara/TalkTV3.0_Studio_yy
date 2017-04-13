package com.sumavision.talktv2.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.StatementDialogActivity;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ShakeProgramData;
import com.sumavision.talktv2.bean.ShakeSub;
import com.sumavision.talktv2.components.ReportDialog;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShakeDianzanRequest;
import com.sumavision.talktv2.http.json.ShakeProgramDetailParser;
import com.sumavision.talktv2.http.json.ShakeProgramDetailRequest;
import com.sumavision.talktv2.http.json.ShakeProgramParser;
import com.sumavision.talktv2.http.json.ShakeProgramRequest;
import com.sumavision.talktv2.http.json.ShakeReportRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class ShakeProgramDetialFragment extends Fragment implements View.OnClickListener{

    private int programId;
    
    private View root;
    private RelativeLayout loadingLayout;
    private ProgressBar mProgress;
    private TextView errText;
    private ImageView img1,img2,img3,userIcon;
    private Button btn1,btn2,btn3;
    private TextView sourceText,urlText,videoTitleText,userNameText;
    private TextView stateText;
    private RelativeLayout layout1,layout2,layout3;
    private ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();
    private PlayerActivity playerActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_half_shake,container,false);
        initViews();
        programId = (int)getArguments().getLong("programId");
        getProgramInfo(programId);
        if (PreferencesUtils.getBoolean(getActivity(),null,Constants.kedaxunfei,false)){
            createBannerAd();
        }
        return root;
    }
    @Override
    public void onAttach(Activity activity) {
        playerActivity = (PlayerActivity) activity;
        super.onAttach(activity);
    }
    private void initViews() {
        loadingLayout = (RelativeLayout)root.findViewById(R.id.errLayout);
        mProgress = (ProgressBar)root.findViewById(R.id.progressBar);
        errText = (TextView)root.findViewById(R.id.err_text);
        errText.setOnClickListener(this);
        userIcon = (ImageView) root.findViewById(R.id.hs_user_pic);
        userNameText = (TextView) root.findViewById(R.id.hs_user_name);
        sourceText = (TextView) root.findViewById(R.id.pdhp_source_from);
        urlText = (TextView) root.findViewById(R.id.pdhp_url);
        videoTitleText = (TextView) root.findViewById(R.id.pdhp_name);
        img1 = (ImageView) root.findViewById(R.id.pdhp_praise_img);
        img2 = (ImageView) root.findViewById(R.id.pdhp_collect_img);
        img3 = (ImageView) root.findViewById(R.id.pdhp_cache_img);
        btn1 = (Button) root.findViewById(R.id.pdhp_praise);
        btn2 = (Button) root.findViewById(R.id.pdhp_collect);
        btn3 = (Button) root.findViewById(R.id.pdhp_cache);
        layout_ads = (FrameLayout) root.findViewById(R.id.hs_ad_layout);
        layout_ads.setVisibility(View.GONE);
        layout1 = (RelativeLayout) root.findViewById(R.id.pdhp_praise_layout);
        layout2 = (RelativeLayout) root.findViewById(R.id.pdhp_collect_layout);
        layout3 = (RelativeLayout) root.findViewById(R.id.pdhp_cache_layout);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        stateText = (TextView) root.findViewById(R.id.pdhp_statement);
        stateText.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
        SpannableString ss = new SpannableString("免责声明 ");
        ss.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        stateText.setText(ss);
        stateText.setOnClickListener(this);
        urlText.setOnClickListener(this);
    }

    private ShakeProgramDetailParser programParser = new ShakeProgramDetailParser();
    private ShakeProgramData data = new ShakeProgramData();
    public void getProgramInfo(int programId) {
        VolleyHelper.post(
                new ShakeProgramDetailRequest(programId,getActivity()).make(),
                new ParseListener(programParser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        if (programParser.errCode == JSONMessageType.SERVER_CODE_OK) {
                            data = programParser.program;
                            updateUI();
                        } else {
                            Log.i("msg", "getProgramInfo data error");
                        }
                        loadingLayout.setVisibility(View.GONE);
                    }
                }, new OnHttpErrorListener() {
                    @Override
                    public void onError(int code) {
                        loadingLayout.setVisibility(View.VISIBLE);
                        errText.setVisibility(View.VISIBLE);
                        mProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void updateUI() {
        if(data != null){
            userNameText.setText(data.director);
            sourceText.setText("视频来源："+data.area);
            urlText.setText("URL："+data.programSub.get(0).url);
            videoTitleText.setText(data.name);
            if (data.isZan){
                img1.setImageResource(R.drawable.zan_already);
                layout1.setClickable(false);
            }
            if (data.hasReported){
                img3.setImageResource(R.drawable.shake_report_already);
                layout3.setClickable(false);
            }
            if(data.programSub != null && data.programSub.size()>0){
                DownloadInfo dInfo = new DownloadInfo();
                dInfo.programId = programId;
                dInfo.subProgramId = data.programSub.get(0).id;
                if(AccessDownload.getInstance(getActivity()).isExists(dInfo)){
                    layout2.setClickable(false);
                    img2.setImageResource(R.drawable.shake_cache_already);
                }else{
                    layout2.setClickable(true);
                    layout2.setEnabled(true);
                }
                startPlay();
            }else{
                layout2.setClickable(false);
                img2.setImageResource(R.drawable.shake_cache_unable);
            }
            imageLoaderHelper.loadImage(userIcon, data.pic, R.drawable.icon);
        }
        
    }

    private void startPlay() {
        boolean autoPlay = PreferencesUtils.getBoolean(getActivity(),
                null, "half_auto", true);
        Intent intent = setIntentExtra(data.programSub.get(0));
        playerActivity.setVideoTitle();
        if (autoPlay){
            playerActivity.restartPlay(intent);
        }else {
            playerActivity.showStartplay();
        }
    }

    private void addPraise() {
        if (data.isZan) {
            showToast("您已点赞");
            img1.setImageResource(R.drawable.zan_already);
            layout1.setClickable(false);
            return;
        }
        final ResultParser rparser = new ResultParser();
        VolleyHelper.post(new ShakeDianzanRequest(getActivity(),(int) programId).make(), new ParseListener(rparser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                    showToast("点赞成功");
                    data.isZan = true;
                    img1.setImageResource(R.drawable.zan_already);
                    layout1.setClickable(false);
                } else {
                    if (rparser.errCode == 1 && "您已点赞!".equals(rparser.errMsg)){
                        showToast("点赞成功");
                        data.isZan = true;
                        img1.setImageResource(R.drawable.zan_already);
                        layout1.setClickable(false);
                    }else{
                        showToast(rparser.errMsg);
                    }
                }
            }
        }, null);
    }
    private void startCache() {
        if (data.programSub.get(0).url.contains("pickcode")){
            ToastHelper.showToast(getActivity(), "由于视频原因本片暂时只提供在线观看");
            layout2.setClickable(false);
            img2.setImageResource(R.drawable.shake_cache_unable);
            return;
        }
        try {
                AccessDownload accessDownload = AccessDownload
                        .getInstance(getActivity());
                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.programId = (int) programId;
                    downloadInfo.subProgramId = data.programSub.get(0).id;
                    downloadInfo.programPic = data.progPic;
                    if (!TextUtils.isEmpty(downloadInfo.programPic) && !downloadInfo.programPic.startsWith("http:")){
                        downloadInfo.programPic = Constants.picUrlFor + downloadInfo.programPic;
                    }
                    InternalExternalPathInfo internalExternalPathInfo = CommonUtils
                            .getInternalExternalPath(getActivity());
                    String path = null;

                    // 获取用户设置的存储路径，0外部存储，1内部存储
                    int type = PreferencesUtils.getInt(getActivity(), null,
                            "cache_path_type");
                    if (type == 0) {
                        if (internalExternalPathInfo.removableSDcard != null) {
                            path = internalExternalPathInfo.removableSDcard;
                        } else {
                            // 如果没有外置，则判断有没有内置的，如果有则设置默认路径为内置
                            if (internalExternalPathInfo.emulatedSDcard != null) {
                                PreferencesUtils.putInt(getActivity(), null,
                                        "cache_path_type", 1);
                                path = internalExternalPathInfo.emulatedSDcard;
                            } else {
                                Toast.makeText(getActivity(),
                                        "无外置sd卡，无法缓存，请修改存储路径为手机存储",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else {
                        if (internalExternalPathInfo.emulatedSDcard != null) {
                            path = internalExternalPathInfo.emulatedSDcard;
                        } else {
                            if (internalExternalPathInfo.removableSDcard != null) {
                                PreferencesUtils.putInt(getActivity(), null,
                                        "cache_path_type", 0);
                                path = internalExternalPathInfo.removableSDcard;
                            } else {
                                Toast.makeText(getActivity(),
                                        "无内置存储空间，无法缓存，请修改存储路径为SD卡",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    downloadInfo.sdcardDir = path;
                    downloadInfo.programName = TextUtils.isEmpty(data.name) ? "电视粉节目"
                                : data.name;
                    downloadInfo.initUrl = data.programSub.get(0).url+"-webparse";
                    downloadInfo.state = DownloadInfoState.WAITTING;
                    accessDownload.save(downloadInfo);
                ArrayList<DownloadInfo> downloadingInfos = accessDownload
                        .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
                if (downloadingInfos == null || downloadingInfos.size() == 0) {
                    ArrayList<DownloadInfo> waittings = accessDownload
                            .queryDownloadInfo(DownloadInfoState.WAITTING);
                    if (waittings != null && waittings.size() > 0) {
                        DownloadInfo downloadInfo1 = waittings.get(0);
                        downloadInfo1.state = DownloadInfoState.DOWNLOADING;
                        accessDownload.updateDownloadState(downloadInfo1);
                    }
                    startCacheService();
                }
                Toast.makeText(getActivity(), "请到缓存中心查看进度",
                        Toast.LENGTH_SHORT).show();
            layout2.setClickable(false);
            img2.setImageResource(R.drawable.shake_cache_already);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private void sendReport(String content) {
        
        final ResultParser rparser = new ResultParser();
        VolleyHelper.post(new ShakeReportRequest(getActivity(),(int) programId,content).make(), new ParseListener(rparser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                    showToast("感谢您的举报，我们会及时处理。");
                    data.hasReported = true;
                    layout3.setClickable(false);
                    img3.setImageResource(R.drawable.shake_report_already);
                } else {
                    if ("您已举报过此节目!".equals(rparser.errMsg)){
                        layout3.setClickable(false);
                        img3.setImageResource(R.drawable.shake_report_already);
                    }
                    showToast(rparser.errMsg);
                } 
            }
        }, null);
    }
    private ReportDialog dialog;
//    private String[] reportItems = {"广告欺诈","淫秽色情","反动政治","其他不和谐"};
    private void showReportDialog(){
        if(dialog == null){
            dialog = new ReportDialog(getActivity());
            dialog.setClicklistener(new ReportDialog.ClickListenerInterface() {
                @Override
                public void onReportItemClick() {
                    sendReport(dialog.content);
                }
            });
        }
        dialog.show();
    }
    private void startCacheService() {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.putExtra(DownloadService.APPNAME_KEY,
                getString(R.string.app_name));
        intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
        getActivity().startService(intent);
    }
    private void showToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pdhp_praise_layout:
                MobclickAgent.onEvent(getActivity(), "yjpbpdianzan");
                addPraise();
                break;
            case R.id.pdhp_collect_layout:
                MobclickAgent.onEvent(getActivity(), "yjpbphuancun");
                startCache();
                break;
            case R.id.pdhp_cache_layout:
                MobclickAgent.onEvent(getActivity(), "yjpbpjubao");
                if (data.hasReported) {
                    showToast("您已举报过");
                    layout3.setClickable(false);
                    img3.setImageResource(R.drawable.shake_report_already);
                    return;
                }else{
                    showReportDialog();
                }
                break;
            case R.id.pdhp_statement:
                startActivity(new Intent(getActivity(), StatementDialogActivity.class));
                break;
            case R.id.err_text:
                getProgramInfo(programId);
                break;
            case R.id.pdhp_url:
                Intent intent = new Intent(getActivity(), WebAvoidActivity.class);
                intent.putExtras(getActivity().getIntent());
                intent.putExtra(PlayerActivity.INTENT_NEEDAVOID,true);
                startActivity(intent);
                break;
        }
    }

    private Intent setIntentExtra(ShakeSub jishu) {
        Intent intent = playerActivity.getIntent();
        intent.putExtra("url", jishu.url);
        intent.putExtra("subid", jishu.id);
        intent.putExtra("playType", playerActivity.SHAKE_PLAY);
        intent.putExtra("title", jishu.name);
        intent.putExtra("id", programId);
        return intent;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.cancelRequest(Constants.shakeProgramDetail);
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }
    IFLYBannerAd bannerView;
    FrameLayout layout_ads;
    public void createBannerAd() {
        //此广告位为Demo专用，广告的展示不产生费用
        String adUnitId = "ECAEEB8439E3D544634FD6DC6F67ECAA";
        //创建旗帜广告，传入广告位ID
        bannerView = IFLYBannerAd.createBannerAd(getActivity(), adUnitId);
        //设置请求的广告尺寸
        bannerView.setAdSize(IFLYAdSize.BANNER);
        //设置下载广告前，弹窗提示
        bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "false");

        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
        //将广告添加到布局
        layout_ads.removeAllViews();
        layout_ads.addView(bannerView);
        layout_ads.invalidate();

    }

    IFLYAdListener mAdListener = new IFLYAdListener() {

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerView.showAd();
            layout_ads.setVisibility(View.VISIBLE);
            bannerView.invalidate();
            bannerView.requestFocus();
            AdStatisticsUtil.adCount(getActivity(),Constants.shakeHalf);
            Log.d("Ad_Android_Demo", "onAdReceive");
        }

        /**
         * 广告请求失败
         */
        @Override
        public void onAdFailed(AdError error) {
            Log.d("Ad_Android_Demo", "onAdFailed");
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            Log.d("Ad_Android_Demo", "onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            Log.d("Ad_Android_Demo", "onAdClose");
        }
    };
}
