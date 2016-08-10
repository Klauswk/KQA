package com.example.kqa;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class KanjiActivity extends Activity{
	public static final String PREFS_NAME = "Preferencias";
	public static final String KEY_IDIOMAS = "Idiomas";
	private String modificador = ">";
	private String sKanjis[];
	private RadioButton cbTranslation, cbGrade, cbStrokes;
	private GridView gvKanjis;
	private EditText etPesquisa;
	private Button btPesquisar;
	private Spinner spEscolha;
	private Cursor cursor = null;
	private SQLiteDatabase ListaKanjis;
	private RadioGroup rg;
	private SharedPreferences shared;
	private String[] textos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tabelakanjis);	
		shared = getSharedPreferences(PREFS_NAME, 0); //Nome do arquivo e tipo de edicação
		textos = getResources().getStringArray(R.array.en);
		criaBanco();
		buscarDados();
		rg = (RadioGroup) findViewById(R.id.rg);
		cbTranslation = (RadioButton) findViewById(R.id.cbTranslation);
		cbGrade = (RadioButton) findViewById(R.id.cbGrade);
		cbStrokes = (RadioButton) findViewById(R.id.cbStrokes);
		etPesquisa = (EditText) findViewById(R.id.etPesquisa);
		btPesquisar = (Button) findViewById(R.id.btPesquisar);
		gvKanjis = (GridView) findViewById(R.id.gvKanji);
		spEscolha = (Spinner) findViewById(R.id.spEscolha);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] {" "});
		ArrayAdapter<CharSequence> Spinneradapter = ArrayAdapter.createFromResource(this,R.array.escolhas_array, android.R.layout.simple_spinner_item);
		Spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEscolha.setAdapter(Spinneradapter);
		gvKanjis.setAdapter(adapter);
		cbStrokes.setChecked(true);
		etPesquisa.setText("");

			if(shared.getString("Idiomas", "").contains("Port"))
			{
				textos = getResources().getStringArray(R.array.pt);
			}
			else if(shared.getString("Idiomas", "").contains("Eng"))
			{
				textos = getResources().getStringArray(R.array.en);
			}
			else if(shared.getString("Idiomas", "").contains("Espa"))
			{
				textos = getResources().getStringArray(R.array.es);
			}
		btPesquisar.setText(textos[21]);
		cbTranslation.setText(textos[19]);
		cbGrade.setText(textos[20]);
		cbStrokes.setText(textos[18]);
			
		Main();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	private void Main()
	{

		btPesquisar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Log.i("Kanji", "Selecionado" + modificador);
				EmpurraInformacoes(etPesquisa.getText().toString(),rg.getCheckedRadioButtonId(), modificador);
				atualizaDados();
			}
		});
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.cbTranslation)
				{
					spEscolha.setVisibility(View.INVISIBLE);
					etPesquisa.setInputType(1);
					etPesquisa.setText("");
				}
				else
				{
					spEscolha.setVisibility(View.VISIBLE);
					etPesquisa.setInputType(2);
					etPesquisa.setText("");
				}
			}
		});

		spEscolha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				modificador = parent.getItemAtPosition(pos).toString();
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		gvKanjis.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				ProcuraInformacao(sKanjis[position]);
			}
		});
	}

	private void ProcuraInformacao(String kanji)
	{
		cursor = ListaKanjis.rawQuery("SELECT * FROM " + "Kanjis" + " where kanji = '" + kanji + "'" , null);
		cursor.moveToFirst();

		final Kanji kanjis = new Kanji(cursor.getInt(cursor.getColumnIndex("id")),kanji,cursor.getString(cursor.getColumnIndex("kanjiClass"))
				,cursor.getInt(cursor.getColumnIndex("grade")),cursor.getInt(cursor.getColumnIndex("strokesnum")), 
				cursor.getString(cursor.getColumnIndex("radical")),cursor.getString(cursor.getColumnIndex("translation")),
				cursor.getString(cursor.getColumnIndex("kunReading")),cursor.getString(cursor.getColumnIndex("kunTranslation"))
				);

		final Dialog informacoesKanjis = new Dialog(this);
		informacoesKanjis.setTitle(textos[8]+ " " + kanji);
		informacoesKanjis.setContentView(R.layout.informacoeskanji);
		TextView tvKanjiClass = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiClass);
		TextView tvKanjiGrade = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiGrade);
		TextView tvKanjiStrokes = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiStrokes);
		TextView tvKanjiRadical = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiRadical);
		TextView tvKanjiTranslation = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiTranslation);
		TextView tvKanjiKunReading = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiKunReading);
		TextView tvKanjiKunTranslation = (TextView) informacoesKanjis.findViewById(R.id.tvKanjiKunTranslation);
		Button btFavoritar = (Button) informacoesKanjis.findViewById(R.id.btFavoritar);
		
		btFavoritar.setText(textos[17]);
		tvKanjiClass.setText(textos[9]+ " " + kanjis.getKanjiclass());
		tvKanjiGrade.setText(textos[10]+ " " + kanjis.getGrade());
		tvKanjiStrokes.setText(textos[11]+ " " + kanjis.getStrokesnum());
		tvKanjiRadical.setText(textos[12]+ " " + kanjis.getRadical());
		tvKanjiTranslation.setText(textos[13]+ " " + kanjis.getTranslation());
		tvKanjiKunReading.setText(textos[14]+ " " + kanjis.getKunReading());
		tvKanjiKunTranslation.setText(textos[15]+ " " + kanjis.getKunTranslation());
		informacoesKanjis.show();
		
		btFavoritar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FavoritaKanji(kanjis.getKanji());
				informacoesKanjis.cancel();
			}
		});
		
		informacoesKanjis.show();

	}
	
	private void FavoritaKanji(String kanji)
	{
		ContentValues cv = new ContentValues();
		cv.put("favoritado", 1);
		ListaKanjis.update("Kanjis", cv, "kanji = '" + kanji + "'", null);
	}

	private void atualizaDados() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sKanjis);

		gvKanjis.setAdapter(adapter);

	}

	private void criaBanco()
	{
		try{
			String sql = "CREATE TABLE IF NOT EXISTS Kanjis ( " +
					"id 	   			INTEGER," +
					"kanji    			TEXT NOT NULL, " +
					"kanjiClass       	TEXT NOT NULL," +
					"grade       		INTEGER," +
					"strokesnum    		INTEGER, " +
					"radical 			TEXT NOT NULL, " +
					"translation		TEXT NOT NULL," +
					"kunReading  		TEXT NOT NULL," + 
					"kunTranslation		TEXT NOT NULL," +
					"favoritado		INTEGER)" ;

			ListaKanjis  = openOrCreateDatabase("BancoKanjis.db",MODE_PRIVATE,null);
			ListaKanjis.execSQL(sql);
		}
		catch(Exception err ){
			Toast.makeText(this, "Não foi possivel inicializar o Banco", Toast.LENGTH_LONG).show();
		}
	}

	private boolean buscarDados()
	{
		int indice = 0;
		try 
		{
			//cursor = ListaKanjis.query("Kanjis", new String[] {"id","kanji","kanjiClass","grade", "strokesnum", "radical", "translation" ,"kunReading","kunTranslation"} ,null, null, null,null,null);
			cursor = ListaKanjis.query("Kanjis", new String[] {"kanji"} ,null, null, null,null,null);

			if(cursor.getCount() != 0)
			{
				sKanjis = new String[20];
				cursor.moveToFirst();
				while(indice < 10)
				{
					sKanjis[indice] = cursor.getString(cursor.getColumnIndex("kanji"));
					cursor.moveToNext();
					indice++;
				}
				return true;
			}
			else
			{
				return false;
			}

		} 
		catch (Exception e) 
		{
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			return false;
		}

	}

	private void EmpurraInformacoes(String pesquisa, int id, String mod)
	{
		Log.i("Kanji", mod);
		if(id == R.id.cbTranslation)
		{
			cursor = ListaKanjis.rawQuery("SELECT * FROM " + "Kanjis" + " where translation LIKE '" + pesquisa + "%'" , null);
			cursor.moveToFirst();
		}
		else if(id == R.id.cbGrade)
		{
			cursor = ListaKanjis.rawQuery("SELECT * FROM " + "Kanjis" + " where grade "+ mod + "'" + pesquisa + "'" , null);
			cursor.moveToFirst();
		}
		else if(id == R.id.cbStrokes)
		{
			cursor = ListaKanjis.rawQuery("SELECT * FROM " + "Kanjis" + " where strokesnum " + mod + "'" + pesquisa + "'" , null);
			cursor.moveToFirst();
		}

		if(cursor.getCount() > 200)
		{
			sKanjis = new String[200];
			for (int i = 0; i < 200; i++) {
				sKanjis[i] = cursor.getString(cursor.getColumnIndex("kanji"));
				cursor.moveToNext();
			}
		}
		else
		{
			sKanjis = new String[cursor.getCount()];
			for (int i = 0; i < sKanjis.length ; i++) {
				sKanjis[i] = cursor.getString(cursor.getColumnIndex("kanji"));
				cursor.moveToNext();
			}
		}

	}
}
