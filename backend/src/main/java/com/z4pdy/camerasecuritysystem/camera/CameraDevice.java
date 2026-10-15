package com.z4pdy.camerasecuritysystem.camera;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraDevice {
    private VideoCapture source;
    private Mat currentFrame = new Mat();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private long sleepTime = 1;

    public CameraDevice(VideoCapture source) {
        if (source == null || !source.isOpened()) {
            throw new IllegalArgumentException("Camera source is not opened or unavailable.");
        }
        int fps = (int) source.get(Videoio.CAP_PROP_FPS);
        if (fps <= 0) {
            throw new IllegalArgumentException("Camera FPS must be above 0");
        }
        sleepTime = 1000 / fps;
        this.source = source;
    }

    private boolean isInitialized() {
        return (source != null && source.isOpened());
    }

    public String getDetails() {
        if (isInitialized()) {
            int width = (int) source.get(Videoio.CAP_PROP_FRAME_WIDTH);
            int height = (int) source.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            double fps = source.get(Videoio.CAP_PROP_FPS);
            return "Camera status: ACTIVE | Resolution: " + width + "x" + height + " | Fps: " + fps;
        }
        return "Camera status: INACTIVE";
    }

    public void startCapturing() {
        if (!isInitialized()) {
            return;
        }

        Thread captureLoopThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Mat frame = new Mat();
                try {
                    if (!source.read(frame) || frame.empty()) {
                        continue;
                    }

                    lock.writeLock().lock();
                    try {
                        frame.copyTo(currentFrame);
                    }
                    finally {
                        lock.writeLock().unlock();
                    }

                    try {
                        Thread.sleep(sleepTime);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                finally {
                    frame.release();
                }
            }
        });
        captureLoopThread.setDaemon(true);
        captureLoopThread.start();
    }

    public byte[] getFrame() {
        lock.readLock().lock();
        Mat frame;
        try {
            frame = currentFrame.clone();
        }
        finally {
            lock.readLock().unlock();
        }

        MatOfByte encodedFrame = new MatOfByte();
        try {
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
        if (isInitialized()) {
            source.release();
            currentFrame.release();
        }
    }
}
