package com.brainsales.gameport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostingActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mGameName;
    private EditText mGameType;
    private Button mAnnounce;
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Games");
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectImage = (ImageButton) findViewById(R.id.imageButton);
        mGameName = (EditText) findViewById(R.id.nameText);
        mGameType = (EditText) findViewById(R.id.typeText);
        mAnnounce = (Button) findViewById(R.id.applyButton);
        mProgress = new ProgressDialog(this);


        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }

        });


        mAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAnnounce();

            }
        });

    }


    private void startAnnounce() {

        mProgress.setMessage("Posting to Square");
        mProgress.show();

        final String game_name = mGameName.getText().toString().trim();
        final String game_type = mGameType.getText().toString().trim();

        if(!TextUtils.isEmpty(game_name) && !TextUtils.isEmpty(game_type) && mImageUri != null) {

            StorageReference filepath = mStorage.child("Game_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("GameName").setValue(game_name);
                    newPost.child("GameType").setValue(game_type);
                    newPost.child("image").setValue(downloadUrl.toString());
                    newPost.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mProgress.dismiss();


                }
            });

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);

        }
    }
}
