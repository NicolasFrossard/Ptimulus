package com.ptimulus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.util.Log;

public class PtimulusCamera {
	
	public static void takePicture() {

		int camId = findFrontFacingCamera();
		
		Camera mCamera = Camera.open(camId);
		
		mCamera.takePicture(null, null, new Camera.PictureCallback() {

			   public void onPictureTaken(byte[] data, Camera camera) {
			         
			         FileOutputStream outStream = null;
			              try {
			            	  String path = Environment.getExternalStorageDirectory().getPath() + "/Timmy.jpg";
			                  outStream = new FileOutputStream(path);
			                  outStream.write(data);
			                  outStream.flush();
			                  outStream.close();

			                  releaseCamera(camera);
			          		  
			              } catch (FileNotFoundException e){
			                  Log.d("CAMERA", e.getMessage());
			              } catch (IOException e){
			                  Log.d("CAMERA", e.getMessage());
			              }

			   }
		});
	}

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

	private static void releaseCamera(Camera camera) {
	    if (camera != null) {
	    	camera.release();
	    	camera = null;
	    }
	}
}
