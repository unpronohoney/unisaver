package com.unisaver.unisaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class IhtimallerDizisi {
	private HashMap<Integer, Double[]> eskiAgnoVeSapmalar ;
	private HashMap<Integer, ArrayList<Ihtimal>> eskiIhtimaller;
	private int counter;

	public IhtimallerDizisi() {
		this.eskiAgnoVeSapmalar = new HashMap<>();
		this.eskiIhtimaller = new HashMap<>();
		this.counter = 0;
	}

	public int getCounter() {
		return counter;
	}
	public boolean ihtimalleriEkle(ArrayList<Ihtimal> ihtimaller, double agno, double minAgno, double maxAgno) {
		if (eskiAgnoVeSapmalar.isEmpty()) {
			counter++;
			for (Ihtimal ih : ihtimaller) {
				agno += ih.getEtkiDuzeyi();
				minAgno += ih.getEtkiDuzeyi();
				maxAgno += ih.getEtkiDuzeyi();
			}
			double real = Math.round(agno * 1000.0) / 1000.0;
			double min = Math.round(minAgno * 1000.0) / 1000.0;
			double max = Math.round(maxAgno * 1000.0) / 1000.0;
			real = Math.round(real * 100.0) / 100.0;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			min = real - min;
			max = max - real;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			Double[] tutacak = {agno, min, max};
			eskiAgnoVeSapmalar.put(counter, tutacak);
			eskiIhtimaller.put(counter, ihtimaller);
			return true;
		} else {
			int aynilar = 0;
			for (Integer i : eskiIhtimaller.keySet()) {
				for (int j = 0; j < ihtimaller.size(); j++) {
					if (eskiIhtimaller.get(i).get(j).getYuksekNot().equals(ihtimaller.get(j).getYuksekNot())) {
						aynilar++;
					}
				}
				if (aynilar == ihtimaller.size()) {
					return false;
				}
				aynilar = 0;
			}
			counter++;
			for (Ihtimal ih : ihtimaller) {
				agno += ih.getEtkiDuzeyi();
				minAgno += ih.getEtkiDuzeyi();
				maxAgno += ih.getEtkiDuzeyi();
			}
			double real = Math.round(agno * 1000.0) / 1000.0;
			double min = Math.round(minAgno * 1000.0) / 1000.0;
			double max = Math.round(maxAgno * 1000.0) / 1000.0;
			real = Math.round(real * 100.0) / 100.0;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			min = real - min;
			max = max - real;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			Double[] tutacak = {agno, min, max};
			eskiAgnoVeSapmalar.put(counter, tutacak);
			eskiIhtimaller.put(counter, ihtimaller);
			return true;
		}
	}

	public boolean yeniIhtimallerEkle(ArrayList<Ihtimal> yeniIhtimaller, double yeniAgno, double minYeniAgno, double maxYeniAgno) {
		if (eskiAgnoVeSapmalar.isEmpty()) {
			counter++;
			double minSap = Math.round((yeniAgno-minYeniAgno) * 1000.0) / 1000.0;
			double maxSap = Math.round((maxYeniAgno-yeniAgno) * 1000.0) / 1000.0;
			Double[] a = {yeniAgno, Math.round(minSap * 100.0) / 100.0, Math.round(maxSap * 100.0) / 100.0};
			eskiAgnoVeSapmalar.put(counter, a);
			eskiIhtimaller.put(1, yeniIhtimaller);
			return true;
		} else {
			for (Integer i : eskiAgnoVeSapmalar.keySet()) {
				if(eskiAgnoVeSapmalar.get(i)[0] == yeniAgno)
					return false;
			}
			int aynilar = 0;
			for(Integer j : eskiIhtimaller.keySet()) {
				for(int i = 0; i<yeniIhtimaller.size();i++) {
					if(eskiIhtimaller.get(j).get(i).getYuksekNot().equals(yeniIhtimaller.get(i).getYuksekNot())) {
						aynilar++;
					}
				}
				if(aynilar==yeniIhtimaller.size()) {
					return false;
				}
				aynilar = 0;
			}
			counter++;
			double minSap = Math.round((yeniAgno-minYeniAgno) * 1000.0) / 1000.0;
			double maxSap = Math.round((maxYeniAgno-yeniAgno) * 1000.0) / 1000.0;
			Double[] a = {yeniAgno, Math.round(minSap * 100.0) / 100.0, Math.round(maxSap * 100.0) / 100.0};
			eskiAgnoVeSapmalar.put(counter, a);
			eskiIhtimaller.put(counter, yeniIhtimaller);
			return true;
		}
	}

	public Map<Integer, Double[]> getEskiAgnolar() {
		return eskiAgnoVeSapmalar;
	}

	public Map<Integer, ArrayList<Ihtimal>> getEskiIhtimaller() {
		return eskiIhtimaller;
	}

	public void listByAgno() {
		HashMap<Integer, ArrayList<Ihtimal>> yeniEskiIhtimaller = new HashMap<>();
		HashMap<Integer, Double[]> yeniEskiAgnolar = new HashMap<>();
		Double maxAgno = -0.5;
		Integer maxAgnoInt = 0;
		Integer uzunluk = eskiAgnoVeSapmalar.size();
		for (Integer yeniCounter = 1; yeniCounter<uzunluk+1 ; yeniCounter++) {
			for (Integer j : eskiAgnoVeSapmalar.keySet()) {
				if (eskiAgnoVeSapmalar.get(j)[0]>maxAgno) {
					maxAgno = eskiAgnoVeSapmalar.get(j)[0];
					maxAgnoInt = j;
				}
			}
			yeniEskiIhtimaller.put(yeniCounter, eskiIhtimaller.get(maxAgnoInt));
			Double[] a = {maxAgno, eskiAgnoVeSapmalar.get(maxAgnoInt)[1], eskiAgnoVeSapmalar.get(maxAgnoInt)[2]};
			yeniEskiAgnolar.put(yeniCounter, a);
			maxAgno = -0.5;
			eskiIhtimaller.remove(maxAgnoInt);
			eskiAgnoVeSapmalar.remove(maxAgnoInt);
		}
		this.eskiAgnoVeSapmalar = yeniEskiAgnolar;
		this.eskiIhtimaller = yeniEskiIhtimaller;
	}
	public String toStringOnePossibility(Integer number) {
		StringBuilder message;
		if (number == 1) {
			message = new StringBuilder("Agnonun en yüksek olduğu olasılık:<br>");
		} else if (number == eskiAgnoVeSapmalar.size()) {
			message = new StringBuilder("Agnonun en düşük olduğu olasılık:<br>");
		} else {
			message = new StringBuilder(number + " numaralı olasılık için:<br>");
		}
		for (Ihtimal ihtimal : eskiIhtimaller.get(number)) {
			message.append(ihtimal.toString()).append("<br>");
		}
		message.append("<br>Bu ihtimaller ışığında son agno ").append(String.format(Locale.ROOT, "%.2f", eskiAgnoVeSapmalar.get(number)[0])).append(" bulundu.<br>");
		message.append(getSapma(number));
		return message.toString();
	}

	public String getAgno(int number) {
        return String.format(Locale.ROOT, "%.2f", Objects.requireNonNull(eskiAgnoVeSapmalar.get(number))[0]);
	}
	public String getSapma(int number) {
		String message = "";
		if (eskiAgnoVeSapmalar.get(number)[1]!=0.0 && Math.round(eskiAgnoVeSapmalar.get(number)[1] * 100.0) / 100.0== Math.round(eskiAgnoVeSapmalar.get(number)[2] * 100.0) / 100.0) {
			message = "±" + eskiAgnoVeSapmalar.get(number)[1] + " kadar sapma olabilir.<br>";
		} else if (eskiAgnoVeSapmalar.get(number)[1]!=0.0 && eskiAgnoVeSapmalar.get(number)[2]!=0.0 ) {
			message = "+" + eskiAgnoVeSapmalar.get(number)[2] + " ve -" +eskiAgnoVeSapmalar.get(number)[1] + " kadar sapma olabilir.<br>";
		} else if (eskiAgnoVeSapmalar.get(number)[1]!=0.0 && eskiAgnoVeSapmalar.get(number)[2]==0.0) {
			message = "-" + eskiAgnoVeSapmalar.get(number)[1] + " kadar sapma olabilir.<br>";
		} else if (eskiAgnoVeSapmalar.get(number)[1]==0.0 && eskiAgnoVeSapmalar.get(number)[2]!= 0.0){
			message = "+" + eskiAgnoVeSapmalar.get(number)[2] + " kadar sapma olabilir.<br>";
		}
		return message;
	}

	public String getSapmaDegeri(int number) {
		return getSapma(number).substring(1, 5);
	}
}
