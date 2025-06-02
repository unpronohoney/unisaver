package com.unisaver.unisaver;

import java.util.ArrayList;

public class GenelNot{
	private int toplamKredi;
	private double currentAGNO;

	public GenelNot(int topKredi, double currentAGNO) {
		this.toplamKredi = topKredi;
		this.currentAGNO = currentAGNO;
	}

	public void yeniDers(int kredi, String letter) {
		currentAGNO *= toplamKredi;
		currentAGNO += (Ders.getPoint(letter) * kredi);
		toplamKredi += kredi;
		currentAGNO /= toplamKredi;
	}

	public void yeniKredi(int eskiKredi, String eskiHarf, String yeniHarf, int yeniKredi) {
		if (eskiHarf.equals("Yok")) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(yeniHarf) * eskiKredi);
			currentAGNO += (Ders.getPoint(yeniHarf) * yeniKredi);
			toplamKredi += yeniKredi - eskiKredi;
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO += (Ders.getPoint(eskiHarf) * eskiKredi);
			currentAGNO -= (Ders.getPoint(yeniHarf) * eskiKredi);
			currentAGNO -= (Ders.getPoint(eskiHarf) * yeniKredi);
			currentAGNO += (Ders.getPoint(yeniHarf) * yeniKredi);
			toplamKredi += yeniKredi - eskiKredi;
			currentAGNO /= toplamKredi;
		}
	}

	public void yeniEskiHarf(int kredi, String eskiHarf, String yeniEskiHarf) {
		if (eskiHarf.equals("Yok")) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(yeniEskiHarf) * kredi);
			toplamKredi -= kredi;
			currentAGNO /= toplamKredi;
		} else if (yeniEskiHarf.equals("Yok")) {
			currentAGNO *= toplamKredi;
			currentAGNO += (Ders.getPoint(eskiHarf) * kredi);
			toplamKredi += kredi;
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO += ((Ders.getPoint(eskiHarf) - Ders.getPoint(yeniEskiHarf)) * kredi);
			currentAGNO /= toplamKredi;
		}
	}

	public void dersSilme(Ders ders) {
		if (ders.getHarfNotu().equals("Yok")) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(ders.getYeniHarf()) * ders.getCredit());
			toplamKredi -= ders.getCredit();
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(ders.getYeniHarf()) * ders.getCredit());
			currentAGNO += (Ders.getPoint(ders.getHarfNotu()) * ders.getCredit());
			currentAGNO /= toplamKredi;
		}
	}

	public void yeniYeniHarf(int kredi, String yeniHarf, String yeniYeniHarf) {
		currentAGNO *= toplamKredi;
		currentAGNO += ((Ders.getPoint(yeniYeniHarf) - Ders.getPoint(yeniHarf)) * kredi);
		currentAGNO /= toplamKredi;
	}

	public void eskiDers(int kredi, String eskiHarf, String yeniHarf) {
		currentAGNO *= toplamKredi;
		currentAGNO += ((Ders.getPoint(yeniHarf) - Ders.getPoint(eskiHarf)) * kredi);
		currentAGNO /= toplamKredi;
	}

	public void setToplamKredi(int kredi) {
		double temp = currentAGNO * toplamKredi;
		this.toplamKredi = kredi;
		temp /= toplamKredi;
		this.currentAGNO = temp;
	}

	public void puanGuncelle(ArrayList<Ders> dersler) {
		for (Ders d : dersler) {
			currentAGNO *= toplamKredi;
			toplamKredi += d.getCredit();
			currentAGNO += Ders.getPoint(d.getHarfNotu()) * d.getCredit();
			currentAGNO /= toplamKredi;
		}
	}

	public void puanGuncelleTekrar(ArrayList<Ders> tekrarDersler, ArrayList<String> harfler) {
		for (int i = 0; i < harfler.size(); i++) {
			currentAGNO *= toplamKredi;
			currentAGNO += (Ders.getPoint(harfler.get(i)) - Ders.getPoint(tekrarDersler.get(i).getHarfNotu()))
					* tekrarDersler.get(i).getCredit();
			currentAGNO /= toplamKredi;
		}
	}

	public double puanGuncelleIhtimal(Ders d, String harf) {
		return ((currentAGNO * toplamKredi) - (Ders.getPoint(d.getHarfNotu()) * d.getCredit()) + Ders.getPoint(harf) * d.getCredit()) / toplamKredi;
	}

	public int getKrediSayisi() {
		return toplamKredi;
	}

	public double getCurrentAGNO() {
		return currentAGNO;
	}

}
