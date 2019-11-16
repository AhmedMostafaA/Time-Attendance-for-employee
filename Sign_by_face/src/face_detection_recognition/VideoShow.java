/*  Made by Ahmed Mostafa 
 *  1) GUI => start and stop camera 
 *  2) Detect faces by HAAR_like features   
 *  3) recognize face if exist in data set by Eignfaces
 * */
package face_detection_recognition;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import eignFace.EigenFaceCreator;

import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.Button;

public class VideoShow extends JFrame {
	
	String dir = "dataSet\";
	String demo = "demo\demo.jpg";
	EigenFaceCreator creator = new EigenFaceCreator();
	
	private FeatureDetector mFeatureDectector;
    private DescriptorExtractor mDescExtractor;
    private DescriptorMatcher mDescMatcher;
	private DaemonThread myThread = null;
	int count = 0 , count1 = 0;
    VideoCapture webSource = null;

    Mat frame = new Mat();
    MatOfByte mem = new MatOfByte();
    Size sz = new Size(320,240);
	private JPanel contentPane;

	class DaemonThread implements Runnable
    {
    protected volatile boolean runnable = false;

    @Override
    public  void run()
    {
        synchronized(this)
        {
            while(runnable)
            {
            	
                if(webSource.grab())
                {
		    	try
                        {
		    				
		    				//HAAR Feature Declaration 
		    				CascadeClassifier faceDetector = new CascadeClassifier(); 
				            faceDetector.load("opencv 3_4_1\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml"); 
				            if ( faceDetector.empty() ) {
				            	String resource = getClass().getResource("haarcascade_frontalface_alt.xml").getPath();
				                // Discard leading / if present.
				                if ( resource.startsWith("/")) {
				                    resource = resource.substring(1);
				                }
				                faceDetector.load(resource);
				            }
				            if(faceDetector.empty())
				            {
				            	System.out.println("Empty");
				            }
				            Imgcodecs imageCodecs = new Imgcodecs();

		    				//get each frame 
                            webSource.retrieve(frame);
                            
                         // Detecting faces 
                            MatOfRect faceDetections = new MatOfRect(); 
                            
                            faceDetector.detectMultiScale(frame, faceDetections);
                            
                            for (Rect rect : faceDetections.toArray()) 
                            { 
                            	// crop face from image
                            	Rect rectCrop = new Rect(rect.x ,rect.y ,rect.width , rect.height);
                            	Mat image_roi = new Mat(frame,rectCrop);
                            	
                            	// draw rectangle around face
                                Imgproc.rectangle(frame, new Point(rect.x, rect.y), 
                                 new Point(rect.x + rect.width, rect.y + rect.height), 
                                                               new Scalar(255, 255, 0));
                                
                                
                               
                                //save image that croped
                               
                               count1++;
                               Mat newIamge = new Mat();
                               Imgproc.resize(image_roi, newIamge, sz,0, 0, Imgproc.INTER_AREA);
                               Imgcodecs.imwrite(demo, newIamge);
                               
                               System.out.println("Comparing  ...");
                               String result = creator.checkAgainst(demo);

                               System.out.println("Most closly reseambling: "+result+" with "+creator.DISTANCE+" distance.");

                                
                            }
                            
                            
                            // show frames
                            Imgcodecs.imencode(".bmp", frame, mem);
                            
						    Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
						    
						    BufferedImage buff = (BufferedImage) im;
						    Graphics g=contentPane.getGraphics();
			
						    if (g.drawImage(buff, 0, 0, getWidth(), getHeight() -150 , 0, 0, buff.getWidth(), buff.getHeight(), null))
						    
						    if(runnable == false)
			                            {
						    	System.out.println("Going to wait()");
						    	this.wait();
						    }
                        }
			 catch(Exception ex)
                         {
			    System.out.println("Error");
                         }
                }
                else
                {
                	System.out.println("system failed");
                }
            }
        }
     }
   }

	public void Start_GUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoShow frame = new VideoShow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VideoShow() {
		
		try {
			System.out.println("Constructing face-spaces from "+dir+" ...");
			creator.readFaceBundles(dir);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 616, 444);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 580, 296);
		contentPane.add(panel);
		panel.setLayout(null);
		
		Button button = new Button("Start");  //start camera
		Button button_1 = new Button("Stop"); // stop camera
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myThread.runnable = false;
	            button_1.setEnabled(false);   
	            button.setEnabled(true);
	            
	            webSource.release();
			}
		});
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				webSource =new VideoCapture(0);
				
				myThread = new DaemonThread();
	            Thread t = new Thread(myThread);
	            t.setDaemon(true);
	            System.out.println("start");
	            myThread.runnable = true;
	            t.start();
				button.setEnabled(false);  //start button
	            button_1.setEnabled(true);  // stop button
			}
		});
		button.setBounds(178, 340, 70, 22);
		contentPane.add(button);
		
		
		button_1.setBounds(330, 340, 70, 22);
		contentPane.add(button_1);
	}
	
	
	public static BufferedImage Mat2BufferedImage(Mat m) // convert Mat images to Buffered images
    {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1)
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;
    }

}
