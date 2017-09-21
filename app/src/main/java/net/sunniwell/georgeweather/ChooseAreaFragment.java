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

import net.sunniwell.georgeweather.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private ListView areaList;
    ListAdapter arrayAdapter;
    private List<String> provinceList = new ArrayList<>();
    ProgressDialog progress;
    Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "onFailure: ");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, "onResponse: id:" + Thread.currentThread().getId());
            requestProvinceData(response.body().string());
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_area_frag, container, false);
        backBtn = (Button)view.findViewById(R.id.back_button);
        barTitle = (TextView)view.findViewById(R.id.bar_title);
        areaList = (ListView)view.findViewById(R.id.area_list);
        backBtn.setVisibility(View.INVISIBLE);
        progress = ProgressDialog.show(getContext(), null, "请求数据中....");
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position:" + position + "id:" + id);

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        initListData();
        super.onActivityCreated(savedInstanceState);
    }
    private void initListData() {
        Log.d(TAG, "initListData: ");
        HttpUtil.sendRequest("http://guolin.tech/api/china/", callback);
    }
    private void requestProvinceData(String requestData) {
        try {
            JSONArray proviceArray = new JSONArray(requestData);
            for(int i = 0; i < proviceArray.length(); i++) {
                JSONObject provinceObject = proviceArray.getJSONObject(i);
                int id = provinceObject.getInt("id");
                String name = provinceObject.getString("name");
                Log.d(TAG, "id:" + id + ",name:" + name);
                provinceList.add(name);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                    Log.d(TAG, "provinceList:" + provinceList);
                    arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, provinceList);
                    areaList.setAdapter(arrayAdapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}