package jp.morishi.lifegame.wallpaper;


public class Pattern {
	private String name; 
	private int byteWidth; 
	private int  byteHeight; 
	private byte[] bytedata;
	public Pattern() {
		this.name = "";
		this.byteWidth = 0;
		this.byteHeight = 0;
		this.bytedata = null;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setByteWidth(int byteWidth) {
		this.byteWidth = byteWidth;
	}
	public int getByteWidth() {
		return byteWidth;
	}
	public void setByteHeight(int byteHeight) {
		this.byteHeight = byteHeight;
	}
	public int getByteHeight() {
		return byteHeight;
	}
	
	public void setBytedataByString(String text) {
		String vals = text.replace("[", "");
		vals = vals.replace("]", "");
		vals = vals.replace("0x", "");
		String slt[] = vals.split(":");
		if(slt != null) {
			this.bytedata = new byte[slt.length];
			for(int i = 0; i < slt.length; i++) {
				this.bytedata[i] = (byte)(Integer.parseInt(slt[i], 16));
			}
		}
	}
	public byte[] getBytedata() {
		return bytedata;
	}
	
}
