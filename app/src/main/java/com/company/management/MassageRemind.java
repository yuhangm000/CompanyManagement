package com.company.management;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MassageRemind.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MassageRemind extends Fragment {
    private ListView listView;
    private List<String> title, abs, time;
    public MassageRemind() {
        // Required empty public constructor
        title = new ArrayList<>();
        abs = new ArrayList<>();
        time = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_massage_remind, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView)view.findViewById(R.id.message_remind_list);
        // TODO: 补充获得这些信息的交互操作
        title.add("hello");
        abs.add("hello");
        time.add("1996-10-01 00:00");
        MessageRemindListViewAdapter messageRemindListViewAdapter = new MessageRemindListViewAdapter(title, abs, time);
        listView.setAdapter(messageRemindListViewAdapter);
//        listView.setOnItemClickListener();
    }
    Handler handler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 3) {
                Toast.makeText(getContext(), "服务器开小差啦~", 1000).show();
                return;
            } else {
                JSONArray jsonArray = (JSONArray) msg.obj;
            }
        }
    };
    /**
     * 以下是类区域
     */
    class GetMessages extends Thread {
        @Override
        public void run() {
            super.run();
            String user_id = new UserWR().getUserID(getContext());
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", Integer.valueOf(user_id));
                JSONObject messages = Conn.get(Router.GET_MESSAGE, params);
                Message msg = handler.obtainMessage();
                if (!JsonUtils.StatusOk(messages)) {
                    msg.arg1 = 3;
                } else {
                    msg.arg1 = 1;
                    JSONArray jsonArray = JsonUtils.GetJsonArrayParam(messages);
                    msg.obj = jsonArray;
                }
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class OnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}