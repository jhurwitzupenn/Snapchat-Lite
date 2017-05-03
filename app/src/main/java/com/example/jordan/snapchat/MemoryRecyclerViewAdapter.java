package com.example.jordan.snapchat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class MemoryRecyclerViewAdapter extends RecyclerView.Adapter<MemoryRecyclerViewAdapter.CustomViewHolder> {

    final private ArrayList<ParseObject> data = new ArrayList<>();

    MemoryRecyclerViewAdapter() {
        getSavedPhotos();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.memory_list_item_layout, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        ParseObject item = data.get(position);
        ParseFile image = item.getParseFile(ParseConstants.IMAGE_KEY);
        Picasso.with(holder.photo.getContext()).load(image.getUrl())
                                               .resize(ApplicationConstants.IMAGE_WIDTH, ApplicationConstants.IMAGE_HEIGHT)
                                               .into(holder.photo);

        holder.date.setText(item.getString(ParseConstants.DATE_KEY));
        holder.description.setText(item.getString(ParseConstants.CAPTION_KEY));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo) ImageView photo;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.description) TextView description;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void removeItem(int position){
        final ParseObject toBeDeleted = data.remove(position);
        toBeDeleted.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.v("Snapchat", "Successfully deleted Memory");
                } else {
                    Log.e("Snapchat", "Failed to delete Memory");
                }
            }
        });
        notifyItemRemoved(position);
    }

    private void getSavedPhotos() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.OBJECT_MEMORY_NAME);
        query.orderByDescending(ParseConstants.DATE_KEY);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    data.addAll(photoList);
                    notifyDataSetChanged();
                } else {
                    Log.d("Snapchat", e.toString());
                }
            }
        });
    }
}
