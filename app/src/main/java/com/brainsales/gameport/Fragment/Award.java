package com.brainsales.gameport.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsales.gameport.Player.PlayerActivity;
import com.brainsales.gameport.R;
import com.brainsales.gameport.utils.AwardSetting;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by Ryu on 2017-05-23.
 */

public class Award extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<AwardSetting, AwardViewHolder> mFirebaseAdapter;
    private RecyclerView mAwardList = null;
    private View rootView;
    private ProgressDialog mProgress;

    private LinearLayoutManager manager;

    public Award() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_award, container, false);
        mProgress = new ProgressDialog(getActivity());

        mAwardList = (RecyclerView) rootView.findViewById(R.id.award_recycler_view);
        manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reviews");

        manager.setReverseLayout(true);

        mAwardList.setLayoutManager(manager);

        if (mAwardList != null) {
            mAwardList.setHasFixedSize(true);
        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<AwardSetting, AwardViewHolder>(
                AwardSetting.class, R.layout.card_item, AwardViewHolder.class, mDatabase
        ) {
            @Override
            protected void populateViewHolder(AwardViewHolder viewHolder, AwardSetting model, int position) {
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(getActivity(), "Start Player", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        manager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mAwardList.scrollToPosition(positionStart);
                }
            }
        });

        mAwardList.setLayoutManager(manager);
        mFirebaseAdapter.notifyDataSetChanged();
        mAwardList.setAdapter(mFirebaseAdapter);
        return rootView;
    }


    public static class AwardViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public AwardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription(String description) {
            TextView post_description = (TextView) mView.findViewById(R.id.post_description);
            post_description.setText(description);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.card_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

    }

}
