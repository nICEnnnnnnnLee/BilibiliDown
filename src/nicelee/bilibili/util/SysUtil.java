package nicelee.bilibili.util;

public class SysUtil {
	static String OS;
	static String ARCH;
	static String EXE_SUFFIX;

	public static boolean surportFFmpegOfficially() {
		String os_arch = String.format("%s_%s", SysUtil.getOS(), SysUtil.getARCH());
		switch (os_arch) {
		case "linux_amd64":
		case "linux_arm64":
		case "win_amd64":
		case "win_arm64":
			return true;
		default:
			return false;
		}
	}

	public static String getOS() {
		if (OS == null) {
			String osName = System.getProperty("os.name").toLowerCase();
			Logger.println(osName);
			if (osName.startsWith("win"))
				OS = "win";
			else if (osName.startsWith("linux"))
				OS = "linux";
			else
				OS = "unknown";
		}
		return OS;
	}

	public static String getARCH() {
		if (ARCH == null) {
			String arch = System.getProperty("os.arch").toLowerCase();
			Logger.println(arch);
			if (arch.equals("amd64") || arch.equals("x86_64"))
				ARCH = "amd64";
			else if (arch.equals("arm64") || arch.equals("aarch64"))
				ARCH = "arm64";
			else
				ARCH = "unknown";
		}
		return ARCH;
	}

	public static String getEXE_SUFFIX() {
		if (EXE_SUFFIX == null) {
			if ("win".equals(getOS()))
				EXE_SUFFIX = ".exe";
			else
				EXE_SUFFIX = "";
		}
		return EXE_SUFFIX;
	}

}
