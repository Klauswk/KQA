package com.example.kqa;

public class Kanji {
	private int id;
	private String kanji;
	private String kanjiclass;
	private int Grade;
	private int Strokesnum;
	private String Radical;
	private String Translation;
	private String KunReading;
	private String KunTranslation;
	
	public Kanji(int id, String kanji ,String kanjiClass ,int grade, int strokesnum,
			String radical, String translation, String kunReading,
			String kunTranslation) {
		super();
		this.id = id;
		this.kanji = kanji;
		this.kanjiclass = kanjiClass;
		Grade = grade;
		Strokesnum = strokesnum;
		Radical = radical;
		Translation = translation;
		KunReading = kunReading;
		KunTranslation = kunTranslation;
	}
	
	public Kanji()
	{
		
	}
	
	public Kanji(int id)
	{
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKanji() {
		return kanji;
	}
	public void setKanji(String kanji) {
		this.kanji = kanji;
	}
	public String getKanjiclass() {
		return kanjiclass;
	}

	public void setKanjiclass(String kanjiclass) {
		this.kanjiclass = kanjiclass;
	}

	public int getGrade() {
		return Grade;
	}
	public void setGrade(int grade) {
		Grade = grade;
	}
	public int getStrokesnum() {
		return Strokesnum;
	}
	public void setStrokesnum(int strokesnum) {
		Strokesnum = strokesnum;
	}
	public String getRadical() {
		return Radical;
	}
	public void setRadical(String radical) {
		Radical = radical;
	}
	public String getTranslation() {
		return Translation;
	}
	public void setTranslation(String translation) {
		Translation = translation;
	}
	public String getKunReading() {
		return KunReading;
	}
	public void setKunReading(String kunReading) {
		KunReading = kunReading;
	}
	public String getKunTranslation() {
		return KunTranslation;
	}
	public void setKunTranslation(String kunTranslation) {
		KunTranslation = kunTranslation;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("id: ");
		string.append(id);
		string.append(" Kanji: ");
		string.append(kanji);
		string.append(" Grade: ");
		string.append(Grade);
		string.append(" Stroke number: ");
		string.append(Strokesnum);
		string.append(" Radical: ");
		string.append(Radical);
		string.append(" Translation: ");
		string.append(Translation);
		string.append(" KunReading: ");
		string.append(KunReading);
		string.append(" KunTranslation: ");
		string.append(KunTranslation);
		return string.toString();
	}
	
}
