package com.puc.parte_electronico.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.puc.parte_electronico.R;
import com.puc.parte_electronico.TicketActivity;
import com.puc.parte_electronico.globals.ImageTransform;
import com.puc.parte_electronico.model.Picture;
import com.puc.parte_electronico.model.TrafficTicket;

import java.io.File;
import java.util.UUID;

/**
 * Created by jose on 5/13/14.
 */
public class TicketPicturesFragment extends Fragment implements ITicketFragment {
    public static final String TAG = "TICKET_PICTURES_FRAGMENT";
    public static final int BACKGROUND_CAMERA_CODE = 1000;
    public static final int EVIDENCE_CAMERA_CODE = 1001;
    public static final String PICTURES_KEY = "PICTURES_KEY";

    private static final String TEMP_PHOTO_FILE_NAME = "TempPhoto.jpg";
    private static final int PHOTO_WIDTH = 640;
    private static final int PHOTO_HEIGHT = 480;
    private static final int THUMBNAIL_SIZE = 100;

    private ViewGroup mBackgroundPicturesContainer;
    private ViewGroup mEvidencePicturesContainer;

    private Animator mCurrentAnimator;

    private TrafficTicket mTicket;

    private boolean mEditable;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTicket = arguments.getParcelable(TrafficTicket.TICKET_KEY);
            mEditable = arguments.getBoolean(TicketActivity.EDITABLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_pictures, container, false);

        Button button = (Button)view.findViewById(R.id.button_add_background);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(BACKGROUND_CAMERA_CODE);
            }
        });
        button.setEnabled(mEditable);

        button = (Button)view.findViewById(R.id.button_add_evidence);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(EVIDENCE_CAMERA_CODE);
            }
        });
        button.setEnabled(mEditable);

        mBackgroundPicturesContainer = (ViewGroup)view.findViewById(R.id.background_container);
        mEvidencePicturesContainer = (ViewGroup)view.findViewById(R.id.evidence_container);

        initializeView(view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        IFragmentCallbacks callback = (IFragmentCallbacks)getActivity();
        callback.updateTicket(mTicket);
    }

    @Override
    public TrafficTicket getTicket() {
        return mTicket;
    }

    private void initializeView(View view) {
        for (Picture picture : mTicket.getPictures()) {
            addPicture(picture.getBitmap(), picture.getType());
        }
    }

    private void takePicture(int code) {
        File outputFile = getTempPhotoPath();
        outputFile.delete();
        Uri tempPhotoUri =  Uri.fromFile(outputFile);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
        getActivity().startActivityForResult(cameraIntent, code);
    }

    private File getTempPhotoPath() {
        return new File(getPictureDirectory(), TEMP_PHOTO_FILE_NAME);
    }

    private File getRandomPicturePath() {
        String fileName = "Picture_" + UUID.randomUUID().toString() + ".jpg";
        return new File(getPictureDirectory(), fileName);
    }

    private File getPictureDirectory() {
        File pictureDirectory;
        boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            pictureDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            pictureDirectory = new File(getActivity().getFilesDir(), "Pictures");
        }
        return pictureDirectory;
    }

    public void handlePicture(int code) {
        int type;
        if (code == BACKGROUND_CAMERA_CODE) {
            type = Picture.PICTURE_TYPE_BACKGROUND;
        } else {
            type = Picture.PICTURE_TYPE_EVIDENCE;
        }


        File photo = getTempPhotoPath();
        if (photo.exists()) {
            PictureTransformTask task = new PictureTransformTask(photo, type);
            task.execute();
        } else {
            // TODO: photo was not successfully saved, handle error appropriately.
        }
    }

    private void addPicture(final Bitmap picture, int type) {
        ImageButton imageButton = new ImageButton(getActivity());
        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        float dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMBNAIL_SIZE, getResources().getDisplayMetrics());
        imageButton.setMaxHeight((int) dimension);
        //imageView.setMaxWidth((int)dimension);
        imageButton.setAdjustViewBounds(true);
        imageButton.setImageBitmap(picture);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImage((ImageButton) v, picture);
            }
        });


        if (type == Picture.PICTURE_TYPE_BACKGROUND) {
            mBackgroundPicturesContainer.addView(imageButton);
        } else {
            mEvidencePicturesContainer.addView(imageButton);
        }
    }

    private void zoomImage(final ImageView v, Bitmap bitmap) {
        ViewGroup container = (ViewGroup) getView().findViewById(R.id.container);
        final ImageView zoomedView = (ImageView) getView().findViewById(R.id.zoomed_image);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        v.getGlobalVisibleRect(startBounds);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        zoomedView.setPivotX(0);
        zoomedView.setPivotY(0);

        zoomedView.setImageBitmap(bitmap);
        v.setAlpha(0f);
        zoomedView.setVisibility(ImageView.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(zoomedView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(zoomedView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(zoomedView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(zoomedView, View.SCALE_Y, startScale, 1f));
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();

        final float startScaleFinal = startScale;
        zoomedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(zoomedView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(zoomedView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(zoomedView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(zoomedView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(300);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setAlpha(1f);
                        zoomedView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        v.setAlpha(1f);
                        zoomedView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    class PictureTransformTask extends AsyncTask<Void, Void, Bitmap> {
        private File mPicture;
        private File mOutputFile;
        private int mType;

        public PictureTransformTask(File picture, int type) {
            mPicture = picture;
            mType = type;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            ImageTransform it = new ImageTransform.Builder(mPicture)
                    .correctOrientation()
                    .keepAspectRatio()
                    .resize(PHOTO_WIDTH, PHOTO_HEIGHT)
                    .build();

            mOutputFile = getRandomPicturePath();
            it.transformAndSave(mOutputFile, true);

            if (mOutputFile.exists()) {
                return it.getBitmap();
            } else {
                // Error
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap thumbnail) {
            if (thumbnail != null) {
                Picture picture = new Picture(mTicket, mOutputFile.getAbsolutePath(), mType);
                picture.setBitmap(thumbnail);
                mTicket.addPicture(picture);

                addPicture(thumbnail, mType);
            } else {
                // TODO: photo was not successfully saved, handle error appropriately.
            }

        }
    }

}
