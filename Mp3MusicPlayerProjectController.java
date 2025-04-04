package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class Mp3MusicPlayerProjectController implements Initializable {

	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playButton, resetButton, pauseButton, previousButton, nextButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;

	private Media media;
	private MediaPlayer mediaPlayer;

	private File directory;
	private File[] files;

	private ArrayList<File> songs;

	private int songNumber;
	private int[] speeds = { 25, 50, 75, 100, 125, 150, 175, 200 };

//	private Timer timer;
//	private TimerTask task;
//	private boolean running;
	
	private AnimationTimer progressTimer;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		songs = new ArrayList<File>();

		directory = new File("music");

		files = directory.listFiles();

		if (files != null) {
			for (File file : files) {

				songs.add(file);
				// System.out.println(file);
			}
		}

		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);

		songLabel.setText(songs.get(songNumber).getName());

		for (int i = 0; i < speeds.length; i++) {

			speedBox.getItems().add(Integer.toString(speeds[i]) + "%");// hien thi toc do trong o Speed
			// speedBox.getItems().add(Integer.toString(speeds[i]));
		}

		speedBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {

				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
			}
			
		});
		
		songProgressBar.setStyle("-fx-accent: #00FF00");
			

	}

	public void playMedia() {

		beginTimer();
		changeSpeed(null); // giu lai toc do phat cua bai hat truoc do, phat o bai hat nay
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01); //giu volume hien tai voi bai moi
		mediaPlayer.play();
	}

	public void pauseMedia() {

		cancelTimer();
		mediaPlayer.pause();

	}

	public void resetMedia() {

		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}

	public void previousMedia() {

		if (songNumber > 0) {

			songNumber--;

			mediaPlayer.stop(); // dung bai nhac dang phat truoc do
			
//			if(running) {
//				cancelTimer();
//			}

			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNumber).getName());

			mediaPlayer.play(); // chay bai nhac ngay khi bam next
		} else {

			songNumber = songs.size() - 1;

			mediaPlayer.stop();
			
//			if(running) {
//				cancelTimer();
//			}

			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNumber).getName());

			mediaPlayer.play();
		}
	}

	public void nextMedia() {

		if (songNumber < songs.size() - 1) {

			songNumber++;
			cancelTimer();
			mediaPlayer.stop(); // dung bai nhac dang phat truoc do
			
//			if(running) {
//				cancelTimer();
//			}

			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNumber).getName());

			playMedia(); // chay bai nhac ngay khi bam next
		} else {

			songNumber = 0;

			mediaPlayer.stop();
			
//			if(running) {
//				cancelTimer();
//			}

			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNumber).getName());

			playMedia();
		}
	}

	public void changeSpeed(ActionEvent event) {

		//neu chua thay doi toc do va play luon thi mac dinh toc do la 100%
		if (speedBox.getValue() == null) {

			mediaPlayer.setRate(1);
		} else {
			// mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01); //khong
			// chay duoc neu de +"%" khi hien chi so

			// chi nhan gia tri cua speedBox khong nhan gia tri "%"
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}

//	public void beginTimer() {
//
//		timer = new Timer();
//		
//		task = new TimerTask() {
//
//			@Override
//			public void run() {
//
//				running = true;
//				double current = mediaPlayer.getCurrentTime().toSeconds();
//				double end = media.getDuration().toSeconds();
//				//System.out.println(current/end);
//				songProgressBar.setProgress(current/end);	
//				
//				if(current/end == 1){
//					
//					cancelTimer();
//				}
//			}	
//		};
//		
//		timer.scheduleAtFixedRate(task, 0, 1000);
//		
//	}
//
//	public void cancelTimer() {
//
//		running = false;
//		timer.cancel();
//	}
//
//}
	
	public void beginTimer() {
	    progressTimer = new AnimationTimer() {
	        @Override
	        public void handle(long now) {
	            if (mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
	                double current = mediaPlayer.getCurrentTime().toSeconds();
	                double end = media.getDuration().toSeconds();
	                songProgressBar.setProgress(current / end);
	                
	                if (current / end >= 1) {  // >= để đảm bảo nhận biết khi kết thúc
	                    progressTimer.stop();
	                }
	            }
	        }
	    };
	    progressTimer.start();
	}

	public void cancelTimer() {
	    if (progressTimer != null) {
	        progressTimer.stop();
	    }
	}
}
