package com.icofound.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.icofound.Activity.CommentActivity;
import com.icofound.Activity.PdfViewerActivity;
import com.icofound.Activity.PostActivity;
import com.icofound.Activity.ProfileActivity;
import com.icofound.Activity.SendReportActivity;
import com.icofound.Constant;
import com.icofound.Fragments.StoriesFragment;
import com.icofound.Model.Comment;
import com.icofound.Model.Like;
import com.icofound.Model.Post;
import com.icofound.Model.User;
import com.icofound.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Activity activity;
    List<Post> postlist = new ArrayList<>();
    User user;
    Fragment parentFragment;
    SparseBooleanArray mCollapsedStatus;
    ArrayList<Like> likeList;
    ArrayList<Comment> commentList;
    Fragment fragment;

    public UserAdapter(Activity activity, List<Post> users, User user, Fragment parentFragment, ArrayList<Like> likeList, ArrayList<Comment> commentList) {
        this.postlist = users;
        this.activity = activity;
        this.user = user;
        this.parentFragment = parentFragment;
        this.mCollapsedStatus = new SparseBooleanArray();
        this.likeList = likeList;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.otheruser_list, parent, false);
        UserAdapter.UserViewHolder viewHolder = new UserAdapter.UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (postlist.get(position).getUserId().equalsIgnoreCase(user.getId())) {
            holder.status.setVisibility(View.VISIBLE);
            if (postlist.get(position).getisApproved()) {
                holder.status.setText("Approved");
            } else {
                holder.status.setText("Pending");
            }
        }



            Glide.with(activity).load(postlist.get(position).getProfilePicLink()).placeholder(R.drawable.ic_person).into(holder.img);



        holder.username.setText(postlist.get(position).getUserName());
        holder.expand_text_view.setText(postlist.get(position).getPost(),  mCollapsedStatus, position);

        for (int i = 0; i < likeList.size(); i++) {

            try {
                if (likeList.get(i).getPostId().equalsIgnoreCase(postlist.get(position).getId())) {


                    if (user.getId().equalsIgnoreCase(likeList.get(i).getUid())) {
                        holder.btnLiked.setVisibility(View.VISIBLE);
                        holder.btnLike.setVisibility(View.GONE);
                        break;
                    }
                }
            }catch (Exception e){

            }

        }

        AtomicInteger likeCounter = new AtomicInteger();


        //---------for like count
        for (int i = 0; i < likeList.size(); i++) {

            if (likeList.get(i).getPostId().equalsIgnoreCase(postlist.get(position).getId())){
                likeCounter.set(likeCounter.get() + 1);
            }

        }
        if (likeCounter.get() != 0){
            holder.tvNoOfLike.setText(likeCounter+ " Likes");
            holder.tvNoOfLike.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvNoOfLike.setVisibility(View.INVISIBLE);
        }


 //---------for comments count
        int commentCount = 0;
        for (int i = 0; i < commentList.size(); i++) {

            if (commentList.get(i).getPostId().equalsIgnoreCase(postlist.get(position).getId())){
                commentCount = commentCount+1;
            }

        }
        if (commentCount != 0){
            holder.tvNoOfComments.setText(commentCount+ " Comments");
            holder.tvNoOfComments.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvNoOfComments.setVisibility(View.INVISIBLE);
        }



        holder.btnLike.setOnClickListener(view -> {
            holder.btnLike.setVisibility(View.GONE);
            holder.btnLiked.setVisibility(View.VISIBLE);
            new StoriesFragment().postLike(postlist.get(position), user);


            likeCounter.set(likeCounter.get() + 1);
            holder.tvNoOfLike.setText(likeCounter+ " Likes");

        });

        holder.btnLiked.setOnClickListener(view -> {
            holder.btnLiked.setVisibility(View.GONE);
            holder.btnLike.setVisibility(View.VISIBLE);
            new StoriesFragment().deleteLike(postlist.get(position).getId(), user);

            likeCounter.set(likeCounter.get() - 1);
            holder.tvNoOfLike.setText(likeCounter+ " Likes");
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ProfileActivity.class);
                if (postlist.get(holder.getAdapterPosition()).getUserId().equalsIgnoreCase(user.getId())) {
                    i.putExtra("frommain", false);
                    i.putExtra("myprofile", true);
                    i.putExtra("Uid", user.getId());
                } else {
                    i.putExtra("frommain", false);
                    i.putExtra("myprofile", false);
                    i.putExtra("Uid", postlist.get(holder.getAdapterPosition()).getUserId());
                    i.putExtra("loginuid", user.getId());

                }
                activity.startActivity(i);
            }

        });


        if (postlist.get(position).getPostImgLink() != null || !TextUtils.isEmpty(postlist.get(position).getPostImgLink())) {
                holder.layImage.setVisibility(View.VISIBLE);
            if (postlist.get(position).getPostImgLink().toLowerCase().contains(".pdf")) {
                Constant.generatePDF( holder.tv_post_image,postlist.get(position).getPostImgLink() ,1000 );
                holder.tvViewPdf.setVisibility(View.VISIBLE);
            }
            else {
                Glide.with(activity).load(postlist.get(position).getPostImgLink()).placeholder(R.drawable.loading).into(holder.tv_post_image);
                holder.tvViewPdf.setVisibility(View.GONE);

            }
        } else {
            holder.tv_post_image.setVisibility(View.GONE);


        }


        holder.tvViewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfViewerActivity.startPdfViewLib(postlist.get(position).getPostImgLink() , activity);
            }
        });

//        PostAdapter adapter = new PostAdapter(activity, postlist.get(position));
//        holder.post_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//        holder.post_list.setAdapter(adapter);

//        if (postlist.get(position).getUserId().equalsIgnoreCase(user.getId())){
//            holder.more.setVisibility(View.VISIBLE);
//        }else {
//            holder.more.setVisibility(View.GONE);
//        }

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (postlist.get(position).getUserId().equalsIgnoreCase(user.getId())) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogStyle);
                    bottomSheetDialog.setContentView(R.layout.stories_buttom_sheet);

                    LinearLayout cancel = bottomSheetDialog.findViewById(R.id.cancel);
                    LinearLayout edit = bottomSheetDialog.findViewById(R.id.edit);
                    LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent(activity, PostActivity.class);
                            intent.putExtra("user", user);
                            intent.putExtra("post", postlist.get(holder.getAdapterPosition()));
                            activity.startActivityForResult(intent, 200);
                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bottomSheetDialog.dismiss();
                            Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.delete_post);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            TextView cancel = dialog.findViewById(R.id.cancel);
                            TextView delete = dialog.findViewById(R.id.delete);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    new StoriesFragment().deletepost(postlist.get(holder.getAdapterPosition()).getId());
                                }
                            });

                            dialog.show();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bottomSheetDialog.show();

                } else {

                    Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.custom_report);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView texts = dialog.findViewById(R.id.texts);
                    texts.setText("Do you want to report this post?");

                    TextView cancel = dialog.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView report = dialog.findViewById(R.id.report);
                    report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(activity, SendReportActivity.class);
                            i.putExtra("postlist", postlist.get(holder.getAdapterPosition()).getId());
                            activity.startActivity(i);

                            dialog.dismiss();
                        }
                    });

                    dialog.show();


                }

            }
        });


        if (postlist.get(position).getComments() != null)
        holder.tvNoOfComments.setText(postlist.get(position).getComments().size()+"");

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CommentActivity.class);
                intent.putExtra("user", user.getId());
                intent.putExtra("post", postlist.get(holder.getAdapterPosition()).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView expand_text_view;
        TextView username, post_text, status;
        RoundedImageView img;
        //        RecyclerView post_list;
        ImageView more, comment, tv_post_image, btnLike, btnLiked;
        RelativeLayout  tvViewPdf;
        RelativeLayout layImage;
        TextView tvNoOfComments, tvNoOfLike;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            layImage = itemView.findViewById(R.id.layImage);
            username = itemView.findViewById(R.id.username);
            tvViewPdf = itemView.findViewById(R.id.tvViewPdf);
            img = itemView.findViewById(R.id.img);
            more = itemView.findViewById(R.id.more);
            post_text = itemView.findViewById(R.id.post_text);
            comment = itemView.findViewById(R.id.comment);
            tv_post_image = itemView.findViewById(R.id.tv_post_image);
            expand_text_view = itemView.findViewById(R.id.expand_text_view);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnLiked = itemView.findViewById(R.id.btnLiked);
            status = itemView.findViewById(R.id.status);
            tvNoOfComments = itemView.findViewById(R.id.tvNoOfComments);
            tvNoOfLike = itemView.findViewById(R.id.tvNoOfLike);

        }
    }

}
