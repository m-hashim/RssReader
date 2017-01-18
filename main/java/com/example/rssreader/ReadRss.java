package com.example.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ReadRss extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ProgressDialog progressDialog;
    private ArrayList<FeedItem>feedItems;
    private RecyclerView recyclerView;
    private URL url;
    private String ur;
    public String[] listUrl=new String[20];

    public ReadRss(Context context,RecyclerView recyclerView) {
        this.recyclerView=recyclerView;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        feedItems=new ArrayList<>();
        ur="http://www.thesadsongco.com/media/images/notfound.jpg";
        try {
            url = new URL(ur);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<FeedItem> getFeeds(){return feedItems;}

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        MyAdapter adapter=new MyAdapter(context,feedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new VerticalSpace(50));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected Void doInBackground(Void... params) {
        int i=0;

        while(listUrl[i]!=null)
            ProcessXml(Getdata(listUrl[i++]));

        return null;
    }

    public Document Getdata(String address) {
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ProcessXml(Document data) {
        if (data != null) {

            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node cureentchild = items.item(i);

                if (cureentchild.getNodeName().equalsIgnoreCase("item")) {
                    FeedItem item=new FeedItem();
                    item.setThumbnailUrl(ur);
                    NodeList itemchilds = cureentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node cureent = itemchilds.item(j);

                        if (cureent.getNodeName().equalsIgnoreCase("title")){
                            item.setTitle(cureent.getTextContent());
                        }else if (cureent.getNodeName().equalsIgnoreCase("description")){
                            item.setDescription(cureent.getTextContent());
                        }else if (cureent.getNodeName().equalsIgnoreCase("pubDate")){
                            item.setPubDate(cureent.getTextContent());
                        }else if (cureent.getNodeName().equalsIgnoreCase("link")){
                            item.setLink(cureent.getTextContent());
                        }else if (cureent.getNodeName().equalsIgnoreCase("media:thumbnail")){
                            //this will return us thumbnail url
                            String url=null;
                            for(int k=0;k<cureent.getAttributes().getLength();k++)
                            {
                                if(cureent.getAttributes().item(k).getNodeName().equalsIgnoreCase("url"))
                                {
                                    url = cureent.getAttributes().item(k).getTextContent();
                                    break;
                                }
                            }
                            item.setThumbnailUrl(url);
                        }
                    }
                    feedItems.add(item);

                }
            }
        }
    }


}
