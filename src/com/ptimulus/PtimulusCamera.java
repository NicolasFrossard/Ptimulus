package com.ptimulus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ptimulus.utils.DateFactory;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PtimulusCamera {

	private static final String PTIMULUS_DIR = Environment.getExternalStorageDirectory().toString() + "/ptimulus";
	
	public static void takePicture(final Context ctx) {

		Toast.makeText(ctx, "Taking picture...", Toast.LENGTH_LONG).show();

		Camera mCamera = Camera.open();
		
		Camera.Parameters params = mCamera.getParameters();
        params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Parameters.SCENE_MODE_AUTO);
        params.setExposureCompensation(params.getMaxExposureCompensation());

        // Jpg quality set to 90% to divide by 2 the image size.
        // The difference in quality is not noticeable, and even the devs of the JPEG format said that 100% was useless.
        params.setJpegQuality(90);
        
        mCamera.setParameters(params);

        
		mCamera.takePicture(null, null, new Camera.PictureCallback() {
			
			public void onPictureTaken(byte[] data, Camera camera) {
            try {
                FileOutputStream outStream;
                String path = getPtimulusDir().getPath() + "/" + generateFileName();
                outStream = new FileOutputStream(path);
                outStream.write(data);
                outStream.flush();
                outStream.close();

                releaseCamera(camera);

                Toast.makeText(ctx, "Picture taken " + path, Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
			}
		});
	}
	
	private static File getPtimulusDir() {

		File ptimulusDir = new File(PTIMULUS_DIR);
		
		if(!ptimulusDir.exists()) {
			ptimulusDir.mkdir();						
		}
	
		return ptimulusDir;
	}

	private static String generateFileName() {
		return "pic_" + DateFactory.nowForPhotoFilename() + ".jpg";
	}

	private static void releaseCamera(Camera camera) {
	    if (camera != null) {
	    	camera.release();
	    }
	}
}
