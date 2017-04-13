package com.sumavision.talktv2.components;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sumavision.talktv2.R;


public class ReportDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private ClickListenerInterface clickListenerInterface;
    public String content;

    public interface ClickListenerInterface {

        public void onReportItemClick();
    }

    public ReportDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_report_list, null);
        setContentView(view);
        view.findViewById(R.id.drl_textView1).setOnClickListener(this);
        view.findViewById(R.id.drl_textView2).setOnClickListener(this);
        view.findViewById(R.id.drl_textView3).setOnClickListener(this);
        view.findViewById(R.id.drl_textView4).setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.6);
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.drl_textView1:
		case R.id.drl_textView2:
		case R.id.drl_textView3:
		case R.id.drl_textView4:
			content =((TextView)v).getText().toString();
			clickListenerInterface.onReportItemClick();
            dismiss();
			break;
		default:
			break;
		}
	};

}