package com.example.jordan.snapchat;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.CustomViewHolder> {

    final private ArrayList<ParseObject> data = new ArrayList<>();
    private MessageActivity mMessageActivity;

    MessageRecyclerViewAdapter(MessageActivity messageActivity) {
        mMessageActivity = messageActivity;
        getMessages();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.message_list_item_layout, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        ParseObject item = data.get(position);
        String sentFromUsername = item.getString(ParseConstants.USERNAME_KEY);
        String sentDate = item.getString(ParseConstants.DATE_KEY);
        holder.messageDescription.setText(mMessageActivity.getString(R.string.message_delivery_text, sentFromUsername, sentDate));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position){
        if (position < data.size()) {
            final ParseObject toBeDeleted = data.remove(position);
            toBeDeleted.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.v("Snapchat", "Successfully deleted message");
                    } else {
                        Log.e("Snapchat", "Failed to delete message");
                    }
                }
            });
            notifyItemRemoved(position);
        }
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_description) TextView messageDescription;
        @BindView(R.id.view_message_button) Button viewMessageButton;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.view_message_button)
        void viewMessage(){
            final int position = this.getAdapterPosition();
            ParseObject message = data.get(position);

            message.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseFile image = object.getParseFile(ParseConstants.IMAGE_KEY);
                        final AlertDialog.Builder message = new AlertDialog.Builder(mMessageActivity);
                        LayoutInflater factory = LayoutInflater.from(mMessageActivity);
                        final View view = factory.inflate(R.layout.message_dialog, null);
                        Picasso.with(mMessageActivity).load(image.getUrl())
                                                      .resize(ApplicationConstants.IMAGE_WIDTH, ApplicationConstants.IMAGE_HEIGHT)
                                                      .into(((ImageView) view.findViewById(R.id.dialog_imageview)));
                        ((TextView) view.findViewById(R.id.dialog_textview)).setText(object.getString(ParseConstants.CAPTION_KEY));
                        message.setView(view);
                        final AlertDialog alertDialog = message.create();
                        alertDialog.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                removeItem(position);
                            }
                        }, 1000 * Integer.parseInt(object.getString(ParseConstants.TIMER_KEY)));
                    } else {
                        Log.e("Snapchat", e.toString());
                    }
                }
            });
        }
    }

    private void getMessages() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.OBJECT_MESSAGE_NAME);
        query.selectKeys(Arrays.asList(ParseConstants.DATE_KEY, ParseConstants.USERNAME_KEY));
        query.orderByDescending(ParseConstants.DATE_KEY);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    data.addAll(photoList);
                    notifyDataSetChanged();
                } else {
                    Log.e("Snapchat", e.toString());
                }
            }
        });
    }
}
