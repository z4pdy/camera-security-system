package com.z4pdy.camerasecuritysystem.camera;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}
