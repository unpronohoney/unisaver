package com.unisaver.unisaver;

import java.util.Collection;
import java.util.HashMap;

public class Ders {
	private int credit;
	private String harfNotu;
	public static final HashMap<String , Double> harfNotlari = new HashMap<>();
	private String isim = null;
	private String yeniHarf = null;
	private int dersNo = -1;

	public Ders(int no, int credit, String harfNotu, String isim, String yeniHarf) {
		this.dersNo = no;
		this.credit=credit;
		this.harfNotu=harfNotu;
		this.isim = isim;
		this.yeniHarf = yeniHarf;
		fillMap();
	}

	public Ders(int credit, String harfNotu, String isim) {
		this.credit=credit;
		this.harfNotu=harfNotu;
		this.isim = isim;
		fillMap();
	}
	public Ders(int credit, String harfNotu) {
		this.credit=credit;
		this.harfNotu=harfNotu;
		fillMap();
	}

	public static String getMaxHarf() {
		double point = 0.0;
		String maxHarf = "";
		for (String s : harfNotlari.keySet()) {
			if (harfNotlari.get(s) > point) {
				point = harfNotlari.get(s);
				maxHarf = s;
			}
		}
		return maxHarf;
	}

	public static String getMinHarf() {
		double point = 10.0;
		String minHarf = "";
		for (String s : harfNotlari.keySet()) {
			if (harfNotlari.get(s) < point) {
				minHarf = s;
				point = harfNotlari.get(s);
			}
		}
		return minHarf;
	}

	private void fillMap() {
		String[] harfler = {"AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};
		Double[] puanlar = {4.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 0.5, 0.0};
		for(int i = 0; i<9;i++) {
			harfNotlari.put(harfler[i], puanlar[i]);
		}
	}

	public static double getPoint(String harfNot) {
		if (harfNot.equals("Yok")) {
			return -1;
		}
		for (String harf : harfNotlari.keySet()) {
			if (harf.equals(harfNot)) {
				return harfNotlari.get(harf);
			}
		}
		return 5;
	}

	public String getLetter(double point, double increaseAmount) {
		if (point == -1) {
			return "Yok";
		}
		for (String harf : harfNotlari.keySet()) {
			if (harfNotlari.get(harf) == point + increaseAmount) {
				return harf;
			}
		}
		return "";
	}

	public static String getLetterStatic(double point) {
		if (point == -1) {
			return "Yok";
		}
		for (String harf : harfNotlari.keySet()) {
			if (harfNotlari.get(harf) == point) {
				return harf;
			}
		}
		return "";
	}

	public void setCredit(int kredi) {
		this.credit = kredi;
	}
	public void setYeniHarf(String harf) {
		this.yeniHarf = harf;
	}

	public void setDersNo(int a) {
		this.dersNo = a;
	}

	public String getYeniHarf() {
		return yeniHarf;
	}

	public int getDersNo() {
		return dersNo;
	}

	public String getIsim() {
		return isim;
	}

	public int getCredit() {
		return credit;
	}

	public String getHarfNotu() {
		return harfNotu;
	}

	public HashMap<String , Double> getHarfNotlarıMapi() {
		return harfNotlari;
	}

	public void setHarfNotu(String harf) {
		this.harfNotu = harf;
	}

	@Override
	public String toString() {
		if (isim != null) {
			return "İsmi <font color='#000000'><u>" + isim + "</u></font> olan ders";
		}
		return "Kredisi "+credit+" ve notu "+harfNotu+" olan ders";
	}

}
