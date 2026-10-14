package com.z4pdy.camerasecuritysystem.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraDevice {
    private VideoCapture camera;

    public CameraDevice(VideoCapture videoCapture) {
        camera = videoCapture;
    }

    private boolean isActive() {
        return (camera != null && camera.isOpened());
    }

    public String getDetails() {
        if (isActive()) {
            int width = (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
            int height = (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            double fps = camera.get(Videoio.CAP_PROP_FPS);
            return "Camera status: ACTIVE | Resolution: " + width + "x" + height + " | Fps: " + fps;
        }
        return "Camera status: INACTIVE";
    }

    public byte[] getFrame() {
        if (!isActive()) {
            return null;
        }

        Mat frame = new Mat();
        MatOfByte encodedFrame = new MatOfByte();
        try {
            if (!camera.read(frame) || frame.empty()) {
                return null;
            }

            if (!Imgcodecs.imencode(".jpg", frame, encodedFrame)) {
                return null;
            }

            return encodedFrame.toArray();
        }
        finally {
            encodedFrame.release();
            frame.release();
        }
    }

    public void release() {
        if (isActive()) {
            camera.release();
            System.out.println("Camera resources released.");
        }
    }
}
