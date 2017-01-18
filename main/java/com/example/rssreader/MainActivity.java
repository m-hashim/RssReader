package com.example.rssreader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String address;
    private RelativeLayout mDrawerPane;
    private final String FILENAME = "data.txt";
    String[] ini_url=new String[20];
    int noOfLinks;
    static String[] listUrl=new String[20];
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private AlertDialog.Builder alertDialogBuilder;
    private int i = 0;
    private RecyclerView recyclerView;
    static Context context;
    ArrayList<FeedItem> feedItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNavItems.add(new NavItem("Add", "Add a new feed", R.mipmap.ic_action_add));
        mNavItems.add(new NavItem("View List", "View or remove list", R.mipmap.ic_view));
        mNavItems.add(new NavItem("Refresh", "Refresh the list", R.mipmap.ic_action_refresh));
        mNavItems.add(new NavItem("The Man?", "Who's the man??", R.mipmap.ic_action_the_man));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        ListView mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        MainActivity.context=getApplicationContext();
        /////////////////////////////////////////////
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            ini_url[0]="http://www.sciencemag.org/rss/news_current.xml";
            ini_url[1]="http://feeds.bbci.co.uk/news/rss.xml";
            ini_url[2]="http://www.androidpit.com/feed/main.xml";
            try {
                FileOutputStream fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                for(int i=0;i<ini_url.length;i++){
                    if(ini_url[i]==null)break;
                    fileOutputStream.write((ini_url[i]+"\n").getBytes());
                }
                fileOutputStream.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
        readFile();


    }

    public void readFile() {

        try {

            InputStream inputStream = openFileInput(FILENAME);
            if  ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String newLine = null;
                noOfLinks=0;
                while ((newLine = bufferedReader.readLine()) != null ) {
                    listUrl[noOfLinks++]=newLine;
                }
                inputStream.close();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();

        }

        ReadRss readRss = new ReadRss(this, recyclerView);
        for(int i=0;i<listUrl.length&&listUrl[i]!=null;i++)
            readRss.listUrl[i]=listUrl[i];

        if(listUrl[0]==null)
        {
            TextView tv = (TextView) findViewById(R.id.empty);
            tv.setText("Add a new URL. Link is empty");
        }
        readRss.execute();
        feedItems=readRss.getFeeds();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String Link=feedItems.get(position).link;
                        Intent myIntent =new Intent(Intent.ACTION_VIEW,Uri.parse(Link));
                        startActivity(myIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }


    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0: {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.adding, null);

                alertDialogBuilder = new AlertDialog.Builder(this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInputAddress = (EditText) promptsView.findViewById(R.id.urls);


                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                address = userInputAddress.getText().toString();
                                add();
                        ///////////////////////////////////////////////////////////////////////////////////////////
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

            break;
            case 1:
                    if(listUrl[0]==null)
                        Toast.makeText(getApplicationContext(),"List is empty, Add an entry first",Toast.LENGTH_LONG).show();
                    else {
                        Intent zz = new Intent(this, removing.class);
                        startActivity(zz);
                        readFile();
                    }
                        break;
            case 2: readFile();
                    break;
            case 3:
                    Toast.makeText(getApplicationContext(), "You're the man!", Toast.LENGTH_LONG).show();
                    break;

        }


        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    private void add() {

        try {
            FileOutputStream fileOutputStream = openFileOutput(FILENAME, Context.MODE_APPEND);
            String url=address;
            if(url.length()>1) {
                while (url.endsWith("\n")){
                    url=url.substring(0,url.length()-1);
                }
                while( url.startsWith("\n")){
                    url=url.substring(1,url.length());
                }

                boolean already=false;
                for(String s:listUrl){
                    if(s==url){
                        already=true;
                        Toast.makeText(getApplicationContext(),"Link Already Exists",Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                try {
                    if (Patterns.WEB_URL.matcher(url).matches() && !already)
                        fileOutputStream.write(((url + "\n").getBytes()));
                    else{
                        Toast.makeText(getApplicationContext(),"Invalid url address",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){};
            }

            fileOutputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        TextView tv = (TextView) findViewById(R.id.empty);
        tv.setText("");
        readFile();
    }
}
//////////////////////////////////////////////////////////////
class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}

/////////////////////////////////////////////////////////////////
class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText( mNavItems.get(position).mTitle );
        subtitleView.setText( mNavItems.get(position).mSubtitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}


class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}
/*

"MyRSS will be a native Android application, which will provide a simple and fast
way to stay tuned with web updates on the go from the user’s favorite websites. It
will provide a simple, intuitive and clutter free user interface.
MyRSS will include features like a) tracking of feeds easily, b) addition of new feeds
from a featured list, or via your own URL, c) preview summaries of articles, and d)
read full articles right on the Smartphone.
The solution has to be developed using IBM Worklight Studio. The target device will
be an Android Phone. The development will follow the IBM’s Rational Unified
Process."

 */
