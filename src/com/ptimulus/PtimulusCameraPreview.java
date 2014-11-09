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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PtimulusCameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    
    PtimulusCamera mCamera;
    
    public PtimulusCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("CAMERA_PREVIEW", "surface created");
        
        mCamera = PtimulusCamera.createCamera();  
        mCamera.init(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("CAMERA_PREVIEW", "surface destroyed");
        mCamera.deInit();
        mCamera = null;
    }

}
