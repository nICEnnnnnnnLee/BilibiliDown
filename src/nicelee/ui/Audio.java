package nicelee.ui;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {

	static byte[] data;
	static AudioFormat format;
	static int length;

	static long lastPlayTime;

	public static void init() {
		try {
			// 98KB 比较小可以放进内存常驻
			AudioInputStream in = AudioSystem.getAudioInputStream(Audio.class.getResource("/resources/nice.wav"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*4];
			int len = in.read(buffer);
			while(len > -1) {
				out.write(buffer, 0, len);
				len = in.read(buffer);
			}
			format = in.getFormat();
			data = out.toByteArray();
			length = data.length;
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void play() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastPlayTime > 3000) {
			lastPlayTime = currentTime;
			try {
				Clip clip = AudioSystem.getClip();
				clip.open(format, data, 0, length);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
