package com.example.rssreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.util.ArrayList;
import static android.R.id.list;
import static com.example.rssreader.MainActivity.listUrl;


public class removing extends AppCompatActivity {

    private int noOfLinks,pos;
    private final String FILENAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.removing);

        Toast.makeText(getApplicationContext(),"Click on any link to remove it",Toast.LENGTH_SHORT).show();

        final ListView listview = (ListView) findViewById(R.id.listview);

       String[] valList = {"1\t","2\t","3\t","4\t","5\t","6\t","7\t","8\t","9\t","10\t","11\t","12\t","13\t","14\t","15\t","16\t"

                                        ,"17\t","18\t","19\t","20\t"};

      //  for(int i=0;i<listUrl.length;i++)
       //   valList[i]=listUrl[i];
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i <valList.length; ++i) {
            if(listUrl[i]==null)
                break;
            list.add(valList[i]+" "+listUrl[i]);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listele, list);
        listview.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do do my action here
                noOfLinks=0;
                while(listUrl[noOfLinks++] !=null);

                try{
                    clearPos(pos);
                    Toast.makeText(getApplicationContext(),"Removed!",Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e){

                }
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // I do not need any action here you might
                dialog.dismiss();
            }
        });


/*
        TextView tv = (TextView) findViewById(R.id.empty);

        if(listUrl[0]==null)
        {
            tv.setText("Add a new URL. Link is empty");
        }
        else
        {
            tv.setText("");
        }
*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                pos=position;
                AlertDialog alert = builder.create();
                alert.show();
                    }
            });
    }

            private void clearPos(int position) {
                int i;
                for (i = position; i < noOfLinks - 1; i++) {
                    listUrl[i] = listUrl[i + 1];
                }
                listUrl[i] = null;
                noOfLinks--;
                try {
                    FileOutputStream fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    for (i = 0; i < listUrl.length-1; i++) {
                        if (listUrl[i] == null) break;
                        fileOutputStream.write((listUrl[i] + "\n").getBytes());
                    }
                    fileOutputStream.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }

    public void goBack(View view) {
        finish();
    }
}
