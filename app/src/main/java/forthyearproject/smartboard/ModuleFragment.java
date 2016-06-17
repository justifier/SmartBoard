package forthyearproject.smartboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModuleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModuleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RefreshHandler refreshHandler;
    TextView msg;
    int num;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private String moduleInfo;
    private int nolectures;
    private String type,user,pass,lectureNo="";
    private String[] lectures;
    private OnFragmentInteractionListener mListener;

    public ModuleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ModuleFragment.
     */
    public static ModuleFragment newInstance(String param1, int param2) {
        ModuleFragment fragment = new ModuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleInfo = getArguments().getString(ARG_PARAM1);
            nolectures = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        msg = (TextView) view.findViewById(R.id.notifications);
        num = 0;
        refreshHandler = new RefreshHandler();
        refreshHandler.sendEmptyMessage(0);
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        // preparing list data
        createListView();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SMARTBOARD_STORAGE", Context.MODE_PRIVATE);
        type = prefs.getString("UserType", "unavailable");
        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setDivider(null);
        expListView.setDividerHeight(0);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);

                if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    return false; //true if we consumed the click, false if not

                } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    //int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    if(type.contains("Lecturer")) {
                        String headerName = (String) expListView.getItemAtPosition(position);
                        String moduleInfo = lectures[0];
                        Intent intent = new Intent(getActivity().getBaseContext(), UploadFiles.class);
                        intent.putExtra("lecture", headerName.substring(headerName.indexOf(":")+1));
                        intent.putExtra("module", moduleInfo.substring(moduleInfo.indexOf(":")+1));
                        startActivity(intent);
                        //do your per-group callback here
                        return true; //true if we consumed the click, false if not
                    }
                    else
                        return false;
                } else {
                    // null item; we don't consume the click
                    return false;
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getting user type
        TextView startLecture = (TextView)this.getActivity().findViewById(R.id.start_lecture);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SMARTBOARD_STORAGE", Context.MODE_PRIVATE);
        type = prefs.getString("UserType", "unavailable");
        user = prefs.getString("User","Test");
        pass = prefs.getString("Password","test");
        int count = lectures.length-1;
        lectureNo = "0";
        while(count != 0 && lectureNo.equals("0")){
            if(lectures[count].contains("Lecture"))
                lectureNo = lectures[count];
            count--;
        }
        int tempNo = Integer.parseInt(lectureNo.substring(lectureNo.indexOf(":")+1));
        tempNo = tempNo+1;
        lectureNo = ""+tempNo;
        if(type.contains("Lecturer")) {
            startLecture.setVisibility(View.VISIBLE);
            startLecture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getBaseContext(),StartLecture.class);
                    intent.putExtra("Module",lectures[0].substring(lectures[0].indexOf(":")+1));
                    intent.putExtra("Password",pass.substring(pass.indexOf(":")+1));
                    intent.putExtra("User",user.substring(user.indexOf(":")+1));
                    intent.putExtra("Lecture",lectureNo);
                    startActivity(intent);
                }
            });
        }
        else {
            startLecture.setVisibility(View.GONE);
        }
    }

    private void createListView() {
        lectures = moduleInfo.split("%%");
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Adding child data
        int temp = 0;
        int j = 0;
        boolean container = false;
        List<String> childValues = new ArrayList<String>();
        for(int i = 0;i < lectures.length;i++){
            if(lectures[i].contains("Lecture:")){
                if(container) {
                    listDataHeader.add(lectures[temp]);
                    listDataChild.put(lectures[temp], childValues); // Header, Child data
                    childValues = new ArrayList<String>();
                }
                temp = i;
                container = true;
            }
            else if(lectures[i].contains("Video:")){
                childValues.add(lectures[i]);
            }
            else if(lectures[i].contains("Notes:")){
                childValues.add(lectures[i]);
            }
            else if(lectures[i].contains("Attachment:")){
                childValues.add(lectures[i]);
            }
            if(container && i == (lectures.length-1)){
                listDataHeader.add(lectures[temp]);
                listDataChild.put(lectures[temp], childValues); // Header, Child data
            }

        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressLint("HandlerLeak")
    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            updateUI();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public void updateUI() {
        String[] notifications = getNotifications();
        if(num == notifications.length)
            num = 0;
        refreshHandler.sleep(6000);
        msg.setText(notifications[num]);
        num++;
    }

    private String[] getNotifications(){
        SharedPreferences preferences;
        String module;
        if(getActivity() != null) {
            preferences = this.getActivity().getBaseContext().getSharedPreferences("SMARTBOARD_STORAGE", Context.MODE_PRIVATE);
            module = preferences.getString("Init", null);
        }
        else
            module = null;
        String[] notifications = {"Updates: \n Lectures cancelled 20/02/16.","Assignments: \n New assignment due.","Tests: \n test on the 17/03/16.", "Notes: \n No new note updates."};
        String[] unavailable = {"unavailable","unavailable","unavailable","unavailable"};
        if(module == null)
            return unavailable;
        else
            return notifications;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
