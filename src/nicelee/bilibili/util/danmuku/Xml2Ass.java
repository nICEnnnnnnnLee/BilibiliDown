package nicelee.bilibili.util.danmuku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xml2Ass {

	int width = 1920;
	int height = 1080;
	
	public Xml2Ass(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Xml2Ass() {
		this(1920, 1080);
	}
	
	public static void main(String[] args) {
		try {
			Xml2Ass xml2AssTool = new Xml2Ass(1080, 720);
			// 从xml读取弹幕
			List<Danmuku> danmuList = xml2AssTool.readXml("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\download\\惜缘、过去\\【丞相司徒】狭路相逢勇者胜-数星星2_x264.xml");
			// 写入ass文件
			xml2AssTool.writeAss(danmuList, "D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\download\\惜缘、过去\\【丞相司徒】狭路相逢勇者胜-pn1-p1-数星星2_x264-80.ass");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Danmuku> readXml(String xmlFile) throws IOException{
		File file = new File(xmlFile);
		return readXml(file);
	}
	
	public List<Danmuku> readXml(File xmlFile) throws IOException{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "utf-8"));
		List<Danmuku> danmuList = new ArrayList<Danmuku>();
		Pattern danmukuPattern = Pattern.compile("<d p=\"([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*)(?:,[0-9]*)?\">(.*?)</d>");
		String line = reader.readLine();
		while(line != null) {
			Matcher m = danmukuPattern.matcher(line);
			while(m.find()) {
//				Logger.println(m.group());
				double time = Double.parseDouble(m.group(1));
				int type = Integer.parseInt(m.group(2));
				if(type >= 1 & type <=3)
					type = 0;
				else if(type <= 8)
					type -= 3;
				else 
					continue;
				
				int fontSizeType = Integer.parseInt(m.group(3));
				if(fontSizeType <= 12)
					fontSizeType = 0;
				else if(fontSizeType <= 16)
					fontSizeType = 1;
				else if(fontSizeType <= 18)
					fontSizeType = 2;
				else if(fontSizeType <= 25)
					fontSizeType = 3;
				else if(fontSizeType <= 36)
					fontSizeType = 4;
				else if(fontSizeType <= 45)
					fontSizeType = 5;
				else if(fontSizeType <= 64)
					fontSizeType = 6;
				else 
					continue;
				
				int fontColor = Integer.parseInt(m.group(4));
				Danmuku danmuku = new Danmuku(time, type, fontSizeType, fontColor, m.group(9));
				danmuList.add(danmuku);
			}
			line = reader.readLine();
		}
		reader.close();
		danmuList.sort(new Comparator<Danmuku>() {
			@Override
			public int compare(Danmuku o1, Danmuku o2) {
				Double d1 = o1.getTime();
				Double d2 = o2.getTime();
				return d1.compareTo(d2);
			}
		});
		return danmuList;
	}

	public void writeAss(List<Danmuku> danmuList, String assFile) throws IOException {
		File file = new File(assFile);
		writeAss(danmuList, file);
	}
	
	public void writeAss(List<Danmuku> danmuList, File assFile) throws IOException {
		FileOutputStream out = new FileOutputStream(assFile);
		out.write(0xef);
		out.write(0xbb);
		out.write(0xbf);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
		
		// 先写头
		writer.write("[Script Info]\r\n");
		writer.write("Title: Bilibili弹幕转ASS字幕\r\n");
		writer.write("Original Script: 由 https://github.com/nICEnnnnnnnLee 制作\r\n");
		writer.write("ScriptType: v4.00+\r\n");
		writer.write("Collisions: Normal\r\n");
		writer.write(String.format("PlayResX: %d\r\n", width));
		writer.write(String.format("PlayResY: %d\r\n", height));
		writer.write(String.format("Aspect Ratio: %d:%d\r\n", width, height));
		writer.write("YCbCr Matrix: TV.601\r\n");
		writer.write("[V4+ Styles]\r\n");
		writer.write("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\r\n");
		for(int i=0; i<= 6; i++) {
			String style = String.format("Style: %s,Microsoft YaHei UI,%d,&H66FFFFFF,&H66FFFFFF,&H66000000,&H66000000,0,0,0,0,100,100,0,0,1,1,1,2,20,20,2,0\r\n", 
					Danmuku.getFontTypeStr(i), Danmuku.getFontSize(i));
			writer.write(style);
		}
		writer.write("[Events]\r\n");
		writer.write("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\r\n");
		
		// 逐行写入弹幕
		int normalSize = Danmuku.getFontSize(3);
		int maxSize = Danmuku.getFontSize(6);
		int normalStartY = 0;
		int topCenterStartY = 0;
		int bottomCenterStartY = height - maxSize;
		for(Danmuku danmu:danmuList) {
			String danmuContent = danmu.getContent();
			// 0 滚动弹幕
			if(danmu.getType() == 0) {
				String startTime = Danmuku.getFormatTime(danmu.getTime());
				String endTime = Danmuku.getFormatTime(danmu.getTime() + 10);
				String fontTypeStr = Danmuku.getFontTypeStr(danmu.getFontSizeType());
				int startX = width;
				int startY = normalStartY;
				int endX = 0 - Danmuku.getFontSize(danmu.getFontSizeType()) * danmuContent.length() * 2;
				int endY = normalStartY;
				String color = Danmuku.getColor(danmu.getFontColor());
				String line = String.format("Dialogue: 2,%s,%s,%s,,0000,0000,0000,,{\\move(%d, %d, %d, %d)\\c&H%s&}%s\r\n", 
						startTime, endTime, fontTypeStr, startX, startY, endX, endY, color, danmuContent);
				writer.write(line);
				
				normalStartY += normalSize;
				if(normalStartY >= height - maxSize)
					normalStartY = 0;
			}else if(danmu.getType() == 1) {// 1 底端弹幕
				String startTime = Danmuku.getFormatTime(danmu.getTime());
				String endTime = Danmuku.getFormatTime(danmu.getTime() + 5);
				String fontTypeStr = Danmuku.getFontTypeStr(danmu.getFontSizeType());
				int startX = width/2;
				int startY = bottomCenterStartY;
				String color = Danmuku.getColor(danmu.getFontColor());
				String line = String.format("Dialogue: 2,%s,%s,%s,,0000,0000,0000,,{\\an8\\pos(%d, %d)\\c&H%s&}%s\r\n", 
						startTime, endTime, fontTypeStr, startX, startY, color, danmuContent);
				writer.write(line);
				bottomCenterStartY -= normalSize;
				if(bottomCenterStartY <= height/2 )
					bottomCenterStartY = height - maxSize;
			}else if(danmu.getType() == 2) {// 2 顶端弹幕
				String startTime = Danmuku.getFormatTime(danmu.getTime());
				String endTime = Danmuku.getFormatTime(danmu.getTime() + 5);
				String fontTypeStr = Danmuku.getFontTypeStr(danmu.getFontSizeType());
				int startX = width/2;
				int startY = topCenterStartY;
				String color = Danmuku.getColor(danmu.getFontColor());
				String line = String.format("Dialogue: 2,%s,%s,%s,,0000,0000,0000,,{\\an8\\pos(%d, %d)\\c&H%s&}%s\r\n", 
						startTime, endTime, fontTypeStr, startX, startY, color, danmuContent);
				writer.write(line);
				topCenterStartY += normalSize;
				if(topCenterStartY >= height/2 )
					topCenterStartY = 0;
			}
		}
		writer.flush();
		writer.close();
		out.close();
	}
}
