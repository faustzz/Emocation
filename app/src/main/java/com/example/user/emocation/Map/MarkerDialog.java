package com.example.user.emocation.Map;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.user.emocation.R;

/**
 * Created by Joo Hyun Jun on 2017-12-02.
 */

public class MarkerDialog extends Dialog { // 마커 클릭시 뜨는 다이알로그
    private Button btn1, btn2;
    private View.OnClickListener image, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_dialog);

        btn1 = (Button)findViewById(R.id.btn_ImageView);
        btn2 = (Button)findViewById(R.id.btn_LocateImageView);

        btn1.setOnClickListener(image);
        btn2.setOnClickListener(location);
    }

    public MarkerDialog(Context context, View.OnClickListener image, View.OnClickListener location){
        super(context);
        this.image = image;
        this.location = location;

    }
}
