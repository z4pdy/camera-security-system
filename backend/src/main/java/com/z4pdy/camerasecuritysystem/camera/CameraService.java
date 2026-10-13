package com.z4pdy.camerasecuritysystem.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import nu.pattern.OpenCV;

@Service
public class CameraService {
    private VideoCapture camera;

    static {
        OpenCV.loadLocally();
    }

    @PostConstruct
    public void initCamera() {
        // camera = new VideoCapture(1);
        camera = new VideoCapture("/dev/video0", org.opencv.videoio.Videoio.CAP_V4L2);

        if (isCameraActive()) {
            System.out.println("Camera successfully initialized via OpenCV.");
        }
        else {
            System.out.println("Error: Could not open the USB camera.");
        }
    }

    public String getCameraDetails() {
        if (isCameraActive()) {
            int width = (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
            int height = (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            return "Camera status: ACTIVE | Resolution: " + width + "x" + height;
        }
        return "Camera status: INACTIVE";
    }

    private boolean isCameraActive() {
        return (camera != null && camera.isOpened());
    }

    @PreDestroy
    public void releaseCamera() {
        if (isCameraActive()) {
            camera.release();
            System.out.println("Camera resources released.");
        }
    }

    public byte[] getFrame() {
        if (!isCameraActive()) {
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
}
