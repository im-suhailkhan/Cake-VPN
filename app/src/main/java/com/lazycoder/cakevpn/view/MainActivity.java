package com.lazycoder.cakevpn.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lazycoder.cakevpn.R;
import com.lazycoder.cakevpn.adapter.ServerListRVAdapter;
import com.lazycoder.cakevpn.interfaces.ChangeServer;
import com.lazycoder.cakevpn.interfaces.NavItemClickListener;
import com.lazycoder.cakevpn.model.Server;

import java.util.ArrayList;

import com.lazycoder.cakevpn.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements NavItemClickListener {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ServerListRVAdapter serverListRVAdapter;
    private DrawerLayout drawer;
    private ChangeServer changeServer;

    public static final String TAG = "CakeVPN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all variable
        initializeAll();

        ImageButton menuRight = findViewById(R.id.navbar_right);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Server List recycler view initialize
        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }

    }

    /**
     * Initialize all object, listener etc
     */
    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);

        fragment = new MainFragment();
        serverListRv = findViewById(R.id.serverListRv);
        serverListRv.setHasFixedSize(true);

        serverListRv.setLayoutManager(new LinearLayoutManager(this));

        serverLists = getServerList();
        changeServer = (ChangeServer) fragment;

    }

    /**
     * Close navigation drawer
     */
    public void closeDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    /**
     * Generate server array list
     */
    private ArrayList getServerList() {



        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String getApiUrl="http://43.205.217.144:8080/vpns";
        ArrayList<Server> servers = new ArrayList<>();



        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray serverArray= null;
                        try {
                            serverArray = new JSONArray(response);
                            for(int i=0;i<serverArray.length();i++){

                                JSONObject json_data = serverArray.getJSONObject(i);
                                String name =json_data.getString("file_name");
                                //String surname =json_data.getString("surname");
                                System.out.println(name+"suhail");
                                servers.add(new Server("United States",
                                        Utils.getImgURL(R.drawable.usa_flag),
                                        name,
                                        "vpn",
                                        "vpn"
                                ));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("That work!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


//        servers.add(new Server("United States",
//                Utils.getImgURL(R.drawable.usa_flag),
//                "us.ovpn",
//                "freeopenvpn",
//                "416248023"
//        ));
//        servers.add(new Server("Japan",
//                Utils.getImgURL(R.drawable.japan),
//                "japan.ovpn",
//                "vpn",
//                "vpn"
//        ));
//        servers.add(new Server("Sweden",
//                Utils.getImgURL(R.drawable.sweden),
//                "sweden.ovpn",
//                "vpn",
//                "vpn"
//        ));
        servers.add(new Server("Korea",
                Utils.getImgURL(R.drawable.korea),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));

        return servers;
    }

    /**
     * On navigation item click, close drawer and change server
     * @param index: server index
     */
    @Override
    public void clickedItem(int index) {
        closeDrawer();
        changeServer.newServer(serverLists.get(index));
    }
}
