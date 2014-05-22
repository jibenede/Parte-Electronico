package com.puc.parte_electronico.globals;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for the manipulation of bitmaps. Supports common transformations such as rotation and scaling.
 * An instance of this class should always be obtained through its builder {@link ImageTransform.Builder}.
 *
 * @author Jose Benedetto
 */
public class ImageTransform {
    private static final String TAG = "Frogmi";

    private File mImageFile;
    private boolean mCorrectOrientation;
    private boolean mResize;
    private int mTargetWidth;
    private int mTargetHeight;
    private int mCompression;
    private boolean mKeepAspectRatio;
    private Bitmap.CompressFormat mFormat;

    private Bitmap mCache;

    private ImageTransform(File imageFile, boolean correctOrientation, boolean resize,
                             int targetWidth, int targetHeight, int compression, boolean keepAspectRatio,
                             Bitmap.CompressFormat format) {
        mImageFile = imageFile;
        mCorrectOrientation = correctOrientation;
        mResize = resize;
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
        mCompression = compression;
        mKeepAspectRatio = keepAspectRatio;
        mFormat = format;
    }

    public void transformAndSave(File outputFile, boolean makeIntermediaryFolders) {
        if (mCache == null) {
            transform();
        }
        Bitmap bitmap = mCache;

        if (makeIntermediaryFolders) {
            mImageFile.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            boolean success = bitmap.compress(mFormat, mCompression, fos);
            if (success) {
                Log.i(TAG, "Picture was saved successfully.");
            } else {
                Log.i(TAG, "Picture was not saved successfully.");
            }

            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Bitmap getBitmap() {
        if (mCache == null) {
            transform();
        }
        return mCache;
    }

    private Bitmap transform() {
        Bitmap photo;
        if (mResize) {
            // First we seek to figure out the dimensions of the photo for future reference.
            Options bitmapOptions = new Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mImageFile.getAbsolutePath(), bitmapOptions);
            int outWidth = bitmapOptions.outWidth;
            int outHeight = bitmapOptions.outHeight;

            // We seek pictures of 640 x 480. We attempt to get the smallest possible size of picture such that we lose no quality.
            // That means the resulting picture must be as small as possible while also being over 640 x 480.
            // However, Android APIs allow for image scaling only in powers of two. We therefore calculate x = min(log2(width/640), log2(height/480)).
            // It can be proved that the optimal scale ratio for our picture is 2^(int)x.
            double widthFactor = outWidth / (double) mTargetWidth;
            double heightFactor = outHeight / (double) mTargetHeight;

            double widthLog = Math.log(widthFactor) / Math.log(2);
            double heighLog = Math.log(heightFactor) / Math.log(2);
            int minLog = (int) Math.min(widthLog, heighLog);
            int sampleSize = (int) Math.pow(2, minLog);

            bitmapOptions = new Options();
            bitmapOptions.inSampleSize = sampleSize;
            photo = BitmapFactory.decodeFile(mImageFile.getAbsolutePath(), bitmapOptions);
        } else {
            // No resize. Be cautious when using this, it may give memory errors on low end devices.
            photo = BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
        }

        int rotation = 0;
        if (mCorrectOrientation) {
            int exifOrientation = ExifInterface.ORIENTATION_NORMAL;
            try {
                ExifInterface exif = new ExifInterface(mImageFile.getAbsolutePath());
                exifOrientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));

                if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    rotation = 90;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    rotation = 180;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    rotation = 270;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Bitmap resizedBitmap;
        if (mResize) {
            if (mKeepAspectRatio) {
                double scaleFactor = getScaleFactor(photo.getWidth(), photo.getHeight());
                resizedBitmap = Bitmap.createScaledBitmap(photo, (int)(photo.getWidth() * scaleFactor),
                        (int)(photo.getHeight() * scaleFactor), true);
            } else {
                resizedBitmap = Bitmap.createScaledBitmap(photo, mTargetWidth,
                        mTargetHeight, true);
            }

        } else {
            resizedBitmap = photo;
        }

        Bitmap rotatedBitmap;
        if (mCorrectOrientation) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            rotatedBitmap = Bitmap.createBitmap(resizedBitmap , 0, 0,
                    resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
        } else {
            rotatedBitmap = resizedBitmap;
        }

        mCache = rotatedBitmap;
        return rotatedBitmap;
    }

    /**
     * Gets scale factor to multiply this photo's dimensions so that it fits in the specified target width and height
     * without loss of aspect ratio.
     *
     * @param originalWidth
     * @param originalHeight
     * @return
     */
    private double getScaleFactor (int originalWidth, int originalHeight) {
        double factor1 = (double) originalWidth / mTargetWidth;
        double factor2 = (double) originalHeight / mTargetHeight;
        return 1 / Math.max(factor1, factor2);
    }

    /**
     * Builder designed to configure an {@link ImageTransform}. It comes with reasonable defaults and may be
     * configured according to the needs of the developer.
     *
     * @author Jose Benedetto
     */
    public static class Builder {
        private File mImageFile;
        private boolean mCorrectOrientation;
        private boolean mResize;
        private int mTargetHeight;
        private int mTargetWidth;
        private int mCompression;
        private boolean mKeepAspectRatio;
        private Bitmap.CompressFormat mFormat;

        public Builder(File imageFile) {
            mImageFile = imageFile;
            mCorrectOrientation = false;
            mResize = false;
            mCompression = 100;
            mKeepAspectRatio = false;
            mFormat = Bitmap.CompressFormat.JPEG;
        }

        public Builder correctOrientation() {
            mCorrectOrientation = true;
            return this;
        }

        public Builder resize(int targetWidth, int targetHeight) {
            if (targetHeight > 0 && targetWidth > 0) {
                mResize = true;
                mTargetWidth = targetWidth;
                mTargetHeight = targetHeight;
            }
            return this;
        }

        public Builder setCompression(int compression) {
            if (compression >= 0 && compression <= 100) {
                mCompression = compression;
            }
            return this;
        }

        public Builder keepAspectRatio() {
            mKeepAspectRatio = true;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            mFormat = format;
            return this;
        }

        public ImageTransform build() {
            return new ImageTransform(mImageFile, mCorrectOrientation, mResize, mTargetWidth, mTargetHeight,
                    mCompression, mKeepAspectRatio, mFormat);
        }
    }

}
