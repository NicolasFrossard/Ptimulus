package com.ptimulus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ptimulus.utils.DateFactory;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
//import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PtimulusCamera {

	private static final String PTIMULUS_DIR = Environment.getExternalStorageDirectory().toString() + "/ptimulus";
	
	public static void takePicture(final Context ctx) {

		Toast.makeText(ctx, "Taking picture...", Toast.LENGTH_LONG).show();
		
		Camera mCamera = Camera.open();
		//int camId = findFrontFacingCamera();
		//Camera mCamera = Camera.open(camId);
		
		Camera.Parameters params = mCamera.getParameters();
        params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Parameters.SCENE_MODE_AUTO);
        params.setExposureCompensation(params.getMaxExposureCompensation());
        params.setJpegQuality(70);
        params.setPictureSize(2048, 1536);
        
        mCamera.setParameters(params);
        
        
        
		mCamera.takePicture(null, null, new Camera.PictureCallback() {
			
			public void onPictureTaken(byte[] data, Camera camera) {
			         
				FileOutputStream outStream = null;
		        
				try {
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

	/*
	private static int findFrontFacingCamera() {  
		
		int cameraId = -1;  
        // Search for the front facing camera  
		int numberOfCameras = Camera.getNumberOfCameras();  
        
		for (int i = 0; i < numberOfCameras; i++) {  
			CameraInfo info = new CameraInfo();  
            Camera.getCameraInfo(i, info);  
            
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {  
                  Log.v("CAMERA", "Camera found");  
                  cameraId = i;  
                  break;  
            }  
		}  
        
		return cameraId;  
    }
*/
	private static void releaseCamera(Camera camera) {
	    if (camera != null) {
	    	camera.release();
	    }
	}
}
