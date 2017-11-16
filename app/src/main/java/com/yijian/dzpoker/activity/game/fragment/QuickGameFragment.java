package com.yijian.dzpoker.activity.game.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;
import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_TITLE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuickGameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuickGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuickGameFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayout btnCreateGame,btnAddintoGame;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View layout_quick;
    private OnFragmentInteractionListener mListener;

    public QuickGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuickGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuickGameFragment newInstance(String param1, String param2) {
        QuickGameFragment fragment = new QuickGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout_quick=inflater.inflate(R.layout.fragment_quick_game, container, false);
        initViews(layout_quick);
        return layout_quick;
    }


    private void initViews(View parent){

        btnCreateGame=(LinearLayout)parent.findViewById(R.id.btnCreateGame);
        btnAddintoGame=(LinearLayout)parent.findViewById(R.id.btnAddintoGame);
        btnCreateGame.setOnClickListener(this);
        btnAddintoGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCreateGame:
                Intent intent = new Intent();
                intent.setClass(getActivity(), com.yijian.dzpoker.activity.game.GameSetActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TextView txCreateGame = (TextView)layout_quick.findViewById(R.id.txCreateGame);
                intent.putExtra(INTENT_KEY_BACKTEXT, "游戏");
                intent.putExtra(INTENT_KEY_TITLE, txCreateGame.getText());
                startActivity(intent);
                break;
            case R.id.btnAddintoGame:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), com.yijian.dzpoker.activity.game.GameAddActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TextView tvAddintoGame = (TextView)layout_quick.findViewById(R.id.txCreateGame);
                intent1.putExtra(INTENT_KEY_BACKTEXT, "游戏");
                intent1.putExtra(INTENT_KEY_TITLE, tvAddintoGame.getText());
                startActivity(intent1);
                break;

        }
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
