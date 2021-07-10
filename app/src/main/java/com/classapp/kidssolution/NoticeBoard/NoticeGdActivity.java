package com.classapp.kidssolution.NoticeBoard;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.classapp.kidssolution.AppAction.GuardianMainActivity;
import com.classapp.kidssolution.AppAction.TeacherMainActivity;
import com.classapp.kidssolution.ClassDetails.CreateClassDialog;
import com.classapp.kidssolution.ClassDetails.ParticularClassGdActivity;
import com.classapp.kidssolution.ClassDetails.ParticularClassTcActivity;
import com.classapp.kidssolution.ModelClasses.StoreClassesData;
import com.classapp.kidssolution.ModelClasses.StoreNoticeData;
import com.classapp.kidssolution.R;
import com.classapp.kidssolution.RecyclerViewAdapters.ClassesCustomAdapter;
import com.classapp.kidssolution.RecyclerViewAdapters.NotebookCustomAdapter;
import com.classapp.kidssolution.RecyclerViewAdapters.NoticeCustomAdapter;
import com.classapp.kidssolution.RecyclerViewAdapters.NoticeCustomAdapterGd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoticeGdActivity extends Fragment implements View.OnClickListener {

    View views;
    CircleImageView circleImageView;
    RecyclerView recyclerView;
    ArrayList<StoreNoticeData> storeNoticeDataArrayList;
    NoticeCustomAdapterGd noticeCustomAdapterGd;
    ProgressBar progressBar;
    TextView noHw;
    DatabaseReference databaseReference;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    ConnectivityManager cm;
    NetworkInfo netInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.activity_notice_gd, container, false);

        progressBar = views.findViewById(R.id.noticeListProgressbarGdId);
        progressBar.setVisibility(View.VISIBLE);

        noHw = views.findViewById(R.id.noNoticeGdId);

        circleImageView = views.findViewById(R.id.backFromNoticeGdId);
        circleImageView.setOnClickListener(this);

        recyclerView = views.findViewById(R.id.noticeRecyclerViewGdId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        storeNoticeDataArrayList = new ArrayList<StoreNoticeData>();

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice Information");

        loadNoticeList();

        return views;
    }

    private void refresh(int milliSecond){
        final Handler handler = new Handler(Looper.getMainLooper());

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadNoticeList();
            }
        };

        handler.postDelayed(runnable, milliSecond);
    }

    private void loadNoticeList() {
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            // Retrieve unknown key
//            Query query = databaseReference.orderByKey();
//            query.addListenerForSingleValueEvent(new ValueEventListener() {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    storeNoticeDataArrayList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        StoreNoticeData StoreNoticeData = item.getValue(StoreNoticeData.class);
                        storeNoticeDataArrayList.add(StoreNoticeData);

                        Collections.reverse(storeNoticeDataArrayList);
                        noticeCustomAdapterGd = new NoticeCustomAdapterGd(getActivity(), storeNoticeDataArrayList);
                        recyclerView.setAdapter(noticeCustomAdapterGd);
                        noticeCustomAdapterGd.notifyDataSetChanged();

                        noHw.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    noHw.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG).show();
        }

        refresh(1000);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backFromNoticeGdId){
            fragment = new GuardianMainActivity();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragmentGdID, fragment);
            fragmentTransaction.commit();
        }
    }
}
