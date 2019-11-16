package face_detection_recognition;

import org.opencv.core.Core;


public class Main_pak {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoShow vs = new VideoShow();
		vs.Start_GUI();

	}

}
