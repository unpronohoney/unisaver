package com.unisaver.unisaver;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ThisTerm {
	private int thisTermLessons;
	private GenelNot pastTerms;
	private GenelNot minGeNot;
	private GenelNot maxGeNot;
	private ArrayList<Ders> dersler;
	private boolean agnoKurtaricisi;
	private IhtimallerDizisi possibilities;
	private ArrayList<Ders> tekrarDers;
	private double minAgno;
	private double maxAgno;

	public ThisTerm(int thisTermLessons, GenelNot pastTerms, boolean agnoKurtaricisi) {
		this.thisTermLessons = thisTermLessons;
		this.pastTerms = pastTerms;
		this.agnoKurtaricisi = agnoKurtaricisi;
		this.dersler = new ArrayList<>();
		if (!agnoKurtaricisi) {
			tekrarDers = new ArrayList<>();
		}
	}

	public ThisTerm(GenelNot pastTerms) {
		this.pastTerms = pastTerms;
		this.dersler = new ArrayList<>();
		this.thisTermLessons = 0;
	}

	public void manuelDersGirisi(Ders ders) {
		dersler.add(ders);
		thisTermLessons++;
		if (ders.getHarfNotu().equals("Yok")) {
			pastTerms.yeniDers(ders.getCredit(), ders.getYeniHarf());
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
		} else {
			pastTerms.eskiDers(ders.getCredit(), ders.getHarfNotu(), ders.getYeniHarf());
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
		}
	}

	public void dersKrediGuncelle(Ders ders, int newKredi) {
		if (ders.getCredit() != newKredi) {
			pastTerms.yeniKredi(ders.getCredit(), ders.getHarfNotu(), ders.getYeniHarf(), newKredi);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setCredit(newKredi);
		}
	}

	public void dersEskiHarfGuncelle(Ders ders, String newEskiHarf) {
		if (!ders.getHarfNotu().equals(newEskiHarf)) {
			pastTerms.yeniEskiHarf(ders.getCredit(), ders.getHarfNotu(), newEskiHarf);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setHarfNotu(newEskiHarf);
		}
	}

	public void dersYeniHarfGuncelle(Ders ders, String newYeniHarf) {
		if (!ders.getYeniHarf().equals(newYeniHarf)) {
			pastTerms.yeniYeniHarf(ders.getCredit(), ders.getYeniHarf(), newYeniHarf);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setYeniHarf(newYeniHarf);
		}
	}

	public void dersSilme(Ders ders) {
		dersler.remove(findDers(ders.getDersNo()));
		thisTermLessons--;
		for (int i = ders.getDersNo(); i < dersler.size(); i++) {
			dersler.get(i).setDersNo(i+1);
		}
		pastTerms.dersSilme(ders);
		minAgno = pastTerms.getCurrentAGNO() - 0.005;
		maxAgno = pastTerms.getCurrentAGNO() + 0.005;
	}

	private Ders findDers(int no) {
		for (Ders d : dersler) {
			if (d.getDersNo() == no) {
				return d;
			}
		}
		return null;
	}

	public String getAgnoInfo() {
		String ret;
		double real = Math.round(pastTerms.getCurrentAGNO() * 1000.0) / 1000.0;
		double min = Math.round(minAgno * 1000.0) / 1000.0;
		double max = Math.round(maxAgno * 1000.0) / 1000.0;
		real = Math.round(real * 100.0) / 100.0;
		min = Math.round(min * 100.0) / 100.0;
		max = Math.round(max * 100.0) / 100.0;
		min = real - min;
		max = max - real;
		min = Math.round(min * 100.0) / 100.0;
		max = Math.round(max * 100.0) / 100.0;
		if (min > 0.0 && max > 0.0 && min == max) {
			ret = "Agno (GNO): " + real + "±" + min + "\n" + "Kredi: " + pastTerms.getKrediSayisi();
		} else if (min > 0.0 && max == 0.0) {
			ret = "Agno (GNO): " + real + "-" + min + "\n" + "Kredi: " + pastTerms.getKrediSayisi();
		} else if (min == 0.0 && max > 0.0) {
			ret = "Agno (GNO): " + real + "+" + max + "\n" + "Kredi: " + pastTerms.getKrediSayisi();
		} else if (min == 0.0 && max == 0.0) {
			ret = "Agno (GNO): " + real + "\n" + "Kredi: " + pastTerms.getKrediSayisi();
		} else {
			ret = "Agno (GNO): " + real + "\n" + "Kredi: " + pastTerms.getKrediSayisi();
		}
		return ret;
	}

	public void yuvarlamaPaylari() {
		int kredi = pastTerms.getKrediSayisi();
		double agno = pastTerms.getCurrentAGNO();
		this.minGeNot = new GenelNot(kredi, agno-0.005);
		this.maxGeNot = new GenelNot(kredi, agno+0.005);
		this.minAgno = pastTerms.getCurrentAGNO() - 0.005;
		this.maxAgno = pastTerms.getCurrentAGNO() + 0.005;
	}

	public void dersGirisiArr(ArrayList<Ders> derss) {
        dersler.addAll(derss);
	}
	public boolean dersGirisi(Ders ders, boolean tekrarAlinanMi) {
		if (tekrarAlinanMi) {
			if (tekrarDers.size() + dersler.size() < thisTermLessons) {
				tekrarDers.add(ders);
				return true;
			}
			return false;
		}
		if (tekrarDers.size() + dersler.size() < thisTermLessons) {
			dersler.add(ders);
			return true;
		}
		else
			return false;
	}

	public int dersGirisiKSH(Ders ders) {
		int kredi = 0;
		for (Ders d : dersler) {
			kredi += d.getCredit();
		}
		kredi += ders.getCredit();
		if (agnoKurtaricisi && dersler.size()+1<=thisTermLessons && kredi<=pastTerms.getKrediSayisi()) {
			dersler.add(ders);
			return 0;
		} else if (dersler.size()+1>thisTermLessons) {
			return 1;
		} else {
			return 2;
		}
	}

	public int getDerslerinKrediSayisi() {
		int topKredi = 0;
		for (Ders d : dersler) {
			topKredi += d.getCredit();
		}
		return topKredi;
	}

	public boolean puanFizibilMi() {
		double puan = 0;
		int kredi = 0;
		for (Ders d : dersler) {
			puan += Ders.getPoint(d.getHarfNotu()) * d.getCredit();
			kredi += d.getCredit();
		}
		puan /= pastTerms.getKrediSayisi();
		if (pastTerms.getCurrentAGNO() == puan && kredi == pastTerms.getKrediSayisi())			//diğerleri FF
			return true;
		else if (pastTerms.getCurrentAGNO() != puan && kredi <= pastTerms.getKrediSayisi())
			if (pastTerms.getCurrentAGNO() >= puan)
				return true;
			else return puan - pastTerms.getCurrentAGNO() < 0.005;
        else
			return false;
	}

	public boolean puanFizibilmiTekrarDersler() {
		if (tekrarDers.size() == 0) {
			return true;
		}
		double puan = 0;
		int kredi = 0;
		for (Ders d : tekrarDers) {
			puan += Ders.getPoint(d.getHarfNotu()) * d.getCredit();
			kredi += d.getCredit();
		}
		puan /= pastTerms.getKrediSayisi();
		if (pastTerms.getCurrentAGNO() == puan && kredi == pastTerms.getKrediSayisi())			//diğerleri FF
			return true;
		else if (pastTerms.getCurrentAGNO() != puan && kredi <= pastTerms.getKrediSayisi())
			if (pastTerms.getCurrentAGNO() >= puan)
				return true;
			else return puan - pastTerms.getCurrentAGNO() < 0.005;
		else
			return false;
	}

	public boolean ihtimallerDizisi() {
		if (agnoKurtaricisi) {
			this.possibilities = new IhtimallerDizisi();
			return true;
		} else
			return false;
	}

	public String setNewAgno(ArrayList<String> yeniHarfler) {
		if (dersler.size() != 0) {
			pastTerms.puanGuncelle(dersler);
			minGeNot.puanGuncelle(dersler);
			maxGeNot.puanGuncelle(dersler);
		}
		if (tekrarDers.size() != 0 && tekrarDers.size() == yeniHarfler.size()) {
			pastTerms.puanGuncelleTekrar(tekrarDers, yeniHarfler);
			minGeNot.puanGuncelleTekrar(tekrarDers, yeniHarfler);
			maxGeNot.puanGuncelleTekrar(tekrarDers, yeniHarfler);
		}
		double max = Math.round(maxGeNot.getCurrentAGNO() * 100.0) / 100.0;
		double min = Math.round(minGeNot.getCurrentAGNO() * 100.0) / 100.0;
		double now = Math.round(pastTerms.getCurrentAGNO() * 100.0) / 100.0;
		double[] sapmaPaylari = {Math.round((max-now) * 100.0 ) / 100.0, Math.round((now-min) * 100.0 ) / 100.0};
		if (sapmaPaylari[0]!=0.0 && sapmaPaylari[1]!=0.0) {
			if (sapmaPaylari[0]==sapmaPaylari[1]) {
				return "±"+sapmaPaylari[0]+" kadar sapma olabilir.";
			} else {
				return "+"+sapmaPaylari[0]+" ve -"+sapmaPaylari[1]+" kadar sapma olabilir.";
			}
		} else if (sapmaPaylari[0]==0.0 && sapmaPaylari[1]!=0.0) {
			return "-"+sapmaPaylari[1]+" kadar sapma olabilir.";
		} else if (sapmaPaylari[0]!=0.0 && sapmaPaylari[1]==0.0) {
			return "+"+sapmaPaylari[0]+" kadar sapma olabilir.";
		} else {
			return "";
		}
	}

	public String[] dersGuncelleme(ArrayList<String> guncelHarfler) {
		if (agnoKurtaricisi) {
			double now = pastTerms.getCurrentAGNO();
			double min = minGeNot.getCurrentAGNO();
			double max = maxGeNot.getCurrentAGNO();
			for (int i = 0; i<guncelHarfler.size(); i++) {
				now += pastTerms.puanGuncelleIhtimal(dersler.get(i), guncelHarfler.get(i)) - pastTerms.getCurrentAGNO();
				min += minGeNot.puanGuncelleIhtimal(dersler.get(i), guncelHarfler.get(i)) - minGeNot.getCurrentAGNO();
				max += maxGeNot.puanGuncelleIhtimal(dersler.get(i), guncelHarfler.get(i)) - maxGeNot.getCurrentAGNO();
			}
			max = Math.round(max * 100.0) / 100.0;
			min = Math.round(min * 100.0) / 100.0;
			now = Math.round(now * 100.0) / 100.0;
			double[] sapmaPaylari = {now, Math.round((now-min) * 100.0 ) / 100.0, Math.round((max-now) * 100.0 ) / 100.0};
			String[] ret = {String.format("%.2f", now), ""};
			if (sapmaPaylari[1]!=0.0 && sapmaPaylari[2]!=0.0) {
				if (sapmaPaylari[1]==sapmaPaylari[2]) {
					ret[1] = "±"+sapmaPaylari[1]+" kadar sapma olabilir.";
				} else {
					ret[1] = "+"+sapmaPaylari[2]+" ve -"+sapmaPaylari[1]+" kadar sapma olabilir.";
				}
			} else if (sapmaPaylari[1]==0.0 && sapmaPaylari[2]!=0.0) {
				ret[1] = "-"+sapmaPaylari[1]+" kadar sapma olabilir.";
			} else if (sapmaPaylari[1]!=0.0 && sapmaPaylari[2]==0.0) {
				ret[1] = "+"+sapmaPaylari[2]+" kadar sapma olabilir.";
			}
			return ret;
		} else {
			return null;
		}
	}

	private ArrayList<Ihtimal> arttirma(int arttir, ArrayList<Ihtimal> ihtimaller, String maxHarf) {
		int i = 0;
		for (int j = 0; j<arttir;j++) {
			if (i==ihtimaller.size()) {
				i=0;
			}
			ihtimaller.get(i).randomize(ihtimaller.get(i).getDers().getLetter(Ders.
					getPoint(ihtimaller.get(i).getYuksekNot()), 0.5), "", maxHarf);
			if (ihtimaller.get(i).getYuksekNot().equals("")) {
				break;
			}
			i++;
		}
		return ihtimaller;
	}

	private boolean kolayKSH(double minAgno, double maxAgno, String minHarf, String maxHarf, int islem, int arttir) {
		GenelNot sonNot = new GenelNot(pastTerms.getKrediSayisi(), pastTerms.getCurrentAGNO());
		GenelNot minNot = new GenelNot(minGeNot.getKrediSayisi(), minGeNot.getCurrentAGNO());
		GenelNot maxNot = new GenelNot(maxGeNot.getKrediSayisi(), maxGeNot.getCurrentAGNO());
		double agnoSon = sonNot.getCurrentAGNO();
		double agnoMin = minNot.getCurrentAGNO();
		double agnoMax = maxNot.getCurrentAGNO();
		ArrayList<Ihtimal> ihtimaller = new ArrayList<>();
		for (Ders d : dersler) {
			Ihtimal ih = new Ihtimal(sonNot, d);
			if (islem == 1) {
				if (maxHarf.equals("Limitsiz")) {
					ih.randomize("AA", "", "");
				} else {
					ih.randomize(maxHarf, "", "");
				}
			} else if (islem == 2) {
				if (minHarf.equals("Limitsiz")) {
					ih.randomize("FF", "", maxHarf);
				} else {
					ih.randomize(minHarf, "", maxHarf);
				}
			} else {
				ih.randomize("", minHarf, maxHarf);
			}
			ihtimaller.add(ih);
		}
		if (islem == 2) {
			ihtimaller = arttirma(arttir, ihtimaller, maxHarf);
		}
		double etkiler = 0.0;
		for (Ihtimal ih : ihtimaller) {
			if (ih.getYuksekNot().equals("")) {
				return false;
			}
			etkiler += ih.getEtkiDuzeyi();
		}
		etkiler = Math.round(etkiler * 100.0) / 100.0;
		agnoSon += etkiler;
		agnoMin += etkiler;
		agnoMax += etkiler;
		if (islem == 1) {
			if (agnoSon>=minAgno && maxAgno == 0) {
				if (possibilities.yeniIhtimallerEkle(new ArrayList<Ihtimal>(ihtimaller), agnoSon, agnoMin, agnoMax)) {
					return true;
				}
			}
			if (agnoSon>=minAgno && agnoSon<=maxAgno) {
				if (possibilities.yeniIhtimallerEkle(new ArrayList<Ihtimal>(ihtimaller), agnoSon, agnoMin, agnoMax)) {
					return true;
				}
			}
		} else if(islem == 2) {
			if (agnoSon>minAgno) {
				for (Ihtimal i : ihtimaller) {
					if (i.getYuksekNot().equals(i.getDers().getHarfNotu())) {
						return false;
					}
				}
				if (possibilities.yeniIhtimallerEkle(new ArrayList<Ihtimal>(ihtimaller), agnoSon, agnoMin, agnoMax)) {
					return true;
				}
			}
		} else {
			if (agnoSon>=minAgno && maxAgno==0.0) {
				if (possibilities.yeniIhtimallerEkle(new ArrayList<Ihtimal>(ihtimaller), agnoSon, agnoMin, agnoMax)) {
					return true;
				}
			}
			else if (agnoSon>=minAgno && agnoSon<=maxAgno) {
				if (possibilities.yeniIhtimallerEkle(new ArrayList<Ihtimal>(ihtimaller), agnoSon, agnoMin, agnoMax)) {
					return true;
				}
			}
		}
		return false;
	}

	public int ihtimalSayisi() {
		int ret = 1;
		int dahilDegil = -1;
		for (Ders d :dersler) {
			if ((4 - Ders.getPoint(d.getHarfNotu())) * 2 == 1) {
				dahilDegil = dersler.indexOf(d);
				break;
			}
		}
		for (Ders d : dersler) {
			if (dersler.indexOf(d) == dahilDegil) {
				continue;
			}
			int a =  (int) ((4 - Ders.getPoint(d.getHarfNotu())) * 2);
			if (a <= 1 && dersler.indexOf(d) == 0) {
				ret = a;
			} else if (a <= 1) {
				ret += a;
			} else {
				ret *= a;
			}
		}
		return ret;
	}

	public boolean kendimiSansliHissediyorum(double istenilenAgno, int olasilikSayisi, double maxAgno, String minHarf, String maxHarf) {
		int ihtimalSayisi = ihtimalSayisi();
		if (ihtimalSayisi == 0)
			return false;
		int olusanIhtimaller = 0;
		if (kolayKSH(istenilenAgno, maxAgno, minHarf, maxHarf, 1, 0))  {
			olusanIhtimaller++;
		}
		if (olusanIhtimaller==0) {
			return false;
		}
		if (olusanIhtimaller==olasilikSayisi ||  ihtimalSayisi== 1) {
			return true;
		}
		int cont = olusanIhtimaller;
		int ecx = (int) Math.pow(dersler.size(), 8.0);
		for (int a = 0; a < ecx; a++) {
			if (kolayKSH(istenilenAgno, maxAgno, minHarf, maxHarf, 2, a))  {
				olusanIhtimaller++;
				break;
			}
		}
		if (cont == olusanIhtimaller)
			return false;
		if (olusanIhtimaller==olasilikSayisi || ihtimalSayisi== 2) {
			return true;
		}
		int kontrol = 0;
		boolean kontrol2 = false;
		while (true){
			for (int j = 0; j<50; j++) {
				if (kolayKSH(istenilenAgno, maxAgno, minHarf, maxHarf, 3, 0))  {
					olusanIhtimaller++;
					kontrol2 = true;
					break;
				}
			}
			if (olusanIhtimaller==olasilikSayisi) {
				possibilities.listByAgno();
				return true;
			}
			kontrol++;
			if (kontrol2) {
				kontrol = 0;
				kontrol2 = false;
			}
			if (kontrol==200) {
				return false;
			}
		}
	}

	public boolean newKsh(double minAgno, double maxAgno, String minHarf, String maxHarf, int komSayisi) {
		if (maxAgno == -1) {
			maxAgno = 4.0;
		}
		if (minHarf.equals("Limitsiz")) {
			minHarf = Ders.getMinHarf();
		}
		if (maxHarf.equals("Limitsiz")) {
			maxHarf = Ders.getMaxHarf();
		}
		int kredi = pastTerms.getKrediSayisi();
		for (Ders d : dersler) {
			if (d.getHarfNotu().equals("Yok")) {
				kredi += d.getCredit();
			}
		}
		pastTerms.setToplamKredi(kredi);
		minGeNot.setToplamKredi(kredi);
		maxGeNot.setToplamKredi(kredi);

		int olusanKomlar = 0;

		ArrayList<Ihtimal> ihtimaller = new ArrayList<>();
		double denemePuani = pastTerms.getCurrentAGNO();
		for (Ders d : dersler) {
			Ihtimal ih = new Ihtimal(pastTerms, d);
			ih.newRandomize(minHarf, maxHarf, maxHarf);
			denemePuani += ih.getEtkiDuzeyi();
			ihtimaller.add(ih);
		}

		if (denemePuani < maxAgno && denemePuani > minAgno) {
			possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO());
			olusanKomlar++;
		}

		if (olusanKomlar == komSayisi) {
			return true;
		}
		ihtimaller = new ArrayList<>();

		denemePuani = pastTerms.getCurrentAGNO();
		for (Ders d : dersler) {
			Ihtimal ih = new Ihtimal(pastTerms, d);
			ih.newRandomize(minHarf, maxHarf, minHarf);
			denemePuani += ih.getEtkiDuzeyi();
			ihtimaller.add(ih);
		}
		if (denemePuani < maxAgno && denemePuani > minAgno) {
			possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO());
			olusanKomlar++;
		}
		if (olusanKomlar == komSayisi) {
			return true;
		}

		int denemeSayisi = 0;
		while (true) {
			denemeSayisi++;
			ihtimaller = new ArrayList<>();
			denemePuani = pastTerms.getCurrentAGNO();
			for (Ders d : dersler) {
				Ihtimal ih = new Ihtimal(pastTerms, d);
				ih.newRandomize(minHarf, maxHarf, "");
				denemePuani += ih.getEtkiDuzeyi();
				ihtimaller.add(ih);
			}
			boolean dene = false;
			if (denemePuani < maxAgno && denemePuani > minAgno) {
				dene = possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO());
			}
			if (dene) {
				olusanKomlar++;
				denemeSayisi = 0;
			}
			if (olusanKomlar == komSayisi)
				return true;
			if (denemeSayisi == 200) {
				return false;
			}
		}
	}

	public void setDersSayisi(int dersSayisi) {
		this.thisTermLessons = dersSayisi;
	}

	public IhtimallerDizisi getPossibilities() {
		return possibilities;
	}

	public int getThisTermLessons() {
		return thisTermLessons;
	}

	public GenelNot getPastTerms() {
		return pastTerms;
	}
}
