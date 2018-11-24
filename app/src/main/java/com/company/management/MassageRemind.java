package com.company.management;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MassageRemind.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MassageRemind#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MassageRemind extends Fragment {
    private ListView listView;
    public MassageRemind() {
        // Required empty public constructor
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
        List<String> title = new ArrayList<>(), abs = new ArrayList<>(), time = new ArrayList<>();
        title.add("hello");
        abs.add("hello");
        time.add("1996-10-01 00:00");
        MessageRemindListViewAdapter messageRemindListViewAdapter = new MessageRemindListViewAdapter(title, abs, time);
        listView.setAdapter(messageRemindListViewAdapter);
    }
}