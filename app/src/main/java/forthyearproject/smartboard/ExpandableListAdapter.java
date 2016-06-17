package forthyearproject.smartboard;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final String smartboardUrl = "www.dbsdataprojects.smartboard.com/";
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null || childPosition == 0) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        final VideoView videoView = (VideoView) convertView.findViewById(R.id.lblVideoItem);
        videoView.setVisibility(View.GONE);

        if(childText.contains("Video:http://")) {
            String videoInfo[] = childText.split(":");
            Uri uri = Uri.parse("");
            if(videoInfo.length >= 4)
                uri = Uri.parse(videoInfo[1] + ":" + videoInfo[2]+":"+videoInfo[3]);
            else if(videoInfo.length >= 3)
                uri = Uri.parse(videoInfo[1] + ":" + videoInfo[2]);
            else if(videoInfo.length >= 2)
                uri = Uri.parse(videoInfo[1]);
            else
                Toast.makeText(this._context,"Video Url cannot be obtained",Toast.LENGTH_LONG).show();
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(_context);
            mediaController.setAnchorView(videoView);
            //MediaController containing controls for a MediaPlayer
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);//give focus to a specific view
            videoView.requestFocus();
            videoView.seekTo(0); //set to 100 to get a thumbnail
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();//starts the video
                }
            });
        }
        else {
            TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
            String tempText = childText.substring(0,childText.indexOf(":")+1)+" "+childPosition;
            txtListChild.setText(tempText);
            txtListChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pathToFile = childText.substring(childText.indexOf(":")+1);
                    Uri uri = Uri.parse(pathToFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    _context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
