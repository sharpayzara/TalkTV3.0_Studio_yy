package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.WebpUtils;

/**
 * 竞猜帮助
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveHelpActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_guessing_help);
		ImageView divView = (ImageView) findViewById(R.id.div);
		divView.setImageBitmap(WebpUtils.getAssetBitmap(this,
				"webp/help_interact.webp"));
		getSupportActionBar().setTitle(R.string.help);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
