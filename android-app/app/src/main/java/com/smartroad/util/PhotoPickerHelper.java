package com.smartroad.util;

import android.content.Context;
import android.net.Uri;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Shared camera/gallery picking logic used by both ReportFragment and
 * EditProfileActivity, so the URI-to-cache-file plumbing only lives once.
 * Must be constructed during onCreate() (or as a field initializer), before
 * the host Fragment/Activity moves past CREATED — the same rule that
 * applies to calling registerForActivityResult() directly.
 */
public class PhotoPickerHelper {

    public interface Listener {
        void onPhotoPicked(File photoFile, Uri previewUri);
    }

    private final Context appContext;
    private final ActivityResultLauncher<Uri> takePictureLauncher;
    private final ActivityResultLauncher<String> pickImageLauncher;
    private Uri cameraUri;

    public PhotoPickerHelper(ActivityResultCaller caller, Context context, Listener listener) {
        this.appContext = context.getApplicationContext();

        takePictureLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraUri != null) {
                        File file = uriToCacheFile(cameraUri, "camera_" + System.currentTimeMillis() + ".jpg");
                        listener.onPhotoPicked(file, cameraUri);
                    }
                });

        pickImageLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        File file = uriToCacheFile(uri, "gallery_" + System.currentTimeMillis() + ".jpg");
                        listener.onPhotoPicked(file, uri);
                    }
                });
    }

    public void launchCamera(Context context) {
        File file = new File(context.getCacheDir(), "capture_" + System.currentTimeMillis() + ".jpg");
        cameraUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        takePictureLauncher.launch(cameraUri);
    }

    public void launchGallery() {
        pickImageLauncher.launch("image/*");
    }

    private File uriToCacheFile(Uri uri, String name) {
        try {
            File out = new File(appContext.getCacheDir(), name);
            InputStream in = appContext.getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(out);
            byte[] buf = new byte[4096];
            int len;
            if (in != null) {
                while ((len = in.read(buf)) != -1) os.write(buf, 0, len);
                in.close();
            }
            os.close();
            return out;
        } catch (Exception e) {
            return null;
        }
    }
}
