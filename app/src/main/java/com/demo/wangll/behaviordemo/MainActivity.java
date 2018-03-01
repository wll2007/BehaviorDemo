package com.demo.wangll.behaviordemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior mBottomSheetBehavior;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_bottom_sheet_control:
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    break;
                case R.id.btn_bottom_dialog_control:
                    if (mBottomSheetDialog.isShowing()) {
                        mBottomSheetDialog.dismiss();
                    } else {
                        mBottomSheetDialog.show();
                    }
                    break;
                case R.id.btn_return_top:
                    startActivity(new Intent(MainActivity.this, Main3Activity.class));
                    break;
                case R.id.btn_zhihu:
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    break;
            }
        }
    };
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.btn_bottom_sheet_control).setOnClickListener(onClickListener);
        findViewById(R.id.btn_bottom_dialog_control).setOnClickListener(onClickListener);
        findViewById(R.id.btn_return_top).setOnClickListener(onClickListener);
        findViewById(R.id.btn_zhihu).setOnClickListener(onClickListener);
        // 拿到这个tab_layout对应的BottomSheetBehavior
        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.tab_layout));
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        createBottomSheetDialog();
        createrSwipeDismissBehavior();
    }

    private void createBottomSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null, false);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);

        View sheetView = mBottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        final BottomSheetBehavior havior= BottomSheetBehavior.from(sheetView);
        havior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    mBottomSheetDialog.dismiss();
                    havior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        final List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(i);
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item,parent,false);
                return new ViewHolder(view){
                };
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
               TextView  view = (TextView) holder.itemView.findViewById(R.id.item_text);
                view.setText(datas.get(position)+"");
            }

            @Override
            public int getItemCount() {
                return datas.size();
            }
        });
    }

    private void createrSwipeDismissBehavior(){
        LinearLayout swiplayout = (LinearLayout) findViewById(R.id.swip_layout);
        SwipeDismissBehavior swipe = new SwipeDismissBehavior();
        swipe.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
        swipe.setStartAlphaSwipeDistance(0f);
        swipe.setSensitivity(0.2f);
        swipe.setListener(new SwipeDismissBehavior.OnDismissListener() {
            @Override
            public void onDismiss(View view) {

            }

            @Override
            public void onDragStateChanged(int state) {

            }
        });

        CoordinatorLayout.LayoutParams parm = (CoordinatorLayout.LayoutParams) swiplayout.getLayoutParams();
        parm.setBehavior(swipe);
    }
}
