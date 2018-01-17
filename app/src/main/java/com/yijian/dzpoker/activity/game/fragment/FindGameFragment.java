package com.yijian.dzpoker.activity.game.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableApi;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableCons;
import com.yijian.dzpoker.http.getmygame.GetMyMatchApi;
import com.yijian.dzpoker.http.getmygame.GetMyMatchCons;
import com.yijian.dzpoker.util.DzApplication;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindGameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindGameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "FindGameFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FindGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindGameFragment newInstance(String param1, String param2) {
        FindGameFragment fragment = new FindGameFragment();
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
//        "getmymatch";
//        "getmygametable";——》entertable
    }

    public void getGames(Activity activity) {
        getMyMatches(activity);
        getMyGameTables(activity);
    }


    private void getMyMatches(Activity activity) {
        DzApplication application = (DzApplication) activity.getApplication();
        try {
            GetMyMatchApi getMyMatchApi = RetrofitApiGenerator.createRequestApi(GetMyMatchApi.class);
            JSONObject param = new JSONObject();
            param.put(GetMyMatchCons.PARAM_KEY_USERID, application.getUserId());

            Call<ResponseBody> callForMyMatches = getMyMatchApi.getResponse(GetMyMatchCons.FUNC_NAME, param.toString());
            callForMyMatches.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Logger.i(TAG, "callForMyMatches response : " + response.body().toString());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getMyGameTables(Activity activity) {
        DzApplication application = (DzApplication) activity.getApplication();
        try {
            GetMyGameTableApi getMyGameTableApi = RetrofitApiGenerator.createRequestApi(GetMyGameTableApi.class);
            JSONObject param = new JSONObject();
            param.put(GetMyGameTableCons.PARAM_KEY_USERID, application.getUserId());

            Call<ResponseBody> callForMyGameTables = getMyGameTableApi.getResponse(GetMyGameTableCons.FUNC_NAME, param.toString());
            callForMyGameTables.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Logger.i(TAG, "callForMyGameTables response : " + response.body().toString());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyMatches(getActivity());
        getMyGameTables(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_game, container, false);
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
