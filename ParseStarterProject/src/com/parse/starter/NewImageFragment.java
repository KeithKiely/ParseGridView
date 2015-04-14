package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/*
 * This fragment manages the data entry for a new image object.
 * The user can input an image name, give it a privacy setting(Asign an album),
 * and take a picture. If there is already a photo associated with this image,
 * it will be displayed in the preview at the bottom, which is a standalone
 * ParseImageView.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewImageFragment extends Fragment {
    private ParseFile photoFile;
    private ImageButton photoButton;
    private Button saveButton;
    private Button cancelButton;
    private TextView imageName;
    private Spinner imagePrivacy;
    private ParseImageView imagePreview;
    private final int SELECT_PHOTO = 1;
    private Image image;
    private boolean fromCamera = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Inflates the new image view for editing image details
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_image, parent, false);

        imageName = ((EditText) v.findViewById(R.id.image_name));

        // The imagePrivacy spinner lets people assign privacy of images they've
        // are uploading.
        imagePrivacy = ((Spinner) v.findViewById(R.id.rating_spinner));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.ratings_array,
                        android.R.layout.simple_spinner_dropdown_item);
        imagePrivacy.setAdapter(spinnerAdapter);

        photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openAlert(v);
            }
        });

        saveButton = ((Button) v.findViewById(R.id.save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    image = ((NewImageActivity) getActivity()).getCurrentImage();
                // When the user clicks "Save," upload the image to Parse
                // Add data to the image object:
                image.setTitle(imageName.getText().toString());

                // Associate the image with the current user
                image.setAuthor(ParseUser.getCurrentUser());

                // Add the rating
                //image.setRating(imagePrivacy.getSelectedItem().toString());
                String isPrivate = imagePrivacy.getSelectedItem().toString();
                if (isPrivate.equals("Private Image")) {
                    image.setPrivacyStatus(true);
                } else {
                    image.setPrivacyStatus(false);
                }
                // If the user added a photo, that data will be
                // added in the CameraFragment

                // Save the image and return
                image.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });

        cancelButton = ((Button) v.findViewById(R.id.cancel_button));
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        // Until the user has taken a photo, hide the preview
        imagePreview = (ParseImageView) v.findViewById(R.id.image_preview_image);
        imagePreview.setVisibility(View.INVISIBLE);

        return v;
    }

    //Creates an AlertView to allow the user to pick between the camera and the Gallery
    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.choose_action).setItems(R.array.intent_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check which option is picked and casts it as an integer
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
                Integer selected = (Integer)lv.getTag();
                //Starts camera intent
                if (selected == 0){
                    fromCamera = true;
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(imageName.getWindowToken(), 0);
                    startCamera();
                    //Starts Gallery Intent
                } else{
                    fromCamera = false;
                    Intent photoPickerIntent=new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            }
        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    /*
     * Receives an image from the local gallery and uploads it to parse
     * Compresses the image, Adds it to the image preview
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent );
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        //Locate image uri
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        selectedImage.compress( Bitmap.CompressFormat.JPEG,60,os);
                        //Convert Bitmap to byte array
                        byte[] bitArray = os.toByteArray();
                        photoFile = new ParseFile("image_photo",bitArray);
                        photoFile.saveInBackground(new SaveCallback() {

                            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(getActivity(),
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    addPhotoToimageAndReturn(photoFile);
                                }
                            }
                        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //Bitmap of image being displayed on phone
                        imagePreview.setImageBitmap(selectedImage);

                        //imagePreview.setParseFile(photoFile);
                        imagePreview.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                imagePreview.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    /*
     * When the image is saved successfully, we're return to the NewimageFragment.
     * We look for the fragment we added earlier called "NewimageFragment". which is
     * a CameraFragment.We pop fragments off the stack until we reach that Fragment.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addPhotoToimageAndReturn(ParseFile photoFile) {
        ((NewImageActivity) getActivity()).getCurrentImage().setPhotoFile(
                photoFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("NewimageFragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /*
     * NewImageActivity manages all image data. When adding a image, a custom
     * CameraFragment is started that will take the photo and save it to the
     * image object owned by the NewImageActivity. Create a new CameraFragment, swap
     * the contents of the fragmentContainer (see activity_new_imagee.xml), then
     * add the NewimageFragment to the back stack so we can return to it when the
     * camera is finished.
     */
    public void startCamera() {
        Fragment cameraFragment = new CameraFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainer, cameraFragment);
        transaction.addToBackStack("NewImageFragment");
        transaction.commit();
    }

    /*
     * On resume, check if an Image has been passed from the CameraFragment.
     * If it has, load the image into the fragment and make the preview image
     * visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((NewImageActivity) getActivity())
                .getCurrentImage().getPhotoFile();
        if (photoFile != null) {
            imagePreview.setParseFile(photoFile);
            imagePreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    imagePreview.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}
