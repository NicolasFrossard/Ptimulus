package com.ptimulus;

import java.io.File;
import java.io.FileOutputStream;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.util.Log;

import com.ptimulus.log.LogEntryType;
import com.ptimulus.utils.DateFactory;

public class PtimulusCamera {

	private static final String PTIMULUS_DIR = Environment.getExternalStorageDirectory().toString() + "/ptimulus";
	
	private static boolean isLockedCamera = false;

    private static final Object lock = new Object();
    
	public static synchronized void takePicture(final PtimulusService ptimulusService) {

    	synchronized (lock) {				
			if(!isLockedCamera) {
		
				Camera mCamera = Camera.open();
				try {
					isLockedCamera = true;
					Log.d("CAMERA", "Camera locked");
					
					Camera.Parameters params = mCamera.getParameters();
			        params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
			        params.setSceneMode(Parameters.SCENE_MODE_AUTO);
			        params.setExposureCompensation(params.getMaxExposureCompensation());
		
			        // Jpg quality set to 90% to divide by 2 the image size.
			        // The difference in quality is not noticeable, and even the devs of the JPEG format said that 100% was useless.
			        params.setJpegQuality(90);
			        
			        mCamera.setParameters(params);
			        
			        mCamera.startPreview();
			        
					mCamera.takePicture(null, null, new Camera.PictureCallback() {
						
						public void onPictureTaken(byte[] data, Camera camera) {
		
			                try {
			    				Log.d("CAMERA", "in picture taken...");
				                FileOutputStream outStream;
				                String path = getPtimulusDir().getPath() + "/" + generateFileName();
				                outStream = new FileOutputStream(path);
				                outStream.write(data);
								outStream.flush();
				                outStream.close();
				                log(ptimulusService, "Picture taken: " + path);
							} catch (Exception e) {
								log(ptimulusService, e.getMessage());
							} finally {
			                	releaseCamera(camera);
							}
					}});
				}
				catch (Exception e){
					log(ptimulusService, e.getMessage());
		        }
				
			}
			else {
				log(ptimulusService, "Could not take picture: camera is locked");
			}
    	}
	}
	
	private static File getPtimulusDir() {

		File ptimulusDir = new File(PTIMULUS_DIR);
		
		if(!ptimulusDir.exists()) {
			ptimulusDir.mkdir();						
		}
	
		return ptimulusDir;
	}

	private static String generateFileName() {
		return "pic_" + DateFactory.nowForFilename() + ".jpg";
	}

	private static void releaseCamera(Camera mCamera) {
	    if (mCamera != null) {
	    	mCamera.release();
	    	isLockedCamera = false;
	        Log.d("CAMERA", "Camera released");
	    }
	}
	
	private static void log(PtimulusService ptimulusService, String entry) {
		ptimulusService.relayLog(LogEntryType.CAMERA, entry);
        Log.d("CAMERA", entry);
	}
}
