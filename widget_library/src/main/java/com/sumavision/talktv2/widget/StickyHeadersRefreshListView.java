package com.sumavision.talktv2.widget;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sumavision.talktv2.widget.sticky.AdapterWrapper;
import com.sumavision.talktv2.widget.sticky.SectionIndexerAdapterWrapper;
import com.sumavision.talktv2.widget.sticky.StickyListHeadersAdapter;
import com.sumavision.talktv2.widget.sticky.WrapperView;

/**
 * 固定头部推挤效果+下拉刷新+加载更多
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StickyHeadersRefreshListView extends ListView {

	public interface OnHeaderClickListener {
		public void onHeaderClick(StickyHeadersRefreshListView l, View header,
				int itemPosition, long headerId, boolean currentlySticky);
	}

	private OnScrollListener mOnScrollListenerDelegate;
	private boolean mAreHeadersSticky = true;
	private int mHeaderBottomPosition;
	private View mHeader;
	private int mDividerHeight;
	private Drawable mDivider;
	private Boolean mClippingToPadding;
	private final Rect mClippingRect = new Rect();
	private Long mCurrentHeaderId = null;
	private AdapterWrapper mAdapter;
	private float mHeaderDownY = -1;
	private boolean mHeaderBeingPressed = false;
	private OnHeaderClickListener mOnHeaderClickListener;
	private int mHeaderPosition;
	private ViewConfiguration mViewConfig;
	private ArrayList<View> mFooterViews;
	private boolean mDrawingListUnderStickyHeader = false;
	private Rect mSelectorRect = new Rect();// for if reflection fails
	private Field mSelectorPositionField;

	private String pullTip;
	private String refreshTip;
	private String releaseTip;
	private String updateTip;
	private Animation animation;
	private Animation reverseAnimation;
	private View headerView;
	private TextView tipTextView;
	private TextView lastUpdateView;
	private ProgressBar progressBar;
	private ImageView arrowImageView;
	private int defaultHeight;
	private int state;
	private static final int none = 0;
	private static final int refresh = 1;
	private static final int release_to_refresh = 2;
	private static final int pull_to_release = 3;
	private static final int load_more = 4;
	private float startY;
	private int firstVisiblePosition;
	private boolean isRecord;
	private int ratio = 2;
	private boolean isBack;
	private View footerView;

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.sticky_list_header, null);
		tipTextView = (TextView) headerView.findViewById(R.id.tip);
		lastUpdateView = (TextView) headerView.findViewById(R.id.updateTime);
		progressBar = (ProgressBar) headerView.findViewById(R.id.progressBar);
		arrowImageView = (ImageView) headerView.findViewById(R.id.imageView);
		this.addHeaderView(headerView);

		defaultHeight = 90;
		headerView.setPadding(0, -1 * defaultHeight, 0, 0);
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(200);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		footerView = inflater.inflate(R.layout.sticky_list_footer, null);
		footerView.setVisibility(View.GONE);
		Resources res = context.getResources();
		pullTip = res.getString(R.string.pull_to_refresh_pull_label);
		releaseTip = res.getString(R.string.pull_to_refresh_release_label);
		updateTip = res.getString(R.string.last_update);
		refreshTip = res.getString(R.string.pull_to_refresh_refreshing_label);
	}

	private AdapterWrapper.OnHeaderClickListener mAdapterHeaderClickListener = new AdapterWrapper.OnHeaderClickListener() {

		@Override
		public void onHeaderClick(View header, int itemPosition, long headerId) {
			if (mOnHeaderClickListener != null) {
				mOnHeaderClickListener.onHeaderClick(
						StickyHeadersRefreshListView.this, header,
						itemPosition, headerId, false);
			}
		}
	};

	private DataSetObserver mDataSetChangedObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			reset();
		}

		@Override
		public void onInvalidated() {
			reset();
		}
	};
	private boolean hasFooter;
	// 用来恢复listview位置
	private int firstPostion;
	private int lastPadding;
	private int lastPosition;
	private int currentLastPosition;
	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOnScrollListenerDelegate != null) {
				mOnScrollListenerDelegate.onScrollStateChanged(view,
						scrollState);
			}
			if (scrollState == SCROLL_STATE_IDLE
					|| scrollState == SCROLL_STATE_FLING) {
				firstPostion = getFirstVisiblePosition();
				try {
					lastPadding = getChildAt(0).getTop();
				} catch (Exception e) {
					lastPadding = 0;
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			firstVisiblePosition = firstVisibleItem;
			lastPosition = totalItemCount;
			if (visibleItemCount != totalItemCount) {
				currentLastPosition = firstVisibleItem + visibleItemCount;
			}
			if (mOnScrollListenerDelegate != null) {
				mOnScrollListenerDelegate.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				scrollChanged(firstVisibleItem);
			}
		}
	};

	public StickyHeadersRefreshListView(Context context) {
		this(context, null);
		init(context);
	}

	public StickyHeadersRefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);
	}

	public StickyHeadersRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		super.setOnScrollListener(mOnScrollListener);
		// null out divider, dividers are handled by adapter so they look good
		// with headers
		super.setDivider(null);
		super.setDividerHeight(0);
		mViewConfig = ViewConfiguration.get(context);
		if (mClippingToPadding == null) {
			mClippingToPadding = true;
		}

		try {
			Field selectorRectField = AbsListView.class
					.getDeclaredField("mSelectorRect");
			selectorRectField.setAccessible(true);
			mSelectorRect = (Rect) selectorRectField.get(this);

			mSelectorPositionField = AbsListView.class
					.getDeclaredField("mSelectorPosition");
			mSelectorPositionField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			reset();
			scrollChanged(getFirstVisiblePosition());
		}
	}

	private void reset() {
		mHeader = null;
		mCurrentHeaderId = null;
		mHeaderBottomPosition = -1;
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (view instanceof WrapperView) {
			view = ((WrapperView) view).mItem;
		}
		return super.performItemClick(view, position, id);
	}

	@Override
	public void setDivider(Drawable divider) {
		this.mDivider = divider;
		if (divider != null) {
			int dividerDrawableHeight = divider.getIntrinsicHeight();
			if (dividerDrawableHeight >= 0) {
				setDividerHeight(dividerDrawableHeight);
			}
		}
		if (mAdapter != null) {
			mAdapter.setDivider(divider);
			requestLayout();
			invalidate();
		}
	}

	@Override
	public void setDividerHeight(int height) {
		mDividerHeight = height;
		if (mAdapter != null) {
			mAdapter.setDividerHeight(height);
			requestLayout();
			invalidate();
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListenerDelegate = l;
	}

	public void setAreHeadersSticky(boolean areHeadersSticky) {
		if (this.mAreHeadersSticky != areHeadersSticky) {
			this.mAreHeadersSticky = areHeadersSticky;
			requestLayout();
		}
	}

	public boolean getAreHeadersSticky() {
		return mAreHeadersSticky;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (this.isInEditMode()) {
			super.setAdapter(adapter);
			return;
		}
		if (adapter == null) {
			mAdapter = null;
			reset();
			super.setAdapter(null);
			return;
		}
		if (!(adapter instanceof StickyListHeadersAdapter)) {
			throw new IllegalArgumentException(
					"Adapter must implement StickyListHeadersAdapter");
		}
		mAdapter = wrapAdapter(adapter);
		reset();

		if (state == load_more) {
			state = none;
			footerView.setVisibility(View.GONE);
		} else {
			state = none;
			lastUpdateView.setText(updateTip + getDateString());
			headerView.setPadding(0, -1 * defaultHeight, 0, 0);
			changeHeaderViewByState();
		}
		if (hasFooter) {
			this.removeFooterView(footerView);
		}
		addFooterView(footerView);
		setSelectionFromTop(firstPostion, lastPadding);
		hasFooter = true;
		super.setAdapter(this.mAdapter);
	}

	private String getDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date());
	}

	private AdapterWrapper wrapAdapter(ListAdapter adapter) {
		AdapterWrapper wrapper;
		if (adapter instanceof SectionIndexer) {
			wrapper = new SectionIndexerAdapterWrapper(getContext(),
					(StickyListHeadersAdapter) adapter);
		} else {
			wrapper = new AdapterWrapper(getContext(),
					(StickyListHeadersAdapter) adapter);
		}
		wrapper.setDivider(mDivider);
		wrapper.setDividerHeight(mDividerHeight);
		wrapper.registerDataSetObserver(mDataSetChangedObserver);
		wrapper.setOnHeaderClickListener(mAdapterHeaderClickListener);
		return wrapper;
	}

	public StickyListHeadersAdapter getWrappedAdapter() {
		return mAdapter == null ? null : mAdapter.mDelegate;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			scrollChanged(getFirstVisiblePosition());
		}
		if (!mAreHeadersSticky || mHeader == null) {
			super.dispatchDraw(canvas);
			return;
		}

		if (!mDrawingListUnderStickyHeader) {
			mClippingRect
					.set(0, mHeaderBottomPosition, getWidth(), getHeight());
			canvas.save();
			canvas.clipRect(mClippingRect);
		}

		positionSelectorRect();
		super.dispatchDraw(canvas);

		if (!mDrawingListUnderStickyHeader) {
			canvas.restore();
		}

		drawStickyHeader(canvas);
	}

	private void positionSelectorRect() {
		if (!mSelectorRect.isEmpty()) {
			int selectorPosition = getSelectorPosition();
			if (selectorPosition >= 0) {
				int firstVisibleItem = fixedFirstVisibleItem(getFirstVisiblePosition());
				View v = getChildAt(selectorPosition - firstVisibleItem);
				if (v instanceof WrapperView) {
					WrapperView wrapper = ((WrapperView) v);
					mSelectorRect.top = wrapper.getTop() + wrapper.mItemTop;
				}
			}
		}
	}

	private int getSelectorPosition() {
		try {
			if (mSelectorPositionField != null) {
				return mSelectorPositionField.getInt(this);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void drawStickyHeader(Canvas canvas) {
		int headerHeight = getHeaderHeight();
		int top = mHeaderBottomPosition - headerHeight;
		// clip the headers drawing region
		mClippingRect.left = getPaddingLeft();
		mClippingRect.right = getWidth() - getPaddingRight();
		mClippingRect.bottom = top + headerHeight;
		mClippingRect.top = mClippingToPadding ? getPaddingTop() : 0;

		canvas.save();
		canvas.clipRect(mClippingRect);
		canvas.translate(getPaddingLeft(), top);
		mHeader.draw(canvas);
		canvas.restore();
	}

	private void measureHeader() {
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(),
				MeasureSpec.EXACTLY);
		int heightMeasureSpec = 0;

		ViewGroup.LayoutParams params = mHeader.getLayoutParams();
		if (params != null && params.height > 0) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.EXACTLY);
		} else {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		mHeader.measure(widthMeasureSpec, heightMeasureSpec);
		mHeader.layout(getLeft() + getPaddingLeft(), 0, getRight()
				- getPaddingRight(), mHeader.getMeasuredHeight());
	}

	private int getHeaderHeight() {
		return mHeader == null ? 0 : mHeader.getMeasuredHeight();
	}

	@Override
	public void setClipToPadding(boolean clipToPadding) {
		super.setClipToPadding(clipToPadding);
		mClippingToPadding = clipToPadding;
	}

	private void scrollChanged(int reportedFirstVisibleItem) {

		int adapterCount = mAdapter == null ? 0 : mAdapter.getCount();
		if (adapterCount == 0 || !mAreHeadersSticky) {
			return;
		}

		final int listViewHeaderCount = getHeaderViewsCount();
		final int firstVisibleItem = fixedFirstVisibleItem(reportedFirstVisibleItem)
				- listViewHeaderCount;

		if (firstVisibleItem < 0 || firstVisibleItem > adapterCount - 1) {
			reset();
			updateHeaderVisibilities();
			invalidate();
			return;
		}

		long newHeaderId = mAdapter.getHeaderId(firstVisibleItem);
		if (mCurrentHeaderId == null || mCurrentHeaderId != newHeaderId) {
			mHeaderPosition = firstVisibleItem;
			mCurrentHeaderId = newHeaderId;
			mHeader = mAdapter.getHeaderView(mHeaderPosition, mHeader, this);
			measureHeader();
		}

		int childCount = getChildCount();
		if (childCount != 0) {
			View viewToWatch = null;
			int watchingChildDistance = Integer.MAX_VALUE;
			boolean viewToWatchIsFooter = false;

			for (int i = 0; i < childCount; i++) {
				final View child = super.getChildAt(i);
				final boolean childIsFooter = mFooterViews != null
						&& mFooterViews.contains(child);
				final int childDistance = child.getTop()
						- (mClippingToPadding ? getPaddingTop() : 0);
				if (childDistance < 0) {
					continue;
				}

				if (viewToWatch == null
						|| (!viewToWatchIsFooter && !((WrapperView) viewToWatch)
								.hasHeader())
						|| childIsFooter
						|| ((((WrapperView) child).hasHeader()) && childDistance < watchingChildDistance)) {
					viewToWatch = child;
					viewToWatchIsFooter = childIsFooter;
					watchingChildDistance = childDistance;
				}
			}

			final int headerHeight = getHeaderHeight();
			if (viewToWatch != null
					&& (viewToWatchIsFooter || ((WrapperView) viewToWatch)
							.hasHeader())) {
				if (firstVisibleItem == listViewHeaderCount
						&& super.getChildAt(0).getTop() > 0
						&& !mClippingToPadding) {
					mHeaderBottomPosition = 0;
				} else {
					final int paddingTop = mClippingToPadding ? getPaddingTop()
							: 0;
					mHeaderBottomPosition = Math.min(viewToWatch.getTop(),
							headerHeight + paddingTop);
					mHeaderBottomPosition = mHeaderBottomPosition < paddingTop ? headerHeight
							+ paddingTop
							: mHeaderBottomPosition;
				}
			} else {
				mHeaderBottomPosition = headerHeight
						+ (mClippingToPadding ? getPaddingTop() : 0);
			}
		}
		updateHeaderVisibilities();
		invalidate();
	}

	@Override
	public void addFooterView(View v) {
		super.addFooterView(v);
		if (mFooterViews == null) {
			mFooterViews = new ArrayList<View>();
		}
		mFooterViews.add(v);
	}

	@Override
	public boolean removeFooterView(View v) {
		if (super.removeFooterView(v)) {
			mFooterViews.remove(v);
			return true;
		}
		return false;
	}

	private void updateHeaderVisibilities() {
		int top = mClippingToPadding ? getPaddingTop() : 0;
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = super.getChildAt(i);
			if (child instanceof WrapperView) {
				WrapperView wrapperViewChild = (WrapperView) child;
				if (wrapperViewChild.hasHeader()) {
					View childHeader = wrapperViewChild.mHeader;
					if (wrapperViewChild.getTop() < top) {
						childHeader.setVisibility(View.INVISIBLE);
					} else {
						childHeader.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	private int fixedFirstVisibleItem(int firstVisibleItem) {
		if (Build.VERSION.SDK_INT >= 11)// 3.0之后
		{
			return firstVisibleItem;
		}

		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getBottom() >= 0) {
				firstVisibleItem += i;
				break;
			}
		}

		// work around to fix bug with firstVisibleItem being to high because
		// listview does not take clipToPadding=false into account
		if (!mClippingToPadding && getPaddingTop() > 0) {
			if (super.getChildAt(0).getTop() > 0) {
				if (firstVisibleItem > 0) {
					firstVisibleItem -= 1;
				}
			}
		}
		return firstVisibleItem;
	}

	@Override
	public void setSelectionFromTop(int position, int y) {
		if (mAreHeadersSticky) {
			y += getHeaderHeight();
		}
		super.setSelectionFromTop(position, y);
	}

	@SuppressLint("NewApi")
	@Override
	public void smoothScrollToPositionFromTop(int position, int offset) {
		if (mAreHeadersSticky) {
			offset += getHeaderHeight();
		}
		super.smoothScrollToPositionFromTop(position, offset);
	}

	@SuppressLint("NewApi")
	@Override
	public void smoothScrollToPositionFromTop(int position, int offset,
			int duration) {
		if (mAreHeadersSticky) {
			offset += getHeaderHeight();
		}
		super.smoothScrollToPositionFromTop(position, offset, duration);
	}

	public void setOnHeaderClickListener(
			OnHeaderClickListener onHeaderClickListener) {
		this.mOnHeaderClickListener = onHeaderClickListener;
	}

	public void setDrawingListUnderStickyHeader(
			boolean drawingListUnderStickyHeader) {
		mDrawingListUnderStickyHeader = drawingListUnderStickyHeader;
	}

	public boolean isDrawingListUnderStickyHeader() {
		return mDrawingListUnderStickyHeader;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!isRecord && firstVisiblePosition == 0) {
				startY = ev.getY();
				isRecord = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state == none || state == pull_to_release) {
				headerView.setPadding(0, -1 * defaultHeight, 0, 0);
				state = none;
				changeHeaderViewByState();
			}
			if (state == refresh) {
				headerView.setPadding(0, 0, 0, 0);

			}
			if (state == release_to_refresh) {
				state = refresh;
				headerView.setPadding(0, 0, 0, 0);
				changeHeaderViewByState();
				if (listener != null) {
					listener.onRefresh();
				}
			}
			isBack = false;
			isRecord = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!isRecord && firstVisiblePosition == 0 && canPullToRefresh) {
				startY = ev.getY();
				isRecord = true;
			}
			if (lastPosition == currentLastPosition && state != load_more
					&& !isRecordLoadingMore) {
				startY = ev.getY();
				isRecordLoadingMore = true;
			}
			if (isRecordLoadingMore && state != refresh) {
				float tempY = ev.getY();
				if (tempY < startY) {
					if (listener != null && canLoadMore) {
						state = load_more;
						listener.onLoadingMore();
						footerView.setVisibility(View.VISIBLE);
					}
					isRecordLoadingMore = false;
				}
			}
			if (isRecord && state != load_more) {
				float tempY = ev.getY();
				int distance = (int) ((tempY - startY) / ratio);
				if (state == none) {
					if (tempY > startY) {
						setSelection(0);
						state = pull_to_release;
						headerView.setPadding(0, (distance - defaultHeight), 0,
								0);
						changeHeaderViewByState();
					}
				}
				if (state == pull_to_release) {
					setSelection(0);
					if (tempY <= startY) {
						state = none;
						headerView.setPadding(0, -1 * defaultHeight, 0, 0);
					} else if (distance >= defaultHeight) {
						state = release_to_refresh;
						changeHeaderViewByState();
					} else {
						headerView.setPadding(0, (distance - defaultHeight), 0,
								0);
					}

				}
				if (state == release_to_refresh) {
					setSelection(0);
					if (distance < defaultHeight) {
						isBack = true;
						state = pull_to_release;
						changeHeaderViewByState();
					}
					headerView.setPadding(0, (distance - defaultHeight), 0, 0);

				}
				if (state == refresh) {
					if (tempY > startY) {
						headerView.setPadding(0, (distance - defaultHeight), 0,
								0);
					}
				}
			}
			break;
		default:
			break;
		}

		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN
				&& ev.getY() <= mHeaderBottomPosition) {
			mHeaderDownY = ev.getY();
			mHeaderBeingPressed = true;
			mHeader.setPressed(true);
			mHeader.invalidate();
			invalidate(0, 0, getWidth(), mHeaderBottomPosition);
			return true;
		}
		if (mHeaderBeingPressed) {
			if (Math.abs(ev.getY() - mHeaderDownY) < mViewConfig
					.getScaledTouchSlop()) {
				if (action == MotionEvent.ACTION_UP
						|| action == MotionEvent.ACTION_CANCEL) {
					mHeaderDownY = -1;
					mHeaderBeingPressed = false;
					mHeader.setPressed(false);
					mHeader.invalidate();
					invalidate(0, 0, getWidth(), mHeaderBottomPosition);
					if (mOnHeaderClickListener != null) {
						mOnHeaderClickListener.onHeaderClick(this, mHeader,
								mHeaderPosition, mCurrentHeaderId, true);
					}
				}
				return true;
			} else {
				mHeaderDownY = -1;
				mHeaderBeingPressed = false;
				mHeader.setPressed(false);
				mHeader.invalidate();
				invalidate(0, 0, getWidth(), mHeaderBottomPosition);
			}
		}

		return super.onTouchEvent(ev);
	}

	private boolean isRecordLoadingMore;

	private OnStickyRefreshListener listener;

	public void setOnRefreshListener(OnStickyRefreshListener listener) {
		this.listener = listener;
	}

	public interface OnStickyRefreshListener {
		public void onRefresh();

		public void onLoadingMore();
	}

	int lastScrollX;
	int lastScrollY;

	private void changeHeaderViewByState() {
		switch (state) {
		case none:
			progressBar.setVisibility(View.GONE);
			arrowImageView.setVisibility(View.VISIBLE);
			tipTextView.setText(pullTip);
			break;
		case refresh:
			tipTextView.setText(refreshTip);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			break;
		case release_to_refresh:
			progressBar.setVisibility(View.GONE);
			arrowImageView.setVisibility(View.VISIBLE);
			tipTextView.setText(releaseTip);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			break;
		case pull_to_release:
			progressBar.setVisibility(View.GONE);
			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
			}
			tipTextView.setText(pullTip);
			break;
		default:
			break;
		}
	}

	public void onLoadMoreOver() {
		state = none;
		footerView.setVisibility(View.GONE);
		setSelectionFromTop(firstPostion, lastPadding);
	}

	private boolean canLoadMore = true;

	public void setCanLoadMore(boolean canLoadMore) {
		this.canLoadMore = canLoadMore;
	}

	public void onLoadError() {
		if (state == load_more) {
			state = none;
			footerView.setVisibility(View.GONE);
		} else {
			state = none;
			headerView.setPadding(0, -1 * defaultHeight, 0, 0);
			changeHeaderViewByState();
		}
	}

	private boolean canPullToRefresh = true;

	public void setPullToRefresh(boolean canPullToRefresh) {
		this.canPullToRefresh = canPullToRefresh;
	}
}
