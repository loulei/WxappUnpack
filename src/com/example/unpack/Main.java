package com.example.unpack;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception {
		File file = new File("file/_-404367246_60.wxapkg");
		File outDir = new File("file/output/"+file.getName()+"/");
		if(!outDir.exists()) {
			outDir.mkdir();
		}
		RandomAccessFile accessFile = new RandomAccessFile(file, "r");
		
		if(accessFile.readByte() != (byte)0xBE) {
			if(accessFile != null)
				accessFile.close();
			throw new RuntimeException("illagle file format");
		}
		accessFile.seek(0xE);
		int filecount = accessFile.readInt();
		System.out.println("file count:"+filecount);
		List<AppItem> appItems = new ArrayList<Main.AppItem>();
		
		for(int i=0; i<filecount; i++) {
			int nameLen = accessFile.readInt();
			byte[] buffer = new byte[nameLen];
			accessFile.read(buffer, 0, nameLen);
			String name = new String(buffer, 0, nameLen);
			int start = accessFile.readInt();
			int length = accessFile.readInt();
			AppItem appItem = new AppItem(name, start, length);
			System.out.println(appItem.toString());
			appItems.add(appItem);
		}
		
		for(int i=0; i<filecount; i++) {
			AppItem appItem = appItems.get(i);
			File outfile = new File(outDir, appItem.name);
			if(!outfile.getParentFile().exists()) {
				outfile.getParentFile().mkdirs();
			}
			accessFile.seek(appItem.start);
			byte[] buffer = new byte[appItem.length];
			accessFile.read(buffer, 0, appItem.length);
			Files.write(Paths.get(outfile.getPath()), buffer, StandardOpenOption.CREATE);
		}
		
		accessFile.close();
		System.out.println("unpack finish");
	}
	
	
	static class AppItem{
		private String name;
		private int start;
		private int length;
		
		public AppItem() {}
		
		public AppItem(String name, int start, int length) {
			super();
			this.name = name;
			this.start = start;
			this.length = length;
		}

		@Override
		public String toString() {
			return "AppItem [name=" + name + ", start=" + start + ", length=" + length + "]";
		}
		
		
	}
}
