package net.sunniwell.georgeweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sunniwell.georgeweather.db.Province;
import net.sunniwell.georgeweather.util.HttpUtil;
import net.sunniwell.georgeweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2017/9/21.
 */

public class ChooseAreaFragment extends Fragment {
    public static final String TAG = "jpd-ChooseAreaFragment";
    private Button backBtn;
    private TextView barTitle;
    ProgressDialog progress;
    private ListView areaList;
    private ArrayAdapter<String> arrayAdapter;
    /**
     * listView数据
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * Province数据
     */
    private List<Province> provinceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_area_frag, container, false);
        backBtn = (Button)view.findViewById(R.id.back_button);
        barTitle = (TextView)view.findViewById(R.id.bar_title);
        areaList = (ListView)view.findViewById(R.id.area_list);
        backBtn.setVisibility(View.INVISIBLE);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        areaList.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position:" + position + "id:" + id);

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBackButtonClick: ");
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果没有查询到再去服务器上查询；
     */
    private void queryProvinces() {
        Log.d(TAG, "queryProvinces: ");
        barTitle.setText("中国");
        backBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for(Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
        } else {
            String url = "http://guolin.tech/api/china";
            queryFromServer(url, "province");
        }
    }

    /**
     * 向服务器请求数据
     * @param url 服务器地址
     */
    private void queryFromServer(String url, final String type) {
        showProgressDialog();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeProgressDialog();
                Toast.makeText(getContext(), "加载数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String serverData = response.body().string();
                boolean result = false;
                if ("province".equalsIgnoreCase(type)) {
                    result = Utility.handleProvinceData(serverData);
                } else if ("city".equalsIgnoreCase(type)) {
                    result = Utility.handCityData(serverData);
                } else if ("county".equalsIgnoreCase(type)) {
                    result = Utility.handCountyData(serverData);
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equalsIgnoreCase(type)) {
                                queryProvinces();
                            }
                        }
                    });
                }
            }
        };
        HttpUtil.sendRequest(url, callback);
    }

    /**
     * 展示请求数据的缓冲图标
     */
    private void showProgressDialog() {
        if (progress == null) {
            progress = new ProgressDialog(getContext());
            progress.setMessage("请求数据中....");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }
    }

    /**
     * 关闭请求数据的缓冲图标
     */
    private void closeProgressDialog() {
        if(progress != null) {
            progress.dismiss();
        }
    }
}