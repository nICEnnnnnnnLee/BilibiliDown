package nicelee.bilibili.util.danmuku;

public class Danmuku {

	double time; 		// 弹幕出现的时间，以秒数为单位
	int type; 			// 弹幕类型 	0 滚动弹幕 1底端弹幕 2顶端弹幕 3 .逆向弹幕 4精准定位 5高级弹幕 
	int fontSizeType; 	// 弹幕大小类型 0非常小,1特小,2小,3中,4大,5很大,6特别大
	int fontColor;		// 弹幕颜色 字体的颜色以HTML颜色的十进制为准
	String content;		// 弹幕内容
	
	public Danmuku(double time, int type, int fontSizeType, int fontColor,String content) {
		this.time = time;
		this.type = type;
		this.fontSizeType = fontSizeType;
		this.fontColor = fontColor;
		this.content = content;
	}

	public double getTime() {
		return time;
	}

	public int getType() {
		return type;
	}

	public int getFontSizeType() {
		return fontSizeType;
	}

	public int getFontColor() {
		return fontColor;
	}

	public String getContent() {
		return content;
	}
	
	public static String getFontTypeStr(int fontSizeType) {
		switch (fontSizeType) {
		case 0:
			return "UltraSmall";
		case 1:
			return "VerySmall";
		case 2:
			return "Small";
		case 3:
			return "Normal";
		case 4:
			return "Big";
		case 5:
			return "VeryBig";
		case 6:
			return "UltraBig";
		default:
			return "Normal";
		}
	}
	
	public static int getFontSize(int fontSizeType) {
		switch (fontSizeType) {
		case 0:
			return 12 * 2;
		case 1:
			return 16 * 2;
		case 2:
			return 18 * 2;
		case 3:
			return 25 * 2 + 5;
		case 4:
			return 36 * 2;
		case 5:
			return 45 * 2;
		case 6:
			return 64 * 2;
		default:
			return 25 * 2;
		}
	}

	public static String getFormatTime(double time) {
		int second = (int) time;
		int minute = second / 60;
		int hour = minute / 60;
		double millis = time - second;
		minute = minute % 60;
		second = second % 60;
		int milliSecond = (int) (millis * 100);
		return String.format("%d:%02d:%02d.%02d", hour, minute, second, milliSecond);
	}
	
	public static String getColor(int color) {
		// TODO 颜色转换有点问题，暂时枚举常见选择
		switch (color) {
		case 16777215:
			return "FFFFFF";
		case 16646914:
			return "FE0302";
		case 16740868:
			return "FF7204";
		case 16755202:
			return "FFAA02";
		case 16765698:
			return "FFD302";
		case 16776960:
			return "FFFF00";
		case 10546688:
			return "A0EE00";
		case 52480:
			return "00CD00";
		case 4351678:
			return "4266BE";
		case 9033215:
			return "89D5FF";
		case 13369971:
			return "CC0273";
		case 10197915:
			return "9B9B9B";
		case 2236962:
			return "222222";
		default:
			int RR = (color >> 16) & 0xff;
			int GG = (color >> 8) & 0xff;
			int BB = color & 0xff;
			return String.format("%02X%02X%02X", BB, GG, RR);
		}
		
	}
}
