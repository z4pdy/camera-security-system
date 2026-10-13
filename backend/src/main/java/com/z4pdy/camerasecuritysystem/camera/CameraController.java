package com.z4pdy.camerasecuritysystem.camera;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/camera")
public class CameraController {
    private CameraService cameraService;

    public CameraController(CameraService cameraService) {
		this.cameraService = cameraService;
	}

	@GetMapping("/info")
    public String getInfo() {
        return cameraService.getCameraDetails();
    }

    @GetMapping("/live")
    public void getLiveStream(HttpServletResponse response) throws InterruptedException {
        response.setContentType("multipart/x-mixed-replace;boundary=frame");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            while (true) {
                byte[] frame = cameraService.getFrame();

                if (frame != null) {
                    outputStream.write("--frame\r\n".getBytes()); 
                    outputStream.write("Content-Type: image/jpeg\r\n".getBytes());
                    outputStream.write(("Content-Length: " + frame.length + "\r\n\r\n").getBytes());
                    outputStream.write(frame);
                    outputStream.write("\r\n\r\n".getBytes());
                    outputStream.flush();
                }

                Thread.sleep(33);
            }

        }
        catch (IOException e) {
            // e.printStackTrace();
        }
    }

}
