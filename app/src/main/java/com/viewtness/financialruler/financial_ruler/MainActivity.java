package com.viewtness.financialruler.financial_ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.viewtness.financialruler.financial_ruler.RulerView.OnRulerChangeListener;
import com.viewtness.financialruler.financial_ruler.RulerView.RulerView;

public class MainActivity extends AppCompatActivity {

    private RulerView rulerView;
    private TextView tvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initListener();
    }

    private void initView() {
        rulerView = (RulerView) findViewById(R.id.rulerView);
        tvValue = (TextView) findViewById(R.id.tv_value);
    }
    private void initListener() {
        rulerView.setOnRulerChangeListener(new OnRulerChangeListener() {
            @Override
            public void onChanged(int newValue) {
                tvValue.setText(newValue+"");
            }
        });

        rulerView.setMaxValue(100000);
        rulerView.setMinValue(0);
        rulerView.setOneItemValue(20000);
        rulerView.setOneItemValue(1000);
        tvValue.setText(20000+"");
    }
}
