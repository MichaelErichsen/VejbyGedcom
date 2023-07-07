package net.myerichsen.archivesearcher.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encodes a Danish name phonetically
 *
 * @author Michael Erichsen
 * @version 6. jul. 2023
 */
public class Fonkod {

	/**
	 * No-arg c:tor
	 *
	 */
	public Fonkod() {
	}

	/**
	 * Den del af navnet, der ligger foran f�rste vokalgruppe, unders�ges for
	 * umulige konsonantsammenstillinger.
	 *
	 * @param textField
	 * @return String
	 */
	private String eliminateImpossibleConsonantCombinations(String input) {
		String out = input;
		if (out.length() < 2 || isVowel(out.charAt(0), out.charAt(1))) {
			return out;
		}

		// Er der overhovedet vokaler?
		Pattern p = Pattern.compile("[aeiouy���]+");
		Matcher m = p.matcher(out);
		if (!m.find()) {
			return out;
		}

		// ��� etc
//		out = out.replace("ø", "�").replace("å", "�").replace("ë", "�").replace("ü", "�");
		out = out.replace("ø", "�").replace("å", "�").replace("ë", "e").replace("ü", "u");

		// _n/l/r jC -> _n/l/r j
		out = out.replaceFirst("\\b([nlr]j)[^aeiouy���]", "$1");

		// _n/l/r (C^j) -> _n/l/r
		out = out.replaceFirst("\\b([nlr])[^aeijouy���]", "$1");

		// _j -> _
		out = out.replaceFirst("\\bj", "");

		// _^(s/n/l/r/j) n/l/r/v/j j -> _^(s/n/l/r/j) n/l/r/v/j
		out = out.replaceFirst("\\b([^snlrj][nlrvj])j", "$1");

		// _^(s/n/l/r/j) n/l/r/v/j (C^j) -> _^(s/n/l/r/j) (C^j)
		out = out.replaceFirst("\\b([^snlrj])[nlrvj]([^aeijouy���])", "$1$2");

		// st j/r C -> st j/r
		out = out.replaceFirst("(st[jr])[^aeiouy���]", "$1");

		// st (C^j/r) -> st
		out = out.replaceFirst("(st)[^aeijoruy���]", "$1");

		// sp j/r/l C -> st j/r/l
		out = out.replaceFirst("(sp[jrl])[^aeiouy���]", "$1");

		// sp (C^j/r/l) -> sp
		out = out.replaceFirst("(sp)[^aeijloruy���]", "$1");

		// skr -> sr
		out = out.replaceFirst("skr", "sr");

		// skj -> sk & sj
		p = Pattern.compile("skj");

		if (out.length() >= 3) {
			m = p.matcher(out.substring(0, 3));
			if (m.matches()) {
				return out.replaceFirst("skj", "sk") + " " + out.replaceFirst("skj", "sj");
			}
		}

		return out.replaceFirst("(sk)[^aeijoruy���]", "$1");
	}

	/**
	 * Navnedelen efter f�rste vokal behandles samlet
	 *
	 * @param input
	 * @return String
	 */
	private String finalizeProcessing(String input) {
		String out = input;
		Pattern p;
		Matcher m;

		// Navnedel efter f�rste vokalgruppe
		// Hvis navn slutter med f�rste vokalgruppe, returneres
		p = Pattern.compile("[aeiouy���]+[^aeiouy���]");
		m = p.matcher(input);
		if (!m.find()) {
			return out;
		}

		// sC_
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*s[^aieouy���])\\b", "$1");

		// sC...s/V
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*s[^aieouy���])[^aeiosuy���]*([aeiosuy���]).*", "$1$2");

		// sV -> sr
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*+s)[aieouy���].*", "$1r");

		// (C^s)s
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*[^aieosuy���]s).*", "$1");

		// (C^s)(C^s)...s/V
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*[^aieosuy���]{2})[^aeiosuy���]*([aeiosuy���]).*", "$1$2");

		// (C^s)(C^s)_
		out = out.replaceFirst("([aeiouy���]+[^aeiouy���]*[^aieosuy���]{2})\\b", "$1");

		return out.replaceFirst("([aeiouy���]+[^aeiouy���]*[^aieosuy���])[^aeiosuy���]*([aeiosuy���]).*", "$1$2");
	}

	/**
	 * Transform a family name to a Danish phonetic encoding
	 *
	 * @param slaegtsNavn a family name
	 * @return the phonetic representation of the family name
	 * @throws Exception If name missing or not valid
	 */
	public String generateKey(String slaegtsNavn) throws Exception {
		if (slaegtsNavn == null) {
			throw new Exception("Sl�gtsnavn mangler");
		}

		String out = slaegtsNavn.toLowerCase();
		String[] sa;

		// Kun bogstaver, blank, bindestreg og gnyf er tilladt
		out = out.replace("'", "");
		Pattern p = Pattern.compile("([a-z�����������]|/|-|\\.|\\s)+");
		Matcher m = p.matcher(out);
		if (!m.matches()) {
			// throw new Exception("Ugyldigt sl�gtsnavn: " + slaegtsNavn);
			return "";
		}

		if (out.length() == 1) {
			return out.trim();
		}

		/**
		 * Det givne navn sammenlignes med en tabel over almindelige navne. Hvis navnet
		 * findes heri, benyttes den hertil svarende tabelkode
		 *
		 * Rykket foran n�ste afsnit af MER
		 *
		 */
		// st = new StringTokenizer(out, " ");
		// sb = new StringBuffer();
		// NameTable nt = new NameTable();
		// String s = "";
		// while (st.hasMoreTokens()) {
		// s = nt.getTableCode(sa[i]);
		// if (s.trim().length() > 0) {
		// System.out.println(s + "\t found in lookUpTable ");
		// return s;
		// }
		// }
		// System.out.println(out + "\tNot found in lookUpTable ");
		/**
		 * Indledningsvis oms�ttes visse tegn, s�ledes at en f�rste standardisering af
		 * navnet opn�s
		 */
		p = Pattern.compile("(\\.|-|\\s|/)+");
		sa = p.split(out);
		StringBuilder sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(performInitialStandardization(element) + " ");
			// System.out.println(sb.toString() +
			// "\tperformInitialStandardization ");
		}

		/**
		 * F�rste vokalgruppe identificeres og omdannes efter behov. Hvis flere end to
		 * vokaler indg�r i en vokalgruppe, behandles vokalerne parvis med start forfra
		 * i gruppen.
		 */
		sa = p.split(sb.toString().trim());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(processFirstVowelGroup(element) + " ");
			// System.out.println(sb.toString() + "\tprocessFirstVowelGroup");
		}

		/**
		 * Efter behandlingen af f�rste vokalgruppe behandles evt. foranstillede
		 * konsonanter sammen med f�rste vokal
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(processFirstConsonantGroup(element) + " ");
			// System.out.println(sb.toString() +
			// "\tprocessFirstConsonantGroup");
		}

		/**
		 * En evt. anden vokalgruppe behandles som den f�rste
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(processSecondVowelGroup(element) + " ");
			// System.out.println(sb.toString() + "\tprocessSecondVowelGroup");
		}

		/**
		 * Navnedelen efter f�rste vokalgruppe behandles
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(processSecondConsonantGroup(element) + " ");
			// System.out.println(sb.toString() +
			// "\tprocessSecondConsonantGroup");
		}

		/**
		 * De to vokalgrupper samt mellemliggende konsonanter behandles i �n sammenh�ng
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(finalizeProcessing(element) + " ");
			// System.out.println(sb.toString() + "\tfinalizeProcessing 1");
		}

		/**
		 * Dubletbogstaver fjernes
		 */
		out = sb.toString().replaceAll("(.)\\1", "$1");
		// System.out.println(out + "\teliminateDoubleCharacters");

		/**
		 *
		 * Den forel�bige kode unders�ges for umulige konsonantgrupper
		 */
		sa = p.split(out);
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(eliminateImpossibleConsonantCombinations(element) + " ");
			// System.out.println(sb.toString() +
			// "\teliminateImpossibleConsonantCombinations");
		}

		/**
		 * De to vokalgrupper samt mellemliggende konsonanter behandles i �n sammenh�ng
		 * igen
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(finalizeProcessing(element) + " ");
		}
		out = sb.toString();
		// System.out.println(out + "\tfinalizeProcessing 2");

		/**
		 * Dubletkoder fjernes
		 */
		p = Pattern.compile("(\\w+)\\s+\\1");
		m = p.matcher(out);
		while (m.find()) {
			// Fejl nedenfor?
			out = out.replaceAll("(\\w+\\s+)\\1", "$1");
			// System.out.println(out + "\teliminateDoubleOutput");
		}

		return out.trim();
	}

	/**
	 * Is any of two characters a vowel?
	 *
	 * @param a
	 * @param b
	 * @return boolean
	 */
	private boolean isVowel(char a, char b) {
		final Pattern p = Pattern.compile("[aeiouy���]");
		final Matcher m = p.matcher(String.valueOf(a));
		if (m.matches()) {
			return true;
		}
		m.reset(String.valueOf(b));
		return m.matches();
	}

	/**
	 * Visse gamle eller fremmede staveformer narmaliseres
	 *
	 * @param input
	 * @return
	 */
	private String normaliseForeignSpellings(String input) {
		String out = input;

		// MER Tilf�jelser
		out = out.replaceAll("(aa|�)", "o");
		out = out.replace("qu", "kv");
		out = out.replaceAll("(ph|pf)", "f");
		out = out.replaceAll("[��]", "y");
		out = out.replaceAll("[����]", "e");
		out = out.replaceAll("[�z]", "s");
		out = out.replace('q', 'k');
		out = out.replace('w', 'v');
		out = out.replace('�', '�');
		return out.replace("�", "sj");
	}

	/**
	 * Indledningsvis oms�ttes visse tegn, s�ledes at en f�rste standardisering af
	 * navnet opn�s
	 *
	 * @param nextToken
	 * @return
	 */
	private String performInitialStandardization(String nextToken) {
		String out = nextToken;
		StringBuilder sb = new StringBuilder();
		final String[] patterns = { "vo", "v�", "val", "ver", "vie", "vet", "vah", "vri" };

		// MER Af, de, du, la og von fjernes
		Pattern p = Pattern.compile("(af|de|du|la|von)");
		Matcher m = p.matcher(out.trim());
		if (m.matches()) {
			return "";
		}

		// "vo", "v�", "val", "ver", "vie", "vet", "vah" og "vri"; dubleres v og
		// f
		for (final String pattern : patterns) {
			p = Pattern.compile(pattern);
			m = p.matcher(out);

			if (m.lookingAt() && m.start() == 0) {
				sb.append("v" + out.substring(1) + " f" + out.substring(1));
				out = sb.toString();
				break;
			}
		}

		/*
		 * Visse gamle eller fremmede staveformer normaliseres
		 */
		p = Pattern.compile(" ");
		String[] sa = p.split(out);
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(normaliseForeignSpellings(element) + " ");
		}

		/**
		 * x, h og fordoblinger
		 */
		sa = p.split(sb.toString());
		sb = new StringBuilder();
		for (final String element : sa) {
			sb.append(processHXAndDoubles(element) + " ");
		}
		out = sb.toString();

		/**
		 * Patronymika
		 */
		if (out.trim().length() > 3) {
			out = out.replaceAll("(sen|son|sdtr|sdater|sdoter)(\\s|\\b)", "s ");
		}

		return out;
	}

	/**
	 * Efter behandlingen af f�rste vokalgruppe behandles evt. foranstillede
	 * konsonanter sammen med f�rste vokal
	 *
	 * @param input
	 * @return String
	 */
	private String processFirstConsonantGroup(String input) {
		String out = input;

		// Find f�rste konsonantgruppe
		// Afslut, hvis den ikke er foranstillet
		final Pattern p = Pattern.compile("\\b[^aeiouy���]+");
		final Matcher m = p.matcher(input);
		if (!m.lookingAt()) {
			return input;
		}

		// Sidste konsonant i gruppen
		// Bl�dt c som sidste konsonant -> s
		out = out.replaceFirst("\\b([^aceiouy���]*)c([eiy])", "$1s$2");

		// H�rdt c som sidste konsonant -> k
		out = out.replaceFirst("\\b([^aceiouy���]*)c([aou���])", "$1k$2");

		// N�stsidste eller f�r
		// H�rdt d -> t
		out = out.replaceFirst("\\b([^adeiouy���]*)d([^aeiouy���]+)", "$1t$2");

		// H�rdt g -> k
		out = out.replaceFirst("\\b([^aegiouy���]*)g([^aeiouy���]+)", "$1k$2");

		// ts -> s
		out = out.replaceFirst("\\b([^aeiotuy���]*)ts([^aeiouy���]+)", "$1s$2");

		// kj -> k
		out = out.replaceFirst("\\b([^aeikouy���]*)kj", "$1k");

		// N�stsidste
		// sco/u -> sko/u
		out = out.replaceFirst("\\b([^aeiosuy���]*)sc([ou])", "$1sk$2");

		// sc(^o/u) -> s(^o/u)
		out = out.replaceFirst("\\b([^aeiouy���]*)sc([aeiy���])", "$1s$2");

		// cs/j -> ss/j
		out = out.replaceFirst("\\b([^aceiouy���]*)c([sj])", "$1s$2");

		// cho -> ko
		out = out.replaceFirst("\\b([^aceiouy���]*)cho", "$1ko");

		// ch(V^o) -> sj(V^o)
		out = out.replaceFirst("([^aceiouy���]*)ch([aeiuy���])", "$1sj$2");

		// cV -> kV
		out = out.replaceFirst("([^aceiouy���]*)c([aeiouy���])", "$1k$2");

		// F�r n�stsidste
		// schov -> skov
		out = out.replaceFirst("\\b([^aeiosuy���]*s)ch(ov)", "$1k$2");

		// schV -> sjV
		out = out.replaceFirst("\\b([^aeiosuy���]*)sch([aeiouy���]+)", "$1sj$2");

		// MER: schr -> skr
		out = out.replaceFirst("\\b([^aeiosuy���]*)schr", "$1skr");

		// schC -> sC
		out = out.replaceFirst("\\b([^aeiosuy���]*)sch([^aeiouy���]+)", "$1s$2");

		// sc -> s
		out = out.replaceFirst("\\b([^aeiosuy���]*)sc", "$1s");

		// Bsd/t -> Bs
		out = out.replaceFirst("\\b([^aeiouy���]+)s[dt]", "$1s");

		// _chr/l -> _kr/l
		out = out.replaceFirst("\\b([^aeiouy���]*)ch([lr])", "$1k$2");

		// _chC -> _C
		out = out.replaceFirst("\\b([^aeiouy���]*)ch([^aeiouy���]+)", "$1$2");

		// ch -> k
		out = out.replaceFirst("\\b([^aceiouy���]*)ch", "$1k");

		// cs/j -> ss/j
		out = out.replaceFirst("\\b([^aceiouy���]*)c([js])", "$1s$2");

		return out.replaceFirst("\\b([^aceiouy���]*)c", "$1k");
	}

	/**
	 * F�rste vokalgruppe identificeres og omdannes efter behov. Hvis flere end to
	 * vokaler indg�r i en vokalgruppe, behandles vokalerne parvis med start forfra
	 * i gruppen
	 *
	 * @param input
	 * @return String
	 */
	private String processFirstVowelGroup(String input) {
		String out = input;
		Pattern p;
		Matcher m;

		// Er der overhovedet vokaler?
		p = Pattern.compile("[aeiouy���]+");
		m = p.matcher(input);
		if (!m.find()) {
			return input;
		}

		// Kun �n vokal i gruppen?
		if (m.group().length() < 2) {

			// y fordobles til y og �
			final int st = m.start();
			if (input.charAt(st) == 'y') {
				return input + " " + input.substring(0, st) + '�' + input.substring(st + 1);
			}
			// Kun en vokal i gruppen, men ikke y
			return input;
		}

		// Der er mindst to vokaler i gruppen
		// Det unders�ges, om nogen af f�lgende regler skal bruges til
		// oms�tning. e som anden vokal er et specialtilf�lde

		// [Klusil eller labiodental frikativ]ie -> []je
		out = out.replaceAll("([bdfgkptv])ie", "$1je");

		// [Klusil eller labiodental frikativ]Ve -> []e
		out = out.replaceAll("([bdfgkptv][aeiouy���])e", "$1");

		// eu er et specialtilf�lde, som fordobles til ev og �j
		p = Pattern.compile("eu");
		m = p.matcher(out);
		if (m.matches()) {
			return out.replaceFirst("eu", "ev") + " " + out.replaceFirst("eu", "�j");
		}

		// �vrig h�ndtering af vokalpar
		final String org = out;
		out = out.replaceFirst("([aeiouy���])i", "$1j");
		out = out.replaceFirst("[iy]([aou])", "j$1");
		out = out.replaceFirst("i�", "j�");
		out = out.replaceFirst("(u)[ao]", "v$1");
		out = out.replaceFirst("([ao])u", "$1v");
		out = out.replaceFirst("([aeo�])y", "$1j");
		if (org.equalsIgnoreCase(out)) {
			return out.replaceFirst("([aeiouy���])[aeiouy���]", "$1");
		}
		return out;
	}

	/**
	 * x, h og fordoblinger
	 *
	 * @param nextToken
	 * @return
	 */
	private String processHXAndDoubles(String input) {
		String out = input;
		Pattern p;
		Matcher m;

		// x -> s eller ks
		p = Pattern.compile("x");
		m = p.matcher(out);
		if (m.find()) {
			if (m.start() == 0) {
				out = m.replaceFirst("s");
			} else {
				out = out.replace("x", "ks");
			}
		}

		// Aspirerert eller stumt h
		out = out.replaceFirst("\\b(h[aeiouy���])", "$1");
		out = out.replaceAll("([^c])h", "$1");
		// MER tilf�jet
		out = out.replaceFirst("\\bhv", "v");

		// gg -> k
		out = out.replace("gg", "k");

		// BB -> B
		out = out.replaceAll("(.)\\1", "$1");

		return out.replace("ck", "k");
	}

	/**
	 * Navnedelen efter f�rste vokalgruppe behandles
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private String processSecondConsonantGroup(String input) throws Exception {
		String out = input;
		StringBuffer sb;

		// Find f�rste konsonantgruppe efter f�rste vokalgruppe
		Pattern p = Pattern.compile("[aeiouy���]+[^aeiouy���]+");
		Matcher m = p.matcher(input);

		if (!m.find()) {
			return out;
		}

		// oj -> �j
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]*)oj", "$1�j");

		// aj -> ej
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]*)aj", "$1ej");

		// Bn/ld -> Bn/l
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*[ln])d", "$1");

		// Vd_ -> V
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)d\\b", "$1");

		// Vd�/e -> V�/e
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)d([e�])", "$1$2");

		// VdC -> VC
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)d([^aeiouy���])", "$1$2");

		// MER Tilf�jet
		// Bd_ -> B_
		p = Pattern.compile("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*)d\\b");
		m = p.matcher(out);
		if (m.find()) {
			sb = new StringBuffer();
			m.appendReplacement(sb, "$1");
			out = sb.toString();
		} else {
			// Bd -> Bt
			out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*)d", "$1t");
		}

		// MER fjernet
		// Bm -> Bn

		// Bb -> Bp
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*)b", "$1p");

		// eg n/l �/_/C/e -> ej n/l �/_/C/e
		out = out.replaceFirst("([^aeiouy���]*[aiouy���]+e)g([ln])([^aiouy��]|\\b)", "$1j$2$3");

		// Vg�/_/C -> V�/_/C
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)g([^aeiouy��]|\\b)", "$1$2");

		// Vge -> V
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)ge", "$1");

		// o/a vg �/_/C -> o/a v �/_/C
		out = out.replaceFirst("([^aeiouy���]*[eiuy���]*[ao]v)g([^aeiouy��]|\\b)", "$1$2");

		// o/a vg -> o/a v
		out = out.replaceFirst("([^aeiouy���]*[eiuy���]*[ao]v)g", "$1");

		// B r/l g �/e/_ -> B r/l �/e/_
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*[lr])g([^aiouy��]|\\b)", "$1$2");

		// arC -> aC, ar -> a
		out = out.replaceFirst("([^aeiouy���]*[eiouy���]*a)r([^aeiouy���]|\\b)", "$1$2");

		// MER
		// Bg -> Bk
		p = Pattern.compile("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*)g\\b");
		m = p.matcher(out);
		if (m.find()) {
			sb = new StringBuffer();
			m.appendReplacement(sb, "$1");
			out = sb.toString();
		} else {
			// Bg -> Bk
			out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*)g", "$1k");
		}

		// Bn g/k C/_ -> Bn C/_
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*n)[gk]([^aeiouy���]|\\b)", "$1$2");

		// Bs d/t -> Bs
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+[^aeiouy���]*s)[dt]", "$1");

		// ic_ -> is_
		out = out.replaceFirst("([^aeiouy���]*[aeouy���]*)ic\\b", "$1is");

		// av -> ov
		out = out.replaceFirst("([^aeiouy���]*[eiouy���]*)av", "$1ov");

		// schov -> skov
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)schov", "$1skov");

		// sch V/_ -> sj V/_
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+s)ch([aeiouy���]|\\b)", "$1j$2");

		// schC -> sC
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+s)ch([^aeiouy���])", "$1$2");

		// sc o/u -> sk o/u
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+s)c([ou])", "$1k$2");

		// sc, ts -> s
		out = out.replaceFirst("([^aeiouy���]*[aeiouy���]+)(sc|ts)", "$1s");

		// c e/i/y/j/s -> s e/i/y/j/s
		out = out.replaceFirst("c([eijsy])", "s$1");

		// ch -> k
		out = out.replaceFirst("ch", "k");

		// c -> k
		out = out.replaceFirst("c", "k");

		return out.replaceFirst("kje", "ke");
	}

	/**
	 * Anden vokalgruppe behandles ligesom den f�rste
	 *
	 * @param input
	 * @return String
	 * @throws Exception
	 */
	private String processSecondVowelGroup(String input) throws Exception {
		String out = input;
		StringBuffer sb = new StringBuffer(input);

		if (out.length() < 2) {
			return input;
		}

		// Find f�rste vokalgruppe
		Pattern p = Pattern.compile("[aeiouy���]+");
		Matcher m = p.matcher(input);

		// Find en evt. 2. vokalgruppe
		if (!m.find()) {
			return input;
		}

		final int vgStart = m.start();
		final int vgSlut = m.end();

		// Kun �n vokal i gruppen?
		if (vgStart == vgSlut - 1) {

			// y fordobles til y og �
			if (input.charAt(vgStart) == 'y') {
				return input + " " + input.substring(0, vgStart) + '�' + input.substring(vgStart + 1);
			}
			// Kun en vokal i gruppen, men ikke y
			return input;
		}

		// Der er mindst to vokaler i gruppen
		// Det unders�ges, om nogen af f�lgende regler skal bruges til
		// oms�tning. e som anden vokal er et specialtilf�lde

		// [Klusil eller labiodental frikativ]ie -> []je
		out = out.replaceAll("([bdfgkptv])ie", "$1je");

		// [Klusil eller labiodental frikativ]Ve -> []e
		p = Pattern.compile("([bdfgkptv][aeiouy���])e");
		m = p.matcher(out);
		if (m.find()) {
			sb = new StringBuffer();
			m.appendReplacement(sb, "$1");
			m.appendTail(sb);
			out = sb.toString();
		}

		// eu er et specialtilf�lde, som fordobles til ev og �j
		p = Pattern.compile("eu");
		m = p.matcher(out);
		if (m.matches()) {
			return out.replaceFirst("eu", "ev") + " " + out.replaceFirst("eu", "�j");
		}

		// �vrig h�ndtering af vokalpar
		final String org = out;
		out = out.replaceFirst("([aeiouy���])i", "$1j");
		out = out.replaceFirst("[iy]([aou])", "j$1");
		out = out.replaceFirst("i�", "j�");
		out = out.replaceFirst("(u)[ao]", "v$1");
		out = out.replaceFirst("([ao])u", "$1v");
		out = out.replaceFirst("([aeo�])y", "$1j");
		if (org.equalsIgnoreCase(out)) {
			return out.replaceFirst("([aeiouy���])[aeiouy���]", "$1");
		}
		return out;
	}

}
