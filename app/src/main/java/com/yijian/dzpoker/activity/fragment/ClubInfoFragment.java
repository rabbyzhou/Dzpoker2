package com.yijian.dzpoker.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.club.ClubManagerActivity;
import com.yijian.dzpoker.activity.club.MyClubActivity;
import com.yijian.dzpoker.adapter.OfficialInfoAdapter;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.entity.OfficialInfoBean;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyApi;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyBean;
import com.yijian.dzpoker.http.requestjoinclub.RequestJoinClubApi;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyRequestInfo;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClubInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ClubInfoFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tvMyclub;
    private RecyclerView officialInfoRecyclerView;

    private OnFragmentInteractionListener mListener;
    private List<GetClubApplyBean> requestinfo;
    private List<OfficialInfoBean> datas;

    public ClubInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClubInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubInfoFragment newInstance(String param1, String param2) {
        ClubInfoFragment fragment = new ClubInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sendRequest();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initData();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View clubLayout = inflater.inflate(R.layout.fragment_club_info, container, false);
        tvMyclub =(TextView)clubLayout.findViewById(R.id.club_page_my_club);
        tvMyclub.setOnClickListener(this);
        officialInfoRecyclerView = (RecyclerView) clubLayout.findViewById(R.id.official_info_list);
        officialInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        officialInfoRecyclerView.setHasFixedSize(true);
        OfficialInfoAdapter officialInfoAdapter = new OfficialInfoAdapter(datas, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ClubManagerActivity.class);
                startActivity(intent);
            }
        });

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.store_user_level_list_divide_drawable));
        officialInfoRecyclerView.addItemDecoration(decoration);
        officialInfoRecyclerView.setAdapter(officialInfoAdapter);


        return clubLayout;

    }

    private void initData() {
        datas = new ArrayList<OfficialInfoBean>();
        OfficialInfoBean bean = new OfficialInfoBean();
        bean.setMainMsg("俱乐部管理");
        bean.setDetailMsg("你还没有收到任何消息");
        datas.add(bean);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_page_my_club:
                gotomyclub();
                break;
            default:
                break;
        }
    }

    private void gotomyclub(){
        //跳转到主界面
        Intent intent = new Intent();
        intent.setClass(getActivity(), MyClubActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(INTENT_KEY_BACKTEXT, tvMyclub.getText());
        startActivity(intent);

    }

    public List<GetClubApplyBean> getRequestinfo() {
        return requestinfo;
    }

    public void setRequestinfo(List<GetClubApplyBean> requestinfo) {
        this.requestinfo = requestinfo;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
