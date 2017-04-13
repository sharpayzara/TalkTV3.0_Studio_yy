package com.sumavision.talktv2.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sumavision.talktv2.R;


public class LoginSendDialog  extends Dialog {

    private Context context;
//    private String title;
//    private String confirmButtonText;
//    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public LoginSendDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.send_comment_login, null);
        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.pdn_c_login_title);
        Button tvConfirm = (Button) view.findViewById(R.id.pdn_c_login_btn);
        Button tvCancel = (Button) view.findViewById(R.id.pdn_c_send_btn);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
            case R.id.pdn_c_login_btn:
                clickListenerInterface.doConfirm();
                break;
            case R.id.pdn_c_send_btn:
                clickListenerInterface.doCancel();
                break;
            }
        }

    };

}