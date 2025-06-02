package com.unisaver.unisaver;

import java.util.Random;

public class Ihtimal {
	private GenelNot agno;
	private Ders ders;
	private String yuksekNot;
	private double etkiDuzeyi;
	private Random r = new Random();
	private double yeniAgno;
	private Ders yeniDers;

	public Ihtimal(GenelNot agno, Ders ders) {
		this.agno = agno;
		this.ders = ders;
		this.yuksekNot = "";
		this.etkiDuzeyi = 0;
		this.yeniAgno = 0.0;
	}

	public void newRandomize(String minHarf, String maxHarf, String istekHarf) {
		String dersNotu = ders.getHarfNotu();
		boolean tekrarliMi = true;
		if (ders.getHarfNotu().equals("Yok")) {
			dersNotu = Ders.getMinHarf();
			tekrarliMi = false;
		}
		if (!istekHarf.equals("")) {
			this.yuksekNot = istekHarf;
		} else {
			double harfSec = (r.nextInt((int) ((Ders.getPoint(maxHarf) - Ders.getPoint(minHarf)) / 0.5) + 1) * 0.5) + Ders.getPoint(minHarf);
			this.yuksekNot = Ders.getLetterStatic(harfSec);
		}
		int kredi = tekrarliMi ? ders.getCredit() : 0 ;
		double point = (Ders.getPoint(yuksekNot) - Ders.getPoint(dersNotu)) * ders.getCredit();
		this.yeniAgno = ((agno.getCurrentAGNO() * agno.getKrediSayisi()) + point) / agno.getKrediSayisi();
		this.etkiDuzeyi = yeniAgno - agno.getCurrentAGNO();
	}

	public void randomize(String harf, String minHarf, String maxHarf) {
		if (harf.equals("") && !minHarf.equals("") && !maxHarf.equals("")) {
			double minPuan;
			double maxPuan;
			if (minHarf.equals("Limitsiz")) {
				minPuan = Ders.getPoint(ders.getHarfNotu());
			} else {
				minPuan = Ders.getPoint(minHarf);
				minPuan = minPuan - 0.5;
			}
			if (maxHarf.equals("Limitsiz")) {
				maxPuan = Ders.getPoint("AA");
			} else {
				maxPuan = Ders.getPoint(maxHarf);
			}
			double harfSecimi = (r.nextInt((int) ((maxPuan-minPuan)/0.5)) * 0.5) + 0.5 + minPuan;
			String yeniHarf = ders.getLetter(harfSecimi, 0.0);
			if (Ders.getPoint(yeniHarf) < Ders.getPoint(ders.getHarfNotu())) {
				yuksekNot = ders.getHarfNotu();
			} else {
				yuksekNot = yeniHarf;
			}
			yeniAgno = agno.puanGuncelleIhtimal(ders, yuksekNot);
			etkiDuzeyi = yeniAgno - agno.getCurrentAGNO();
		} else if (harf.equals("") && minHarf.equals("") && maxHarf.equals("")) {
			yuksekNot = "";
		} else if (!harf.equals("") && minHarf.equals("") && !maxHarf.equals("")) {
			String abc;
			if (maxHarf.equals("Limitsiz")) {
				abc = "AA";
			} else {
				abc = maxHarf;
			}
			if (Ders.getPoint(harf)> Ders.getPoint(abc)) {
				yuksekNot = "";
			} else {
				yuksekNot = harf;
				yeniAgno = agno.puanGuncelleIhtimal(ders, harf);
				etkiDuzeyi = yeniAgno - agno.getCurrentAGNO();
			}
		}
		else {
			yeniAgno = agno.puanGuncelleIhtimal(ders, harf);
			if (Ders.getPoint(harf)< Ders.getPoint(ders.getHarfNotu())) {
				yuksekNot = ders.getLetter(Ders.getPoint(ders.getHarfNotu()), 0.5);
			} else {
				yuksekNot = harf;
			}
			etkiDuzeyi = yeniAgno - agno.getCurrentAGNO();
		}
	}

	public GenelNot getAgno() {
		return agno;
	}

	public Ders getDers() {
		return ders;
	}

	public String getYuksekNot() {
		return yuksekNot;
	}

	public double getEtkiDuzeyi() {
		return etkiDuzeyi;
	}


	public Double getYeniAgno() {
		return yeniAgno;
	}

	@Override
	public String toString() {
		return "<br>" + ders.toString()+" için "+yuksekNot+ " alındı. Bu durumun agnoya etkisi: "+String.format("%.2f", etkiDuzeyi)+ " oldu.";
	}
}
