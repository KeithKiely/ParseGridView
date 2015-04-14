package com.parse.starter;

/*
 * Camera Fragment controlls the camera, it allows the user
 * to take a picture. It thens passes it back to NewImageFragment
 * for further editing.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile photoFile;
    private ImageButton photoButton;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        //Inflates camera Fragment to allow the user to take a picture
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);
        //Assigns the camera button
        photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

        //Check to see if the device has a camera and alerts the user if a camera isn't present
        if (camera == null) {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
        //on click listener that takes a photo and passes it to savedScaledPhoto method
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        // nothing to do not playing shot audio
                    }
                }, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        saveScaledPhoto(data);
                    }

                });

            }
        });

        //ImageView that displays what the camera sees
        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // nothing here
            }

        });

        return v;
    }

    /*
     * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
     * they are saved. Since we never need a full-size image in our app, we'll
     * save a scaled one right away.
     */
    private void saveScaledPhoto(byte[] data) {

        // Resize photo from camera byte array
        Bitmap imageImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        //Only used if images are being scaled
        /*Bitmap imageImageScaled = Bitmap.createScaledBitmap(imageImage, 500, 500
                * imageImage.getHeight() / imageImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledimageImage = Bitmap.createBitmap(imageImageScaled, 0,
                0, imageImageScaled.getWidth(), imageImageScaled.getHeight(),
                matrix, true);*/

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageImage.compress(Bitmap.CompressFormat.JPEG, 80, bos);

        byte[] scaledData = bos.toByteArray();

        //Save the scaled image to Parse
        photoFile = new ParseFile("image_photo.jpg", scaledData);
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
    }

    /*
     * Once the image has saved, we're return to NewimageFragment.
     * The CameraFragment is added to the back stack, we named it
     * "NewimageFragment". Now we'll pop fragments off the back stack
     * until we reach that Fragment.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addPhotoToimageAndReturn(ParseFile photoFile) {
        ((NewImageActivity) getActivity()).getCurrentImage().setPhotoFile(
                photoFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("NewimageFragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

}
