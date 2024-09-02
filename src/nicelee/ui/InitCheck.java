package nicelee.ui;

import java.io.File;
import java.nio.file.Files;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.CmdUtil;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.ResourcesUtil;
import nicelee.ui.item.JOptionPane;
import nicelee.ui.thread.DownloadRunnable;

/**
 * 初始化检查要放在配置文件读取之后
 *
 */
public class InitCheck {

	public static void main(String[] args) {
		checkFileAccess();
		Global.ffmpegPath = "ffmpeg";
		checkFFmpeg(true);
	}

	public static void checkFileAccess() {
		File f = ResourcesUtil.baseDirFile();
		if (!Files.isWritable(f.toPath())) {
			String tips = "检测到程序对于数据目录没有“写”权限，可能无法正常工作。\n"
					+ "建议设置JVM参数 -Dbilibili.prop.dataDirPath={dataDirPath} 指定有读写权限的数据目录位置。\n当前数据目录为: "
					+ f.getAbsolutePath();
			JOptionPane.showMessageDialog(null, tips);
		}
	}

	public static void checkFFmpeg(boolean isFFmpegSupported) {
		CmdUtil.DEFAULT_WORKING_DIR = ResourcesUtil.baseDirFile();
		if (!checkFFmpegConf() && !checkFFmpegSys()) {
			if (isFFmpegSupported) {
				Object[] options = { "是", "否" };
				int m = JOptionPane.showOptionDialog(null,
						"检测到当前没有ffmpeg环境, mp4及小部分flv文件将无法转码或合并.\r\n     是否下载ffmpeg(自编译, 3M左右)?", "请选择：",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				Logger.println(m);
				if (m == 0) {
					VideoInfo avInfo = new INeedAV().getVideoDetail("ffmpeg", 0, false);
					DownloadRunnable downThread = new DownloadRunnable(avInfo, avInfo.getClips().get(1234L), 0);
					Global.queryThreadPool.execute(downThread);
					return;
				}
			}

			JOptionPane.showMessageDialog(null, "当前没有ffmpeg环境，请自行下载ffmpeg，并正确配置 bilibili.ffmpegPath ", "请注意!!",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private static boolean checkFFmpegSys() {
		String ffmpeg = "ffmpeg";
		String[] cmd = new String[] { ffmpeg, "-version" };
		if (CmdUtil.run(cmd)) {
			CmdUtil.FFMPEG_PATH = ffmpeg;
			return true;
		}
		return false;
	}

	private static boolean checkFFmpegConf() {
		String ffmpeg = ResourcesUtil.resolve(Global.ffmpegPath);
		String[] cmd = new String[] { ffmpeg, "-version" };
		if (CmdUtil.run(cmd)) {
			CmdUtil.FFMPEG_PATH = ffmpeg;
			return true;
		}
		return false;
	}

}
