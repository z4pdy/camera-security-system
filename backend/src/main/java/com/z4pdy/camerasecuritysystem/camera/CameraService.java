package com.z4pdy.camerasecuritysystem.camera;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import nu.pattern.OpenCV;

@Service
public class CameraService {
    private CameraDevice camera;

    static {
        OpenCV.loadLocally();
    }

    @PostConstruct
    public void initCamera() {
        VideoCapture source = new VideoCapture("/dev/video0", org.opencv.videoio.Videoio.CAP_V4L2);
        source.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('M', 'J', 'P', 'G'));
        source.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920);
        source.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080);
        source.set(Videoio.CAP_PROP_FPS, 30);

        camera = new CameraDevice(source);
        camera.startCapturing();
    }

    public String getCameraDetails() {
        return camera.getDetails();
    }

    @PreDestroy
    public void releaseCamera() {
        camera.release();
    }

	public byte[] getFrame() {
        return camera.getFrame();
	}

}
