package com.sumavision.talktv2.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ExchangeLimitActivity;
import com.sumavision.talktv2.activity.LiveDetailActivity;
import com.sumavision.talktv2.activity.MyFavActivity;
import com.sumavision.talktv2.adapter.FavAdapter;
import com.sumavision.talktv2.adapter.PartsGoodsAdapter;
import com.sumavision.talktv2.bean.ChaseData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.GoodsPiece;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.fragment.DeleteDialogFragment.OnClickDeleteListener;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.MyDebrisParser;
import com.sumavision.talktv2.http.json.MyDebrisRequest;
import com.sumavision.talktv2.http.listener.OnChaseDeleteListener;
import com.sumavision.talktv2.http.listener.OnChaseListListener;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRemindListListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 我的碎片
 * 
 * @author suma-hpb
 * 
 */
public class MyPartsGoodsFragment extends BaseFragment implements
		OnRefreshListener2<ListView>, OnItemClickListener {
	public static MyPartsGoodsFragment newInstance() {
		MyPartsGoodsFragment fragment = new MyPartsGoodsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_piece);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	MyDebrisParser pieceParser = new MyDebrisParser();
	ArrayList<GoodsPiece> myPieces = new ArrayList<>();
	public void loadData(final int first,int count) {
		showLoadingLayout();
		VolleyHelper.post(new MyDebrisRequest(first, count).make(), new ParseListener(pieceParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				ptrListView.onRefreshComplete();
				hideLoadingLayout();
				if (pieceParser.errCode == JSONMessageType.SERVER_CODE_OK){
					if (first == 0){
						myPieces.clear();
					}
					if (pieceParser.pieces.size()<defoultCount){
						ptrListView.setMode(Mode.PULL_FROM_START);
					}
					myPieces.addAll(pieceParser.pieces);
					pieceAdapter.notifyDataSetChanged();
					if (myPieces.size() ==0){
						ptrListView.setVisibility(View.GONE);
						emptyText.setVisibility(View.VISIBLE);
					} else {
						ptrListView.setVisibility(View.VISIBLE);
						emptyText.setVisibility(View.GONE);
					}
				} else {
					if (myPieces.size()<0){
						showErrorLayout();
					}
				}
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				if (myPieces.size()<0){
					showErrorLayout();
				}
			}
		});
	}


	PullToRefreshListView ptrListView;
	PartsGoodsAdapter pieceAdapter;
	TextView emptyText;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ptrListView = (PullToRefreshListView) view.findViewById(R.id.list);
		emptyText = (TextView) view.findViewById(R.id.no_piece);
//		ptrListView.getRefreshableView().setEmptyView(emptyText);
		ptrListView.setMode(Mode.PULL_FROM_END);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		ptrListView.setEmptyView(inflater.inflate(R.layout.fav_empty_layout, null));
		pieceAdapter = new PartsGoodsAdapter(getActivity(),myPieces);
		ptrListView.setAdapter(pieceAdapter);
		ptrListView.setOnItemClickListener(this);
		loadData(0, 10);
	}

	@Override
	public void reloadData() {
		loadData(0, myPieces.size() >0 ? myPieces.size() : defoultCount);
	}

	int selectPos = -1;
	int defoultCount = 10;


	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(0, myPieces.size() >0 ? myPieces.size() : defoultCount);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData( myPieces.size() , defoultCount);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GoodsPiece temp = myPieces.get((int)id);
		if (temp.hotGoodsId<=0){
			return;
		}
		Intent intent = new Intent(getActivity(), ExchangeLimitActivity.class);
		intent.putExtra("hotGoodsId",(long)temp.hotGoodsId);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	public void onEvent(EventMessage msg){
		if (msg.name.equals("MyPartsGoodsFragment")){
			reloadData();
		}
	}

}
