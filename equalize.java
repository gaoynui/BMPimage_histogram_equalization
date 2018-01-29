//by Gao Yang
//only bmp, gray
package equalize;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;

import java.io.*;

public class equalize {
	
	static int[][] pixl = new int[400][400];//存放原图像素的矩阵，大小以灰度图大小为准
	static int[][] newp = new int[400][400];//均衡后存放像素的矩阵
	static int width;
	static int height;
	static int [] hist = new int[256];//存放原图每个像素个数，下标表示像素值
	static int [] newhist = new int [256];//存放均衡后图像每个像素个数，下标表示像素值
	
	public static void getpixel(String filename) throws IOException {//获取图像像素信息
		File file = new File(filename); 
		BufferedImage bi = null;
		try{ 
			bi = ImageIO.read(file); 
		}
		catch(Exception e){ 
			e.printStackTrace(); 
		}

		width = bi.getWidth(); 
		height = bi.getHeight(); 
		System.out.println("width=" + width + ",height=" + height + "."); 
		
		for(int i = 0; i < width; i++){//将获取的像素值转化为矩阵形式
			for(int j = 0; j < height; j++){ 
				WritableRaster raster = bi.getRaster();
				pixl[i][j] = raster.getSample(i, j, 0);
				//System.out.print(pixl[i][j] + " ");
			}
			//System.out.println();
		}
	}
	
	public static void equalize_hist(String filename) throws IOException {
		getpixel(filename);
		gethist(hist, pixl);
		int[] equalize_hist = new int[256];
		int add = 0;
		for (int index = 0; index < 256; index++){
			add += hist[index];
			equalize_hist[index] = add;
		}
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				newp[i][j] = (equalize_hist[pixl[i][j]] * 255) / (width * height);//直方图均衡算法
			}
		}
		
		BufferedImage big = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				big.setRGB(i, j, newp[i][j] * 65793);
			}
		}				
		File outputfile = new File("new.bmp");//输出均衡图
		ImageIO.write(big, "bmp", outputfile);
		
		gethist(newhist, newp);
		//输出均衡后直方图
		BufferedImage newhis = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
		newhis = paintHist(newhist);
		ImageIO.write(newhis, "png", new File("C:/Users/dell/Desktop/eclipse/equalize/newhist.png"));
	}
	
	public static void show_origin_hist(String filename) throws IOException {
		//将原图的像素出现次数按像素大小组成一个数组hist
		getpixel(filename);
		gethist(hist, pixl);
		//输出原图像素直方图
		BufferedImage newhist = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
		newhist = paintHist(hist);
		ImageIO.write(newhist, "png", new File("C:/Users/dell/Desktop/eclipse/equalize/hist.png"));
	}
	
	public static int count(int a, int [][] ar) {
		//计算一个像素出现的总次数，a为一个像素值，ar为它所在的像素矩阵
		int sum = 0;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(ar[i][j] == a) {
					sum ++;
				}
			}
		}
		return sum;
	}

	public static void gethist(int [] a, int [][] b) {
		//在b中遍历，将count结果赋给数组a，a的下标为图像灰度值
		for (int index = 0; index < 256; index++){
			a[index] = count(index, b);
		}
	}
	
	public static BufferedImage paintHist(int []a) {//生成直方图函数
		int size = 300;
		BufferedImage pic = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);//定义类型
		Graphics2D g = pic.createGraphics();
		g.setPaint(Color.black);//背景色
		g.fillRect(0, 0, size, size);
		g.setPaint(Color.pink);//坐标颜色
		g.drawLine(5, 250, 265, 250);
		g.drawLine(5, 250, 5, 5);
		
		g.setPaint(Color.blue);//直方图颜色
		int max = findmax(a);
		float rate = 200.0f/((float)max);//算每个像素占比
		int offset = 2;
		for(int i = 0; i < a.length; i++) {
			int frequency = (int)(a[i] * rate);
			g.drawLine(5 + offset + i,  250,  5 + offset + i,  250 - frequency);
		}
		
		g.setPaint(Color.red);
		g.drawString("直方图", 100, 270);
		return pic;
	}
	
	public static int findmax(int [] a) {
		int max = 0;
		for(int i = 0; i < a.length; i++) {
			if(a[i] >= max) max = a[i];
		}
		return max;
	}
	
	public static void main(String[] args) throws IOException {
		equalize_hist("test.bmp");
		//show_origin_hist("test.bmp");
	}
}
