package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;
import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.AlimamaHelper;
import com.sumavision.talktv2.adapter.JujiAdapter;
import com.sumavision.talktv2.adapter.ProgramCommentAdapter;
import com.sumavision.talktv2.adapter.ProgramHalfRecommendAdapter;
import com.sumavision.talktv2.adapter.ProgramHalfSourceAdapter;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.EpisodeEvent;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.bean.SubVideoData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.HorizontalListView2;
import com.sumavision.talktv2.components.LoginSendDialog;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.components.StaticListView;
import com.sumavision.talktv2.fragment.PlatformSourceDialogFragment;
import com.sumavision.talktv2.fragment.ProgramCacheFragment;
import com.sumavision.talktv2.fragment.ProgramRecommendFragment;
import com.sumavision.talktv2.fragment.ProgramSubFragment;
import com.sumavision.talktv2.fragment.RecommendFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.RankingUpdateEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ChaseDeleteRequest;
import com.sumavision.talktv2.http.json.ChaseProgramRequest;
import com.sumavision.talktv2.http.json.DianzanRequest;
import com.sumavision.talktv2.http.json.ProgramParser;
import com.sumavision.talktv2.http.json.ProgramRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.SendCommentRequestNew;
import com.sumavision.talktv2.http.json.SendForwardRequestNew;
import com.sumavision.talktv2.http.json.SendReplyRequestNew;
import com.sumavision.talktv2.http.json.SubStageListParser;
import com.sumavision.talktv2.http.json.SubStageListRequest;
import com.sumavision.talktv2.http.json.TalkListParser;
import com.sumavision.talktv2.http.json.TalkListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;

public class ProgramDetailHalfActivity extends Fragment implements OnClickListener {

    private PullToRefreshScrollView scrollView;
    private boolean autoPlay = true;
    private boolean skipWeb = false;//true 显示网页

    public ProgramData programData = new ProgramData();
    private ProgramParser programParser = new ProgramParser();
    private ProgramSubFragment subFragment;
    private ProgramCacheFragment cacheFragment;
    private ProgramRecommendFragment recommendFragment;

    private RelativeLayout taobaoLayout;
    private ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();
    //节目详情
    private TextView programSource;
    private TextView programStatement;
    private HorizontalListView2 subTagList;
    private ProgramHalfSourceAdapter subTagAdapter;
    private ArrayList<String> programSourceData = new ArrayList<String>();
    private TextView programName;
    private TextView programInfo0;
    private TextView programInfo1;
    private TextView programInfo2;
    private TextView programInfo3;
    private TextView programInfo4;
    private ImageView programPic;
    private TextView programDetail;
    private TextView programDetailTitle;
    private RelativeLayout praise;
    private RelativeLayout collect;
    private RelativeLayout cache;
    private ImageView praiseImg;
    private ImageView collectImg;
    private LinearLayout infoLayout;
    private LinearLayout infoPart1, infoPart2;
    private TextView infoMoreText;
    private ImageView infoMoreImg;

    //剧集
    private TextView episodeUpdate;
    private StaticListView episodeList;
    private StaticGridView episodeGrid;
    private ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();
    private JujiAdapter episodeAdapter;
    private Button episodeMore;
    public int currentEpisodePos = 0;
    //	public int subId;
    public ArrayList<JiShuData> currentJiShu = new ArrayList<JiShuData>();
    public int currentJiShuPos;
    //	public Spinner sourceSpinner;
//	public VideoSourceAdapter sourceAdapter;
    private PlatformSourceDialogFragment platformDialog;
    private TextView platText;
    private ImageView platImg;

    //推荐
    private RelativeLayout recommend;
    private StaticGridView recommendGrid;
    private ProgramHalfRecommendAdapter recommendAdapter;
    private ArrayList<VodProgramData> recommendPrograms;
    private TextView recommendNumber;
    private Button recommendMore;

    //评论
    public EditText commentInput;
    private TextView commentNumber;
    private StaticListView commentList;
    private Button commentSend;
    private ProgramCommentAdapter commentAdapter;
    private ArrayList<CommentData> commentData = new ArrayList<CommentData>();
    public final static int COMMENT_SEND = 0;
    public final static int COMMENT_FORWARD = 1;
    public final static int COMMENT_COMMENT = 2;
    public int commentType;

    private final int GRIDCOUNT = 15;
    private final int LISTCOUNT = 5;

    private View rootView;

    private PlayerActivity playerActivity;
    private BVideoView player;

//	public int point;

    public int clickStage = -1;
    public int clickPlatformId = -1;
    //	public int mProgramId;
    public boolean programLoadOk, videoLoadOk;
    public boolean platformChanged;
    public boolean needRefreshCommon;

    @Override
    public void onAttach(Activity activity) {
        playerActivity = (PlayerActivity) activity;
        player = playerActivity.getMediaPlayer();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.program_detail_half_activity, container, false);
        accessDownload = AccessDownload.getInstance(getActivity());
        getExtras();
        initView();
        initEvents();
        loadingLayout.setVisibility(View.VISIBLE);
        getProgramInfo((int) programData.programId, curVideo.subId, true);
        return rootView;
    }

    private void getExtras() {
        Bundle bundle = getArguments();
        programData.programId = bundle.getLong("programId");
        curVideo.subId = bundle.getInt("subId");
//		point = bundle.getInt("point");
        Log.i("mylog", "id:" + bundle.getLong("programId"));
        Log.i("mylog", "point:" + bundle.getLong("point"));
        if (bundle.containsKey("fromNotification")) {
            cancelNotification();
        }
        autoPlay = PreferencesUtils.getBoolean(getActivity(),
                null, "half_auto", true);
//		subId = getLogInt();
    }

    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = (int) programData.programId;
        manager.cancel(notificationId);
    }

    public void getProgramInfo(int programId, final int subId, final boolean play) {
        VolleyHelper.post(
                new ProgramRequest(getActivity(), programId, subId).make(),
                new ParseListener(programParser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        programLoadOk = true;
                        scrollView.onRefreshComplete();
                        if (programParser.errCode == JSONMessageType.SERVER_CODE_OK) {
                            programData = programParser.program;
                            if (programData.programId <= 0) {
                                loadError();
                                return;
                            }
                            updateProgramView();
                            updateTaobaoAdvertise(programParser.advertise);
                            updateRecommendView();
                            updateCommentView(true);
                            updateEpisodeView(0, play);
                            if (subId <= 0) {
                                needRefreshCommon = true;
                            }
                            scrollView.getRefreshableView().scrollTo(0, 0);
                            loadingLayout.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        } else {
                            loadError();
                        }
                        System.gc();
                    }
                }, new OnHttpErrorListener() {
                    @Override
                    public void onError(int code) {
                        loadError();
                    }
                });
    }

    private void loadError() {
        loadingLayout.setVisibility(View.VISIBLE);
        mErr.setVisibility(View.VISIBLE);
        mPb.setVisibility(View.GONE);
        scrollView.setVisibility(View.INVISIBLE);
    }

    public int getClickStage() {
        return clickStage == -1 ? curVideo.stage : clickStage;
    }

    public int getClickPlatformId() {
        return clickPlatformId == -1 ? curVideo.platformId : clickPlatformId;
    }

    public void getCommentData(int topicId, final int first, int count) {
        final TalkListParser tparser = new TalkListParser();
        VolleyHelper.post(new TalkListRequest(topicId, first, count).make(), new ParseListener(tparser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                scrollView.onRefreshComplete();
                if (tparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                    programData.comment = tparser.commentData;
                    programData.talkCount = tparser.talkCount;
                    if (first == 0) {
                        updateCommentView(true);
                    } else {
                        updateCommentView(false);
                    }
                    if (tparser.commentData != null && tparser.commentData.size() < 10) {
                        scrollView.setMode(Mode.PULL_FROM_START);
                    } else {
                        scrollView.setMode(Mode.BOTH);
                    }
                }
            }
        }, null);
    }

    private boolean sendingComment = false;

    public void sendComment(int type) {
        if (sendingComment) {
            return;
        }
        final ResultParser rparser = new ResultParser();
        if (currentJiShu == null || currentJiShu.size() <= currentJiShuPos) {
            return;
        }

        sendingComment = true;
        if (type == COMMENT_COMMENT) {
            VolleyHelper.post(new SendReplyRequestNew().make(), new ParseListener(rparser) {
                @Override
                public void onParse(BaseJsonParser parser) {
                    if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                        getCommentData(currentJiShu.get(currentJiShuPos).topicId, 0, 10);
//						getCommentData(jiShuDatas.get(currentEpisodePos).topicId, 0, 10);
                        commentInput.setText("");
                        commentInput.setHint("说点什么吧");
                        commentType = COMMENT_SEND;
                    }
                    sendingComment = false;
                }
            }, null);
        } else if (type == COMMENT_FORWARD) {
            VolleyHelper.post(new SendForwardRequestNew().make(), new ParseListener(rparser) {
                @Override
                public void onParse(BaseJsonParser parser) {
                    if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                        getCommentData(currentJiShu.get(currentJiShuPos).topicId, 0, 10);
//						getCommentData(jiShuDatas.get(currentEpisodePos).topicId, 0, 10);
                        commentInput.setText("");
                        commentInput.setHint("说点什么吧");
                        commentType = COMMENT_SEND;
                    }
                    sendingComment = false;
                }
            }, null);
        } else {
            VolleyHelper.post(new SendCommentRequestNew(currentJiShu.get(currentJiShuPos).topicId).make(), new ParseListener(rparser) {
                @Override
                public void onParse(BaseJsonParser parser) {
                    if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                        getCommentData(currentJiShu.get(currentJiShuPos).topicId, 0, 10);
//						getCommentData(jiShuDatas.get(currentEpisodePos).topicId, 0, 10);
                        commentInput.setText("");
                        commentInput.setHint("说点什么吧");
                        commentType = COMMENT_SEND;
                    }
                    sendingComment = false;
                }
            }, null);
        }
    }

    private void initView() {
        initLoadingLayout();
        scrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.scrollview);
        scrollView.setMode(Mode.BOTH);
        scrollView.setVisibility(View.INVISIBLE);
        initProgramView();
        initEpisodeView();
        initRecommendView();
        initCommentView();
        rootView.findViewById(R.id.fragment_layout).setOnClickListener(null);
    }

    public View loadingLayout;
    ProgressBar mPb;
    TextView mErr;

    private void initLoadingLayout() {
        loadingLayout = rootView.findViewById(R.id.program_loading);
        mPb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mErr = (TextView) rootView.findViewById(R.id.err_text);
        mErr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getProgramInfo((int) programData.programId, curVideo.subId, true);
                mErr.setVisibility(View.GONE);
                mPb.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initProgramView() {
//		program = (RelativeLayout) rootView.findViewById(R.id.pdhp_program);
//		programSource = (TextView) rootView.findViewById(R.id.pdhp_source);
        programStatement = (TextView) rootView.findViewById(R.id.pdhp_statement);
        subTagList = (HorizontalListView2) rootView.findViewById(R.id.pdhp_sourcelist);
        programName = (TextView) rootView.findViewById(R.id.pdhp_name);
        programInfo0 = (TextView) rootView.findViewById(R.id.pdhp_text0);
        programInfo1 = (TextView) rootView.findViewById(R.id.pdhp_text1);
        programInfo2 = (TextView) rootView.findViewById(R.id.pdhp_text2);
        programInfo3 = (TextView) rootView.findViewById(R.id.pdhp_text3);
        programInfo4 = (TextView) rootView.findViewById(R.id.pdhp_text4);
        programPic = (ImageView) rootView.findViewById(R.id.pdhp_pic);
        programDetail = (TextView) rootView.findViewById(R.id.pdhp_detail);
        programDetailTitle = (TextView) rootView.findViewById(R.id.pdhp_dtitle);
        praise = (RelativeLayout) rootView.findViewById(R.id.pdhp_praise_layout);
        collect = (RelativeLayout) rootView.findViewById(R.id.pdhp_collect_layout);
        cache = (RelativeLayout) rootView.findViewById(R.id.pdhp_cache_layout);
        praiseImg = (ImageView) rootView.findViewById(R.id.pdhp_praise_img);
        collectImg = (ImageView) rootView.findViewById(R.id.pdhp_collect_img);
        infoLayout = (LinearLayout) rootView.findViewById(R.id.pdhd_info_layout);
        infoPart1 = (LinearLayout) rootView.findViewById(R.id.pdhp_info_part1);
        infoPart2 = (LinearLayout) rootView.findViewById(R.id.pdhp_info_part2);
        infoMoreText = (TextView) rootView.findViewById(R.id.pdhp_title_more);
        infoMoreImg = (ImageView) rootView.findViewById(R.id.pdhp_title_arrow);
        subTagAdapter = new ProgramHalfSourceAdapter(getActivity(), programSourceData);
        subTagList.setAdapter(subTagAdapter);
        programStatement.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
        SpannableString ss = new SpannableString("免责声明 ");
        ss.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        programStatement.setText(ss);
        layout_ads = (FrameLayout) rootView.findViewById(R.id.hs_ad_layout);
        layout_ads.setVisibility(View.GONE);
        img_ads = (ImageView) rootView.findViewById(R.id.hs_ad_image);
        img_ads.setOnClickListener(this);
        Random random = new Random();
        int type = random.nextInt(20)%4;
        Log.e("msg_type",""+type);
        if (!PreferencesUtils.getBoolean(getActivity(), null, Constants.kugouHalf, false)
                || (PreferencesUtils.getBoolean(getActivity(), null, Constants.kedaxunfei, false) && type <2)) {
            createBannerAd();
        } else if (!PreferencesUtils.getBoolean(getActivity(), null, Constants.kedaxunfei, false)
                || (PreferencesUtils.getBoolean(getActivity(), null, Constants.kugouHalf, false) && type >1)){
            if (type == 3){
                img_ads.setImageResource(R.drawable.pdh_ad_img2);
            } else {
                img_ads.setImageResource(R.drawable.pdh_ad_img1);
            }
            img_ads.setVisibility(View.VISIBLE);
            layout_ads.setVisibility(View.VISIBLE);
        }
    }

    private void initEpisodeView() {
//		episode = (RelativeLayout) rootView.findViewById(R.id.pdhe_episode);
        episodeUpdate = (TextView) rootView.findViewById(R.id.pdhp_statement);
        episodeList = (StaticListView) rootView.findViewById(R.id.pdhe_listview);
        episodeGrid = (StaticGridView) rootView.findViewById(R.id.pdhe_gridview);
        episodeMore = (Button) rootView.findViewById(R.id.pdhe_more);
//		sourceSpinner = (Spinner) rootView.findViewById(R.id.pdhe_source_spinner);
        platImg = (ImageView) rootView.findViewById(R.id.pdhe_plat_img);
        platText = (TextView) rootView.findViewById(R.id.pdhe_plat_text);
    }

    private void initCommentView() {
//		comment = (RelativeLayout) rootView.findViewById(R.id.pdhc_comment);
        commentInput = (EditText) rootView.findViewById(R.id.pdhc_input);
        commentNumber = (TextView) rootView.findViewById(R.id.pdhc_number);
        commentList = (StaticListView) rootView.findViewById(R.id.pdhc_listview);
        commentSend = (Button) rootView.findViewById(R.id.pdhc_send);
        commentAdapter = new ProgramCommentAdapter(getActivity(), commentData, this);
        commentList.setAdapter(commentAdapter);
        commentList.setFocusable(false);
        commentList.setFocusableInTouchMode(false);
    }

    private void initRecommendView() {
        recommend = (RelativeLayout) rootView.findViewById(R.id.pdhr_recommend);
        recommendGrid = (StaticGridView) rootView.findViewById(R.id.pdhr_listview);
        recommendPrograms = new ArrayList<VodProgramData>();
        recommendNumber = (TextView) rootView.findViewById(R.id.pdhr_number);
        recommendMore = (Button) rootView.findViewById(R.id.pdhr_more);
        recLine = rootView.findViewById(R.id.half_recommend_line);
    }
    private View taobaoLine,recLine;

    private void initEvents() {
        initProgramEvent();
        initEpisodeEvent();
        initRecommendEvent();
        initCommentEvent();
        EventBus.getDefault().register(this);
        scrollView.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {
                getProgramInfo((int) programData.programId, curVideo.subId, false);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {
                if (jiShuDatas != null && jiShuDatas.size() > 0) {
                    getCommentData(jiShuDatas.get(currentEpisodePos).topicId, commentData.size(), 10);
                } else {
                    scrollView.setMode(Mode.PULL_FROM_START);
                }
            }
        });
    }

    public SubStageListParser subStageParser = new SubStageListParser();
    boolean isExpand;

    private void initProgramEvent() {
        infoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (programData.detailType == 0 || programData.detailType == 3) {
                    return;
                }
                if (!isExpand) {
                    expandProgramDetail();
                    infoMoreImg.setImageResource(R.drawable.arrow_up);
                } else {
                    foldProgramDetail();
                    infoMoreImg.setImageResource(R.drawable.arrow_down);
                }
                isExpand = !isExpand;
            }
        });
        programStatement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openStatement();
            }
        });

        subTagList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (getClickStage() == position && !platformChanged) {
                    return;
                }
                clickStage = position;
                getSubStageList(programData.programId, getClickPlatformId(), getClickStage(), false);
                updateTagView(position);
            }
        });
        collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				new TvBaiduPushMessageReceiver().parseMessage(getActivity(), "{\"push\":{\"text\":\"看吴奇隆抗战新剧\",\"programId\":54216,\"title\":\"寒冬\",\"pushPic\":\"\"}}");
                if (UserNow.current().userID <= 0) {
//					showToast("请先登录");
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                if (programData.isChased) {
                    deleteCollect();
                } else {
                    addCollect();
                }
            }
        });
        praise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				new TvBaiduPushMessageReceiver().parseMessage(getActivity(), "{\"push\":{\"text\":\"战狼活动2\",\"activityId\":176}}");
                addPraise();
            }
        });
        cache.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCacheView();
            }
        });
    }

    private void initEpisodeEvent() {
        episodeMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addEpisodeView();
            }
        });
        episodeList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = (int) id;
                episodeAdapter.setSelectedPosition(pos);
                episodeAdapter.notifyDataSetChanged();
                if (curVideo.subId == jiShuDatas.get(pos).id) {
                    return;
                }
                if (getClickStage() == subTagAdapter.getCount() - 1 || subTagAdapter.getCount() < 2) {
                    setLastFlag(true);
                } else {
                    setLastFlag(false);
                }
                setCurrentJuji(jiShuDatas, pos);
//				getCommentData(jiShuDatas.get(pos).topicId, 0, 10);
                startPlay(currentJiShu, currentJiShuPos);
                //更改当前platform和stage
                if (platformChanged && clickPlatformId != -1) {
                    curVideo.platformId = getClickPlatformId();
                }
                curVideo.stage = getClickStage();
            }
        });
        episodeGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = (int) id;
                episodeAdapter.setName(null);
                episodeAdapter.setSelectedPosition(pos);
                episodeAdapter.notifyDataSetChanged();
                if (curVideo.subId == jiShuDatas.get(pos).id) {
                    return;
                }
                if (getClickStage() == subTagAdapter.getCount() - 1 || subTagAdapter.getCount() < 2) {
                    setLastFlag(true);
                } else {
                    setLastFlag(false);
                }
                setCurrentJuji(jiShuDatas, pos);
//				getCommentData(jiShuDatas.get(pos).topicId, 0, 10);
                startPlay(currentJiShu, currentJiShuPos);
                //更改当前platform和stage
                if (platformChanged && clickPlatformId != -1) {
                    curVideo.platformId = getClickPlatformId();
                }
                curVideo.stage = getClickStage();
            }
        });
        platImg.setOnClickListener(this);
        platText.setOnClickListener(this);
    }

    public void setLastFlag(boolean isLastStage) {
        playerActivity.getIntent().putExtra("isLastStage", isLastStage);
    }

    private void initRecommendEvent() {
        recommendMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecommendView();
            }
        });
        recommendGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                VodProgramData data = recommendPrograms.get(position);
                int programId = Integer.parseInt(data.id);
                if (programId == 0 || programId == (int) programData.programId) {
                    return;
                }

                loadingLayout.setVisibility(View.VISIBLE);
                playerActivity.switchEpisode = true;
                programData.programId = programId;
//				subId = getLogInt();
                clickPlatformId = -1;
                platformChanged = false;
                currentJiShuPos = 0;
                subTagAdapter.setSelectedPos(0);
                int tempId = 0;
                if (programData.pType == 1 || programData.pType == 11) {
                    tempId = PreferencesUtils.getInt(getActivity(), Constants.SP_PLAY_RECORD, "" + programData.programId, 0);
                }
                switchVideo((int) programData.programId, tempId, programParser.recVersion);
            }
        });
    }

    public void switchVideo(int programId, int subId, String version) {
        videoLoadOk = false;
        programLoadOk = false;
        playerActivity.switchEpisode = true;
        playerActivity.getIntent().removeExtra("subList");
        playerActivity.stopPreLoading();
        playerActivity.mediaControllerFragment.getSubVideoInfo(programId, subId, 0, 3, version);
        getProgramInfo(programId, subId, true);
    }
    public void switchVideo(int programId, int subId){
        switchVideo(programId, subId, "");
    }

    private void initCommentEvent() {
        commentSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentData.current().content = commentInput.getText().toString();
                if (TextUtils.isEmpty(CommentData.current().content)) {
                    commentInput.setHint("说点什么吧");
                    commentType = COMMENT_SEND;
                } else {
                    if (UserNow.current().userID != 0) {
                        sendComment(commentType);
                        hideCommentInput(commentInput);
                    } else {
                        showLogin();
                    }
                }
            }
        });
    }

    private void showLogin() {
        final LoginSendDialog dialog = new LoginSendDialog(getActivity());
        dialog.setClicklistener(new LoginSendDialog.ClickListenerInterface() {

            @Override
            public void doConfirm() {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                dialog.dismiss();
            }

            @Override
            public void doCancel() {
                sendComment(commentType);
                hideCommentInput(commentInput);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //更新评论
    private void updateCommentView(boolean clear) {
        if (clear) {
            commentData.clear();
        }
        if (programData.comment != null) {
            commentData.addAll(programData.comment);
        }
        if (commentData.size() < 10) {
            scrollView.setMode(Mode.PULL_FROM_START);
        } else {
            scrollView.setMode(Mode.BOTH);
        }
        commentAdapter.notifyDataSetChanged();
        commentNumber.setText(programData.talkCount + "条评论");
    }

    private void updateProgramView() {
        programName.setText(programData.name);
        if (programData.detailType == 0) {
            infoPart1.setVisibility(View.GONE);
            infoPart2.setVisibility(View.GONE);
            infoMoreText.setVisibility(View.GONE);
            infoMoreImg.setVisibility(View.GONE);
        } else if (programData.detailType == 1) {
            infoPart1.setVisibility(View.GONE);
            infoPart2.setVisibility(View.GONE);
        } else if (programData.detailType == 2) {
            infoPart1.setVisibility(View.VISIBLE);
            infoPart2.setVisibility(View.GONE);
        } else if (programData.detailType == 3) {
            infoPart1.setVisibility(View.VISIBLE);
            infoPart2.setVisibility(View.VISIBLE);
        }
        programInfo0.setText("主演：" + getRealText(programData.actors));
        programInfo1.setText("导演：" + getRealText(programData.director));
        programInfo2.setText("时间：" + getRealText(programData.time));
        programInfo3.setText("类型：" + getRealText(programData.contentType));
        programInfo4.setText("地区：" + getRealText(programData.region));
        loadImage(programPic, programData.pic, R.drawable.aadefault);
        getActivity().getIntent().putExtra("programPic",programData.pic);
        programDetail.setText(programData.detail);
        if (programData.stageTag != null && programData.stageTag.size() > 0) {
            programSourceData.clear();
            programSourceData.addAll(programData.stageTag);
            updateTagView(programData.stage);
            subTagList.setVisibility(View.VISIBLE);
        } else {
            subTagList.setVisibility(View.GONE);
        }
        if (programData.platformList != null && programData.platformList.size() > 0) {
            platformDialog = PlatformSourceDialogFragment.newInstance(programData.platformList);
//			sourceAdapter = new VideoSourceAdapter(getActivity(),programData.platformList);
//			sourceSpinner.setAdapter(sourceAdapter);
//			sourceSpinner.setVisibility(View.VISIBLE);
//			sourceSpinner.setSelection(currentPlatform,true);
//			sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//				@Override
//				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//					getSubStageList(mProgramId,sourceAdapter.getItem(i).id,clickStage,false);
//					clickPlatform = i;
//				}
//				@Override
//				public void onNothingSelected(AdapterView<?> adapterView) {
//				}
//			});
        } else {
//			sourceSpinner.setVisibility(View.GONE);
        }
//		currentPlatform = 0;
        if (programData.isZan) {
            praiseImg.setImageResource(R.drawable.zan_already);
        } else {
            praiseImg.setImageResource(R.drawable.zan_normal);
        }
        if (programData.isChased) {
            collectImg.setImageResource(R.drawable.program_fav_already);
        } else {
            collectImg.setImageResource(R.drawable.programhalf_fav_normal);
        }
        skipWeb = programData.skipWeb == 1 ? true : false;
        getActivity().getIntent().putExtra("ptype", programData.pType);
        normalOrder = programData.order == 0 ? true : false;
        getActivity().getIntent().putExtra("normalOrder", normalOrder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (platformDialog != null && platformDialog.isVisible()) {
            platformDialog.dismiss();
        }
    }

    private String getRealText(String str) {
        return TextUtils.isEmpty(str) ? "暂无" : str;
    }

    private void updateRecommendView() {
        recommendPrograms.clear();
        if (programData.recommendPrograms != null && programData.recommendNumber > 0) {
            recommendPrograms.addAll(programData.recommendPrograms);
            recommendAdapter = new ProgramHalfRecommendAdapter(getActivity(), recommendPrograms);
            recommendGrid.setAdapter(recommendAdapter);
            recommendGrid.setVisibility(View.VISIBLE);
            recommendMore.setText("共" + programData.recommendNumber + "条推荐，点击查看更多");
            if (programData.recommendNumber > 4) {
                recommendMore.setVisibility(View.VISIBLE);
            } else {
                recommendMore.setVisibility(View.GONE);
            }
        } else {
            recommend.setVisibility(View.GONE);
            recommendGrid.setVisibility(View.GONE);
            recommendMore.setVisibility(View.GONE);
            recLine.setVisibility(View.GONE);
        }
    }

    private void updateEpisodeView(int position, boolean start) {
//		currentPlayPos = -1;
        Log.i("mylog", "position = " + position);
        String str = "全部剧集" + (TextUtils.isEmpty(programData.update) ? "" : ("(" + programData.update + ")"));
        episodeMore.setText(str);
        if (programData != null && programData.platformList != null
                && programData.platformList.size() > position) {
            if (videoLoadOk || playerActivity.getDataError) {
                if (playerActivity.getDataError) {
                    curVideo.platformId = programData.platformList.get(0).id;
                    isNative = programData.platformList.get(0).isNative;
                }
                if (!platformChanged) {
                    getSubStageList(programData.programId, curVideo.platformId, curVideo.stage, start);
                    updatePlatformView(curVideo.platformId);
                    updateTagView(curVideo.stage);
                } else {
                    if (clickPlatformId == curVideo.platformId) {
                        getSubStageList(programData.programId, curVideo.platformId, curVideo.stage, start);
                    } else {
                        getSubStageList(programData.programId, getClickPlatformId(), getClickStage(), start);
                    }
                    updatePlatformView(getClickPlatformId());
                    updateTagView(getClickStage());
                }
            }
        } else {
            currentEpisodePos = 0;
            episodeMore.setVisibility(View.GONE);
            jiShuDatas.clear();
            if (programData.pType != ProgramData.TYPE_TV
                    && programData.pType != ProgramData.TYPE_DONGMAN) {
                episodeAdapter = new JujiAdapter(getActivity(), programData.subType == 2 ? JujiAdapter.TYPE_PIC : JujiAdapter.TYPE_MOVIE, jiShuDatas);
                episodeList.setAdapter(episodeAdapter);
                episodeList.setVisibility(View.VISIBLE);
            } else {
                episodeAdapter = new JujiAdapter(getActivity(), JujiAdapter.TYPE_TV, jiShuDatas);
                episodeGrid.setAdapter(episodeAdapter);
                episodeGrid.setVisibility(View.VISIBLE);
            }
        }
        if (start) {
//			startPlayAuto(currentJiShu, currentEpisodePos);
        }
    }

    private int getPlatformPos(int platformId) {
        int size = programData.platformList.size();
        for (int i = 0; i < size; i++) {
            if (programData.platformList.get(i).id == platformId) {
                return i;
            }
        }
        return 0;
    }

    private void setJujiViews(List<JiShuData> datas, boolean setPosition) {
        jiShuDatas.clear();
        jiShuDatas.addAll(datas);

        if (jiShuDatas.size() < programData.subCount) {
            episodeMore.setVisibility(View.VISIBLE);
        } else {
            episodeMore.setVisibility(View.GONE);
        }
//		if(start){
//			setCurrentJuji(jiShuDatas, currentEpisodePos);
//		}
        if (programData.pType != ProgramData.TYPE_TV
                && programData.pType != ProgramData.TYPE_DONGMAN) {
            filterCacheInfo(jiShuDatas, programData);
            episodeAdapter = new JujiAdapter(getActivity(), programData.subType == 2 ? JujiAdapter.TYPE_PIC : JujiAdapter.TYPE_MOVIE, jiShuDatas);
            episodeList.setAdapter(episodeAdapter);
            episodeList.setVisibility(View.VISIBLE);
            episodeGrid.setVisibility(View.GONE);
        } else {
            filterCacheInfo(jiShuDatas, programData);
            episodeAdapter = new JujiAdapter(getActivity(), JujiAdapter.TYPE_TV, jiShuDatas);
            episodeGrid.setAdapter(episodeAdapter);
            episodeGrid.setVisibility(View.VISIBLE);
            episodeList.setVisibility(View.GONE);
            episodeAdapter.notifyDataSetChanged();
        }
        int index = exchangeSubidToPosition(jiShuDatas);
        episodeAdapter.setSelectedPosition(index);
        episodeAdapter.notifyDataSetChanged();
        if (setPosition) {
            currentEpisodePos = index >= 0 ? index : 0;
        }
    }

    List<JiShuData> mSubList = new ArrayList<JiShuData>();
    boolean normalOrder = true;

    /*
    获取分段剧集列表
     */
    private void getSubStageList(long programId, int platformId, final int stage, final boolean toPlayer) {
        VolleyHelper.post(new SubStageListRequest(programId,
                        platformId, stage).make(),
                new ParseListener(subStageParser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        if (subStageParser.subList != null && subStageParser.subList.size() > 0) {
                            mSubList = subStageParser.subList;
                            normalOrder = subStageParser.normalOrder;
                            setJujiViews(mSubList, toPlayer);
                            if (toPlayer) {
                                setCurrentJuji(jiShuDatas, currentEpisodePos);
                                setJuJiExtra(currentJiShu, currentJiShuPos, subStageParser.normalOrder);
                                if (stage == subTagAdapter.getCount() - 1 || subTagAdapter.getCount() < 2) {
                                    setLastFlag(true);
                                } else {
                                    setLastFlag(false);
                                }
                            }
//							if (stage == 0){
                            if (subStageParser.tags != null && subStageParser.tags.size() > 0) {
                                programSourceData.clear();
                                programSourceData.addAll(subStageParser.tags);
                                updateTagView(stage);
                                if (subTagAdapter.getCount() > 1) {
                                    subTagList.setVisibility(View.VISIBLE);
                                } else {
                                    subTagList.setVisibility(View.GONE);
                                }
                            } else {
                                subTagList.setVisibility(View.GONE);
                            }
                            if (needRefreshCommon) {
                                currentJiShuPos = exchangeSubidToPosition(currentJiShu);
                                if (currentJiShu != null && currentJiShuPos >= 0 && currentJiShuPos < currentJiShu.size()) {
                                    getCommentData(currentJiShu.get(currentJiShuPos).topicId, 0, 10);
                                }
                                needRefreshCommon = false;
                            }
//							}
                        }
                    }
                }, new OnHttpErrorListener() {
                    @Override
                    public void onError(int code) {

                    }
                });
    }

    private int exchangeSubidToPosition(List<JiShuData> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == curVideo.subId) {
                return i;
            }
        }
        return -1;
    }

    //	public void setSubid(int subId) {
//		this.subId = subId;
//	}
//	int platformId;
    private void addEpisodeView() {
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
        subFragment = ProgramSubFragment.newInstance(getClickPlatformId(), curVideo.subId);
        transaction.addToBackStack(null).replace(R.id.fragment_layout, subFragment, "content").commit();
    }

    private void addCacheView() {
//		if (clickPlatform > programData.platformList.size() - 1) {
//			return;
//		}
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
        cacheFragment = ProgramCacheFragment.newInstance(getClickPlatformId(),isNative);
        transaction.replace(R.id.fragment_layout, cacheFragment, "content").addToBackStack(null)
                .commit();
    }

    private void addRecommendView() {
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
        recommendFragment = ProgramRecommendFragment.newInstance();
        transaction.addToBackStack(null).replace(R.id.fragment_layout, recommendFragment, "content").commit();
    }

    private void changeCollectStatus() {
        if (programData.isChased) {
            collectImg.setImageResource(R.drawable.program_fav_already);
        } else {
            collectImg.setImageResource(R.drawable.programhalf_fav_normal);
        }
    }

    private void deleteCollect() {
        final ResultParser rparser = new ResultParser();
        VolleyHelper.post(new ChaseDeleteRequest(""
                + programData.programId).make(), new ParseListener(
                rparser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                    programData.isChased = false;
                    changeCollectStatus();
                    showToast("取消收藏成功");
                } else {
                    showToast(rparser.errMsg);
                }
            }
        }, null);
    }

    private void addCollect() {
        final ResultParser rparser = new ResultParser();
        VolleyHelper.post(new ChaseProgramRequest((int) programData.programId).make(),
                new ParseListener(rparser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                            programData.isChased = true;
                            changeCollectStatus();
                            showToast("收藏成功");
                        } else {
                            showToast(rparser.errMsg);
                        }
                    }
                }, null);
    }

    private void addPraise() {
        if (programData.isZan) {
            showToast("您已点赞");
            return;
        }
        final ResultParser rparser = new ResultParser();
        int ADD = 1;
        VolleyHelper.post(new DianzanRequest(getActivity(), (int) programData.programId, ADD).make(), new ParseListener(rparser) {
            @Override
            public void onParse(BaseJsonParser parser) {
                if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
                    showToast("点赞成功");
                    programData.isZan = true;
                    EventBus.getDefault().post(new RankingUpdateEvent());
                    praiseImg.setImageResource(R.drawable.zan_already);
                } else {
                    showToast(rparser.errMsg);
                }
            }
        }, null);
    }

    private void showToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
        }
    }

    public void setProgramId(int id) {
        programData.programId = id;
    }

    public void onEvent(EpisodeEvent e) {
        PreferencesUtils.putInt(getActivity(), Constants.SP_PLAY_RECORD, programData.programId + "", e.subid);
        curVideo.subId = e.subid;
        if (jiShuDatas != null) {
            for (int i = 0; i < jiShuDatas.size(); i++) {
                if (jiShuDatas.get(i).id == e.subid) {
                    if (episodeAdapter != null) {
                        episodeAdapter.setSelectedPosition(i);
                        currentJiShuPos = i;
                        episodeAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
        //更新评论
        currentJiShuPos = exchangeSubidToPosition(currentJiShu);
        if (currentJiShu != null && currentJiShuPos >= 0 && currentJiShuPos < currentJiShu.size()) {
            getCommentData(currentJiShu.get(currentJiShuPos).topicId, 0, 10);
        }
    }

    public SubVideoData curVideo = new SubVideoData();
    public boolean isNative = true;

    public void onEvent(SubVideoData video) {
        videoLoadOk = true;
        curVideo = video;
        String tempUrl = checkCacheInfo();
        if (!TextUtils.isEmpty(tempUrl)){
            video.videoPath = tempUrl;
        }
        Intent intent = setIntentExtra(video);
        isNative = video.isNative;
        if (!isNative && video.positionFlag!=2){
            playerActivity.url = video.webUrl;
            waitForPlay();
        } else if ((!video.skipToWeb && autoPlay) || 3 == video.positionFlag || 2 == video.positionFlag) {
            playerActivity.hideHalfControl();
            player.stopPlayback();
            playerActivity.restartPlay(intent);
        } else {
            waitForPlay();
        }
        if (clickPlatformId == curVideo.platformId) {
            clickStage = video.stage;
        }

//		mProgramId = video.programId;
        if (programLoadOk) {
            if (!platformChanged) {
                updatePlatformView(curVideo.platformId);
                getSubStageList(curVideo.programId, curVideo.platformId, curVideo.stage, true);
                updateTagView(curVideo.stage);
            } else {
                if (curVideo.platformId == clickPlatformId) {
                    updatePlatformView(getClickPlatformId());
                    getSubStageList(curVideo.programId, curVideo.platformId, curVideo.stage, true);
                    updateTagView(getClickStage());
                } else {

                }
            }
        }
    }
    private void waitForPlay(){
        if (player.isPlaying()) {
            player.stopPlayback();
            playerActivity.isPrepared = false;
        }
        playerActivity.showStartplay();
        playerActivity.setVideoTitle();
    }

    private String checkCacheInfo() {
        DownloadInfo info = new DownloadInfo();
        info.programId = (int)programData.programId;
        info.subProgramId = curVideo.subId;
        DownloadInfo temp = accessDownload.queryCacheProgram(info);
        if (temp.state == 2){
            return temp.fileLocation;
        }
        return null;
    }

    private void updateTagView(int pos) {
        if (subTagAdapter != null) {
            subTagAdapter.setSelectedPos(pos);
            subTagAdapter.notifyDataSetChanged();
            subTagList.setSelection(pos);
        }
    }

    public void onEvent(EventMessage message) {
        if (message.name.equals("ProgramDetailHalfActivity")) {
            onPlatformSelected(message.pos);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        VolleyHelper.cancelRequest(Constants.platFormProgramSubList);
        VolleyHelper.cancelRequest(Constants.subStageList);
        VolleyHelper.cancelRequest(Constants.programDetail);
        VolleyHelper.cancelRequest(Constants.programEvaluateAdd);
        VolleyHelper.cancelRequest(Constants.chaseAdd);
        VolleyHelper.cancelRequest(Constants.chaseDelete);
        VolleyHelper.cancelRequest(Constants.talkAdd);
        VolleyHelper.cancelRequest(Constants.talkForwardAdd);
        VolleyHelper.cancelRequest(Constants.replyAdd);
        VolleyHelper.cancelRequest(Constants.talkList);
    }


    private void foldProgramDetail() {
        if (programData.detailType == 1) {
            infoPart1.setVisibility(View.GONE);
            infoPart2.setVisibility(View.GONE);
        } else if (programData.detailType == 2) {
            infoPart1.setVisibility(View.VISIBLE);
            infoPart2.setVisibility(View.GONE);
        }
    }

    private void expandProgramDetail() {
        infoPart1.setVisibility(View.VISIBLE);
        infoPart2.setVisibility(View.VISIBLE);
    }

    private void updateTaobaoAdvertise(int advertise) {
        taobaoLayout = (RelativeLayout) rootView.findViewById(R.id.layout);
        taobaoLayout.setBackgroundColor(Color.WHITE);
        taobaoLine = rootView.findViewById(R.id.half_taobao_line);
        AlimamaHelper alimamaHelper = new AlimamaHelper(getActivity());
        View view = alimamaHelper.getFeedView(RecommendFragment.feed);
        if (view != null && advertise == 1) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            View child0 = taobaoLayout.getChildAt(0);
            lp.addRule(RelativeLayout.BELOW, child0.getId());
            view.setLayoutParams(lp);
            taobaoLayout.addView(view);
            taobaoLayout.setVisibility(View.VISIBLE);
            taobaoLine.setVisibility(View.VISIBLE);
        } else {
            taobaoLayout.setVisibility(View.GONE);
            taobaoLine.setVisibility(View.GONE);
        }
    }

    private void openStatement() {
        startActivity(new Intent(getActivity(), StatementDialogActivity.class));
    }


    public void setCurrentJuji(List<JiShuData> list, int pos) {
        currentJiShu.clear();
        currentJiShu.addAll(list);
        currentJiShuPos = pos;
        CommentData.current().topicID = list.get(pos).topicId;
    }

    public void startPlay(ArrayList<JiShuData> jiShuDatas, int pos) {
        CommonUtils.showMethodLog("start-Play method");
        if (pos >= jiShuDatas.size()) {
            return;
        }
        Intent intent = setIntentExtra(jiShuDatas, pos);
        playerActivity.hideHalfControl();
        playerActivity.switchEpisode = true;
        playerActivity.stopPreLoading();
        player.stopPlayback();
        playerActivity.restartPlay(intent);
    }

    public void startPlayAuto(ArrayList<JiShuData> jiShuDatas, int pos) {
        CommonUtils.showMethodLog("startPlayAuto");
        if (jiShuDatas != null && jiShuDatas.size() > 0 && pos < jiShuDatas.size()) {
            Intent intent = setIntentExtra(jiShuDatas, pos);
            if (!skipWeb) {
                playerActivity.hideHalfControl();
                player.stopPlayback();
                playerActivity.restartPlay(intent);
            } else {
                if (player.isPlaying()) {
                    player.stopPlayback();
                    playerActivity.isPrepared = false;
                }
                playerActivity.showStartplay();
                playerActivity.setVideoTitle();
            }
        } else {
            Log.e("msg", " data count is 0");
            Toast.makeText(getActivity(), "暂无视频源", Toast.LENGTH_SHORT).show();
            playerActivity.mediaControllerFragment.setTitleName("");
            player.stopPlayback();
            playerActivity.isPrepared = false;
            playerActivity.resetLoadingLogo(2);
        }
    }

    private Intent setIntentExtra(ArrayList<JiShuData> jiShuDatas,
                                  int arg2) {
        Intent intent = playerActivity.getIntent();
        JiShuData jishu = jiShuDatas.get(arg2);
        intent.putExtra("programPic", programData.pic);
        intent.putExtra("url", jishu.url);
        intent.putExtra("fav", programData.isChased);
        intent.putExtra("path", jishu.videoPath);
        intent.putExtra("subid", jishu.id);
        intent.putExtra("skipWeb", skipWeb);
        intent.putExtra("isNative",isNative);
        if (jiShuDatas != null) {// 传上下集数据
            Bundle mBundle = new Bundle();
            mBundle.putInt("mPosition", arg2);
            mBundle.putInt("subOrderType", programData.subOrderType);
            mBundle.putSerializable("subList", jiShuDatas);
            intent.putExtras(mBundle);
        }
        intent.putExtra("playType", 2);

        if (programData.pType == ProgramData.TYPE_TV
                || programData.pType == ProgramData.TYPE_DONGMAN) {
            intent.putExtra("programName", programData.name);
            if (TextUtils.isDigitsOnly(jishu.shortName)) {
                intent.putExtra("title", "第" + jishu.shortName + "集");
            } else {
                intent.putExtra("title", jishu.shortName);
            }
        } else {
            intent.putExtra("title", (TextUtils.isEmpty(jishu.shortName) ? "" : (jishu.shortName + " : ")) + jishu.name);
        }
        String id = String.valueOf(programData.programId);
        intent.putExtra("id", Integer.parseInt(id));
        intent.putExtra("topicId", programData.topicId);
        intent.putExtra("programPic", programData.pic);
        VolleyProgramRequest.playCount(getActivity(), null, Integer.parseInt(programData.programId + ""),
                jishu.id, 0,"", null);
        return intent;
    }

    AccessDownload accessDownload;
    public void filterCacheInfo(List<JiShuData> jiShuDatas, ProgramData programData) {
        if (jiShuDatas == null || jiShuDatas.size() == 0) {
            return;
        }
        ArrayList<DownloadInfo> downloadedInfos = accessDownload
                .queryDownloadInfo(DownloadInfoState.DOWNLOADED);
        ArrayList<DownloadInfo> downloadIngInfos = accessDownload
                .queryDownloadInfo(DownloadInfoState.WAITTING);
        ArrayList<DownloadInfo> downloadIngInfos2 = accessDownload
                .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
        ArrayList<DownloadInfo> downloadIngInfos3 = accessDownload
                .queryDownloadInfo(DownloadInfoState.PAUSE);
        ArrayList<DownloadInfo> downloadIngInfos4 = accessDownload
                .queryDownloadInfo(DownloadInfoState.ERROR);
        downloadIngInfos.addAll(downloadIngInfos2);
        downloadIngInfos.addAll(downloadIngInfos3);
        downloadIngInfos.addAll(downloadIngInfos4);
        for (JiShuData jishuData : jiShuDatas) {
            if (jishuData.url.contains("pickcode") || !isNative) {
                CacheInfo info = new CacheInfo();
                info.state = 7;
                jishuData.cacheInfo = info;
                continue;
            }
            boolean isSet = false;
            for (DownloadInfo downloadedInfo : downloadedInfos) {
                if (programData.programId == downloadedInfo.programId
                        && jishuData.id == downloadedInfo.subProgramId) {
                    CacheInfo info = new CacheInfo();
                    info.state = DownloadInfoState.DOWNLOADED;
                    jishuData.cacheInfo = info;
                    jishuData.videoPath = downloadedInfo.fileLocation;
                    // jishuData.netPlayDatas.get(0).videoPath =
                    // downloadedInfo.fileLocation;
                    isSet = true;
                    break;
                }
            }
            for (DownloadInfo downloadingInfo : downloadIngInfos) {
                if (isSet) {
                    break;
                }
                if (programData.programId == downloadingInfo.programId
                        && jishuData.id == downloadingInfo.subProgramId) {
                    CacheInfo info = new CacheInfo();
                    info.state = DownloadInfoState.WAITTING;
                    jishuData.cacheInfo = info;
                    isSet = true;
                    break;
                }
            }
            if (!isSet) {
                CacheInfo info = new CacheInfo();
                info.state = 5;
                jishuData.cacheInfo = info;
            }

        }
    }

    public DownloadInfo isCacheVideo(int subId, ProgramData programData) {
        AccessDownload accessDownload = AccessDownload
                .getInstance(getActivity());
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.programId = (int) programData.programId;
        downloadInfo.subProgramId = subId;
        downloadInfo = accessDownload.queryCacheProgram(downloadInfo);
        if (downloadInfo.state == DownloadInfoState.DOWNLOADED) {
            return downloadInfo;
        }
        return null;
    }

    public void showCommentInput(EditText edit) {
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    public void hideCommentInput(EditText edit) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    protected void loadImage(final ImageView imageView, String url,
                             int defaultPic) {
        imageLoaderHelper.loadImage(imageView, url, defaultPic);
    }
//	public int getLogInt(){
//		int id;
//		try {
//			id = PreferencesUtils.getInt(getActivity(), Constants.SP_PLAY_RECORD, programData.programId + "", 0);
//		} catch (Exception e) {
//			id = 0;
//			PreferencesUtils.remove(getActivity(), Constants.SP_PLAY_RECORD, programData.programId + "");
//		}
//		return id;
//	}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pdhp_info_part1:
                break;
            case R.id.pdhp_info_part2:
                break;
            case R.id.pdhe_plat_img:
            case R.id.pdhe_plat_text:
                if (platformDialog != null) {
                    if (!platformDialog.isAdded())
                    platformDialog.show(getChildFragmentManager(), "platformDialog");
                }
                break;
            case R.id.hs_ad_image:
                AdStatisticsUtil.adCount(getActivity(), Constants.programHalf_2, 1);
                FanxingManager.goMainUi(getActivity());
                break;
        }
    }

    private Intent setIntentExtra(SubVideoData video) {
        Intent intent = playerActivity.getIntent();
        intent.putExtra("url", video.webUrl);
        intent.putExtra("path", video.videoPath);
        intent.putExtra("subid", video.subId);
        intent.putExtra("skipWeb", video.skipToWeb);
        intent.putExtra("playType", 2);
        intent.putExtra("isNative", video.isNative);

        if (video.ptype == ProgramData.TYPE_TV
                || video.ptype == ProgramData.TYPE_DONGMAN) {
            intent.putExtra("programName", video.progName);
            if (TextUtils.isDigitsOnly(video.subName)) {
                intent.putExtra("title", "第" + video.subName + "集");
            } else {
                intent.putExtra("title", video.subName);
            }
        } else {
            intent.putExtra("title", video.subName);
        }
        intent.putExtra("id", video.programId);
        intent.removeExtra("subList");
        String tempStr = video.recVersion;
        if (!TextUtils.isEmpty(video.recVersion)){
            video.recVersion = "";
        }
        VolleyProgramRequest.playCount(getActivity(), null, Integer.parseInt(video.programId + ""),
                video.subId, 0,tempStr, null);
        return intent;
    }

    public void setJuJiExtra(ArrayList<JiShuData> datas, int pos, boolean order) {
        if (datas != null) {// 传上下集数据
            Bundle mBundle = new Bundle();
            mBundle.putInt("mPosition", pos);
            mBundle.putBoolean("normalOrder", order);
            mBundle.putSerializable("subList", datas);
            mBundle.putBoolean("isNative",isNative);
            playerActivity.getIntent().putExtras(mBundle);
            playerActivity.getJishuExtra();
        }
    }

    public void onPlatformSelected(int pos) {
//		platformId = programData.platformList.get(pos).id;
        clickPlatformId = programData.platformList.get(pos).id;
        isNative = programData.platformList.get(pos).isNative;
        if (clickPlatformId == curVideo.platformId) {
            getSubStageList(programData.programId, clickPlatformId, curVideo.stage, false);
        } else {
            getSubStageList(programData.programId, clickPlatformId, 0, false);
        }
        updatePlatformView(clickPlatformId);
        platformDialog.dismiss();
        platformChanged = true;
    }

    public void updatePlatformView(int pId) {
        int pos = getPlatformPos(pId);
        SourcePlatform platform = programData.platformList.get(pos);
        loadImage(platImg, platform.pic, R.drawable.play_source_default);
        platText.setText("");
    }

    IFLYBannerAd bannerView;
    FrameLayout layout_ads;
    ImageView img_ads;

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
//			bannerView.invalidate();
//			bannerView.requestFocus();
            AdStatisticsUtil.adCount(getActivity(), Constants.programHalf);
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
