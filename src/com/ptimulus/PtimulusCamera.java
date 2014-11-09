/*
 * Copyright (C) 2014 Ptimulus
 * http://www.ptimulus.eu
 * 
 * This file is part of Ptimulus.
 * 
 * Ptimulus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ptimulus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Ptimulus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.ptimulus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import com.ptimulus.log.LogEntryType;
import com.ptimulus.utils.DateFactory;

public class PtimulusCamera {

    private static final String   PTIMULUS_DIR    = Environment
                                                          .getExternalStorageDirectory()
                                                          .toString()
                                                          + "/ptimulus";

    private static PtimulusCamera sPtimulusCamera = null;

    private Camera                mCamera         = null;
    private boolean               mIsInitialized  = false;
    private boolean               mIsLockedCamera = false;

    private PtimulusCamera() {
        mCamera = Camera.open();
    }

    public synchronized void init(SurfaceHolder holder) {
        Camera.Parameters params = mCamera.getParameters();
        params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Parameters.SCENE_MODE_AUTO);
        params.setExposureCompensation(params.getMaxExposureCompensation());

        // Jpg quality set to 90% to divide by 2 the image size.
        // The difference in quality is not noticeable, and even the
        // devs of the JPEG format said that 100% was useless.
        params.setJpegQuality(90);

        mCamera.setParameters(params);

        mIsInitialized = true;
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deInit() {
        try {
            mIsInitialized = false;
            mCamera.setPreviewDisplay(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        deInit();
        mCamera.release();

        Log.d("CAMERA", "Camera released");
    }

    public static synchronized PtimulusCamera createCamera() {
        if (sPtimulusCamera == null) {
            sPtimulusCamera = new PtimulusCamera();
        }

        return sPtimulusCamera;
    }

    public synchronized void takePicture(final PtimulusService ptimulusService) {
        if (!mIsLockedCamera && mIsInitialized) {
            try {
                mIsLockedCamera = true;
                Log.d("CAMERA", "Camera locked");

                mCamera.startPreview();

                mCamera.takePicture(null, null, new Camera.PictureCallback() {

                    public void onPictureTaken(byte[] data, Camera camera) {

                        try {
                            Log.d("CAMERA", "in picture taken...");
                            FileOutputStream outStream;
                            String path = getPtimulusDir().getPath() + "/"
                                    + generateFileName();
                            outStream = new FileOutputStream(path);
                            outStream.write(data);
                            outStream.flush();
                            outStream.close();
                            log(ptimulusService, "Picture taken: " + path);
                        } catch (Exception e) {
                            log(ptimulusService, e.getMessage());
                        } finally {
                            mIsLockedCamera = false;
                        }
                    }
                });
            } catch (Exception e) {
                log(ptimulusService, e.getMessage());
                mIsLockedCamera = false;
            }
        } else {
            log(ptimulusService, "Could not take picture: camera is locked");
        }
    }

    private static File getPtimulusDir() {

        File ptimulusDir = new File(PTIMULUS_DIR);

        if (!ptimulusDir.exists()) {
            ptimulusDir.mkdir();
        }

        return ptimulusDir;
    }

    private static String generateFileName() {
        return "pic_" + DateFactory.nowForFilename() + ".jpg";
    }

    private void log(PtimulusService ptimulusService, String entry) {
        ptimulusService.relayLog(LogEntryType.CAMERA, entry);
        Log.d("CAMERA", entry);
    }
}
