package com.example.selfchat3_1;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public ArrayList<String> data;
    private recItemOnLongClick clickedMessage;
    private int data_size;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public interface recItemOnLongClick {
        void itemLongClick(View view, final int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView textView;
        public MyViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.textView_one_message_template);
            view.setOnLongClickListener(this);
        }

        public boolean onLongClick(View view){
            if (clickedMessage != null) {
                clickedMessage.itemLongClick(view, getAdapterPosition());
            }
            return true;
        }
    }

    public MyAdapter(int size, SharedPreferences other_sp, SharedPreferences.Editor other_editor){
        this.data = new ArrayList<String>();
        this.data_size = size;
        this.sp = other_sp;
        this.editor = other_editor;
        this.gson = new Gson();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_one_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add_message(String message){
        this.data.add(message);
        data_size += 1;
        saveEditedData();
        notifyDataSetChanged();
    }

    public void delete_message(int position){
        this.data.remove(position);
        data_size -= 1;
        saveEditedData();
        notifyDataSetChanged();
    }

    public void saveEditedData()
    {
        editor.putInt(MainActivity.SP_DATA_SIZE_KEY, this.data_size);
        String wjson = gson.toJson(this.data);
        editor.putString(MainActivity.SP_DATA_LIST_KEY, wjson);
        editor.apply();
    }

    public void setClickListener(recItemOnLongClick itemClick) {
        this.clickedMessage = itemClick;
    }

    public void loadData() {

        String rjson = sp.getString(MainActivity.SP_DATA_LIST_KEY, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        this.data = gson.fromJson(rjson, type);
    }

    public void supportConfigurationChange()
    {
        notifyDataSetChanged();
    }
}