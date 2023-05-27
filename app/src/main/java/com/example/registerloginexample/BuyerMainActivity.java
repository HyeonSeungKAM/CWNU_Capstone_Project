package com.example.registerloginexample;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BuyerMainActivity extends AppCompatActivity {

    private Button btn_logout, btn_purchasedlist, btn_list; // 판매하기, 목록 버튼
    private RecyclerView recyclerView;

    // 서버에서 가져온 내용 보여주기
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buyer);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        String url = "http://gamhs44.ivyro.net/fullbins.php"; // PHP 파일의 URL
        new GetDataTask().execute(url);

        TextView tv_id = findViewById(R.id.tv_id);
        TextView tv_name = findViewById(R.id.tv_name);

        Intent intent = getIntent();
        String kind = intent.getStringExtra("kind");
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");

        tv_id.setText(userID);
        tv_name.setText(userName);


        btn_logout = findViewById(R.id.btn_logout);// 목록
        btn_logout.setOnClickListener(new View.OnClickListener() {    // 목록
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        btn_purchasedlist = findViewById(R.id.btn_purchasedlist); // 판매하기
        btn_purchasedlist.setOnClickListener(new View.OnClickListener() {    // 목록
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerMainActivity.this, PurchasedListActivity.class);
                intent.putExtra("kind",kind);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);

                startActivity(intent);

            }
        });


        btn_list = findViewById(R.id.btn_list); // 목록
        btn_list.setOnClickListener(new View.OnClickListener() {    // 목록
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerMainActivity.this, ListActivity.class);
                intent.putExtra("kind",kind);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);

                startActivity(intent);

            }
        });
    }
    private class GetDataTask extends AsyncTask<String, Void, List<DataItem>> {
        @Override
        protected List<DataItem> doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject  jsonObject = new JSONObject(response.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                List<DataItem> dataList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject itemObject = jsonArray.getJSONObject(i);
                    String userID = itemObject.getString("userID");
                    String binName = itemObject.getString("binName");
                    String binLoc = itemObject.getString("binLoc");
                    String glass_full = itemObject.getString("glass_full");
                    String plastic_full = itemObject.getString("plastic_full");
                    String paper_full = itemObject.getString("paper_full");
                    String metal_full = itemObject.getString("metal_full");
                    String status = null;

                    if (glass_full.equals("1")) {
                        status = "유리병 수거함 가득참";

                    } else if (plastic_full.equals("1")) {
                        status = "플라스틱 수거함 가득참";


                    } else if(paper_full.equals("1")) {
                        status = "종이 수거함 가득참";

                    } else if(metal_full.equals("1")) {
                        status = "고철 수거함 가득참";

                    };

                    DataItem dataItem = new DataItem(userID, binName, binLoc, status, glass_full,plastic_full,paper_full,metal_full);
                    dataList.add(dataItem);
                }
                return dataList;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DataItem> result) {
            if (result != null) {
                MyAdapter adapter = new MyAdapter(result);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<DataItem> data;

        public MyAdapter(List<DataItem> data) {
            this.data = data;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_itm_userID, tv_itm_binName, tv_itm_binLoc, tv_itm_glass_f,
                    tv_itm_plastic_f, tv_itm_paper_f, tv_itm_metal_f,tv_itm_status;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_itm_userID = itemView.findViewById(R.id.tv_itm_userID);
                tv_itm_binName = itemView.findViewById(R.id.tv_itm_binName);
                tv_itm_binLoc = itemView.findViewById(R.id.tv_itm_binLoc);
                tv_itm_status = itemView.findViewById(R.id.tv_itm_status);
                tv_itm_glass_f = itemView.findViewById(R.id.tv_itm_glass_f);
                tv_itm_plastic_f = itemView.findViewById(R.id.tv_itm_plastic_f);
                tv_itm_paper_f = itemView.findViewById(R.id.tv_itm_paper_f);
                tv_itm_metal_f = itemView.findViewById(R.id.tv_itm_metal_f);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_binfull, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DataItem item = data.get(position);
            holder.tv_itm_userID.setText(item.getUserID());
            holder.tv_itm_binLoc.setText(item.getBinLoc());
            holder.tv_itm_binName.setText(item.getBinName());
            holder.tv_itm_status.setText(item.getStatus());
            holder.tv_itm_glass_f.setText(item.getGF());
            holder.tv_itm_plastic_f.setText(item.getPLF());
            holder.tv_itm_paper_f.setText(item.getPPF());
            holder.tv_itm_metal_f.setText(item.getMF());

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(80, 0, 80, 0); // 왼쪽과 오른쪽 마진을 16dp로 설정
            holder.itemView.setLayoutParams(layoutParams);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class DataItem {
        private String userID;
        private String binName;
        private String binLoc;
        private String status;
        private String glass_full;
        private String plastic_full;
        private String paper_full;
        private String metal_full;

        public DataItem(String userID, String binName, String binLoc, String status, String glass_full, String plastic_full, String paper_full, String metal_full) {
            this.userID = userID;
            this.binName = binName;
            this.binLoc = binLoc;
            this.status = status;
            this.glass_full = glass_full;
            this.plastic_full = plastic_full;
            this.paper_full = paper_full;
            this.metal_full = metal_full;
        }

        public String getUserID() {
            return userID;
        }

        public String getBinName() {
            return binName;
        }

        public String getBinLoc() {
            return binLoc;
        }
        public String getStatus() {return status;}

        public String getGF() {
            return glass_full;
        }

        public String getPLF() {
            return plastic_full;
        }

        public String getPPF() {
            return paper_full;
        }
        public String getMF() {
            return metal_full;
        }

    }

}