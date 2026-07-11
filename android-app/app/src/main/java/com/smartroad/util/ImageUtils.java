package com.smartroad.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

/** Shared image-compression helper used before any photo upload (hazard reports, profile pictures). */
public final class ImageUtils {

    private static final int MAX_DIMENSION = 1280;
    private static final int JPEG_QUALITY = 80;

    private ImageUtils() { }

    /** Downscales/re-encodes a photo as JPEG before upload; falls back to the original file if compression fails. */
    public static File compressForUpload(File original) {
        if (original == null || !original.exists()) return null;
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(original.getAbsolutePath(), bounds);
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return original;

            int sample = 1;
            while ((bounds.outWidth / sample) > MAX_DIMENSION
                    || (bounds.outHeight / sample) > MAX_DIMENSION) {
                sample *= 2;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bitmap = BitmapFactory.decodeFile(original.getAbsolutePath(), opts);
            if (bitmap == null) return original;

            File compressed = new File(original.getParentFile(), "upload_" + original.getName());
            try (FileOutputStream out = new FileOutputStream(compressed)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            }
            bitmap.recycle();
            return compressed;
        } catch (Exception e) {
            return original;
        }
    }
}
