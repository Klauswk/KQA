package com.example.kqa;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "Preferencias";
	public static final String KEY_IDIOMAS = "Idiomas";
	public static final String DBNAME = "BancoKanjis";
	private GridView gvKanjisFavoritos;
	private String sKanjis[];
	private TextView tvIdioma, tvKlaus;
	private Button btKanjis, btTabela, btOpcoes;
	private SharedPreferences shared;
	private RadioButton rbPortugues, rbEnglish, rbEspanhol;	
	private RadioGroup rgIdiomas;
	private SQLiteDatabase KanjisFavoritos;
	private Cursor cursor = null;
	private String[] textos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		shared = getSharedPreferences(PREFS_NAME, 0); //Nome do arquivo e tipo de edicação
		btKanjis = (Button) findViewById(R.id.btKanjis);
		btTabela = (Button) findViewById(R.id.btTabela);
		tvIdioma = (TextView) findViewById(R.id.tvIdioma);
		tvKlaus = (TextView) findViewById(R.id.tvKlaus);
		btOpcoes = (Button) findViewById(R.id.btOpcoes);
		textos = getResources().getStringArray(R.array.en);
		criaBanco();
		fKanjisFavoritos();

		gvKanjisFavoritos = (GridView) findViewById(R.id.gvKanjisFavoritos);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sKanjis);
		gvKanjisFavoritos.setAdapter(adapter);

		if(shared.contains("Idiomas"))
		{	
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
			Main();
		}
		else
		{
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.i("Kanjis", "Erro ao copiar Database");
			}
			PerguntaIdioma();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		fKanjisFavoritos();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sKanjis);

		gvKanjisFavoritos.setAdapter(adapter);

	}

	private void PerguntaIdioma() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.idioma);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		rgIdiomas = (RadioGroup) dialog.findViewById(R.id.rgIdiomas);
		rbPortugues = (RadioButton) dialog.findViewById(R.id.rbPt);
		rbEnglish = (RadioButton) dialog.findViewById(R.id.rbEnglish);
		rbEspanhol = (RadioButton) dialog.findViewById(R.id.rbEspanhol);

		rbPortugues.setChecked(false);
		rbEnglish.setChecked(false);
		rbEspanhol.setChecked(false);

		rgIdiomas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.rbPt)
				{
					SalvaIdioma("Portugues");
					dialog.cancel();
					Main();
				}
				else if(checkedId == R.id.rbEnglish)
				{
					SalvaIdioma("English");
					dialog.cancel();
					Main();

				}
				else if(checkedId == R.id.rbEspanhol)
				{
					SalvaIdioma("Espanhol");
					dialog.cancel();
					Main();
				}
			}
		});

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();

	}
	@Override
	protected void onPause() {
		super.onPause();

	}

	private void Main()
	{
		btKanjis.setText(textos[0]);
		btTabela.setText(textos[1]);
		tvKlaus.setText(textos[2]);
		tvIdioma.setText(shared.getString("Idiomas", "")); //nome da chave e aquilo que retorna 

		btTabela.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ChamaTabela();
			}
		});

		btKanjis.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ChamaListaKanjis();				
			}
		});

		gvKanjisFavoritos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if(sKanjis[0].contains("kan"))
				{
					
				}
				else
				{
					ProcuraInformacao(sKanjis[position]);
				}
				
			}
		});

		btOpcoes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CarregaOpcoes();

			}
		});
	}

	private void CarregaOpcoes() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.opcoes);

		final Button btSalvar = (Button) dialog.findViewById(R.id.btSalvarOpcoes);
		final TextView tvIdiomaOpcoes = (TextView) dialog.findViewById(R.id.tfIdiomaOpcoes);
		final TextView TamanhoPesquisa = (TextView) dialog.findViewById(R.id.tvTamanhoOpcoes);
		final Spinner spTamanhoArray = (Spinner) dialog.findViewById(R.id.spTamanhoArray);
		final Spinner spIdiomas = (Spinner) dialog.findViewById(R.id.spinnerIdiomaOpcoes);
		final int tamanho = shared.getInt("KEY_TAMANHOARRAY", 200);
		int valorArray = 100;
				
		btSalvar.setText(textos[6]);
		TamanhoPesquisa.setText(textos[5]);
		tvIdiomaOpcoes.setText(textos[4]);
		dialog.setTitle(textos[7]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] {" "});
		ArrayAdapter<CharSequence> Spinneradapter = ArrayAdapter.createFromResource(this,R.array.idiomas_array, android.R.layout.simple_spinner_item);
		Spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spIdiomas.setAdapter(Spinneradapter);
		
		ArrayAdapter<String> adapterArray = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] {" "});
		ArrayAdapter<CharSequence> SpinneradapterArray = ArrayAdapter.createFromResource(this,R.array.tamanho_array, android.R.layout.simple_spinner_item);
		SpinneradapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTamanhoArray.setAdapter(SpinneradapterArray);
		
		for (int i = 0; tamanho != valorArray; i++) {
			spTamanhoArray.setSelection(i);
			valorArray = Integer.valueOf(spTamanhoArray.getItemAtPosition(i).toString());
		}
		
		if(shared.getString("Idiomas", "").contains("Port"))
		{
			spIdiomas.setSelection(0);
		}
		else if(shared.getString("Idiomas", "").contains("Espa"))
		{
			spIdiomas.setSelection(1);
		}
		else if(shared.getString("Idiomas", "").contains("Eng"))
		{
			spIdiomas.setSelection(2);
		}
		dialog.show();

		spIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				SalvaIdioma(parent.getItemAtPosition(pos).toString());
				if(shared.getString("Idiomas", "").contains("Port"))
				{
					textos = getResources().getStringArray(R.array.pt);
					tvIdioma.setText(shared.getString("Idiomas", "")); //nome da chave e aquilo que retorna
					btKanjis.setText(textos[0]);
					btTabela.setText(textos[1]);
					tvKlaus.setText(textos[2]);
					btSalvar.setText(textos[6]);
					TamanhoPesquisa.setText(textos[5]);
					tvIdiomaOpcoes.setText(textos[4]);
					dialog.setTitle(textos[7]);

				}
				else if(shared.getString("Idiomas", "").contains("Eng"))
				{
					textos = getResources().getStringArray(R.array.en);
					btKanjis.setText(textos[0]);
					btTabela.setText(textos[1]);
					tvKlaus.setText(textos[2]);
					tvIdioma.setText(shared.getString("Idiomas", "")); //nome da chave e aquilo que retorna 
					btSalvar.setText(textos[6]);
					TamanhoPesquisa.setText(textos[5]);
					tvIdiomaOpcoes.setText(textos[4]);
					dialog.setTitle(textos[7]);
				}
				else if(shared.getString("Idiomas", "").contains("Espa"))
				{
					textos = getResources().getStringArray(R.array.es);
					btKanjis.setText(textos[0]);
					btTabela.setText(textos[1]);
					tvKlaus.setText(textos[2]);
					tvIdioma.setText(shared.getString("Idiomas", "")); //nome da chave e aquilo que retorna
					btSalvar.setText(textos[6]);
					TamanhoPesquisa.setText(textos[5]);
					tvIdiomaOpcoes.setText(textos[4]);
					dialog.setTitle(textos[7]);

				}
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spTamanhoArray.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				SalvaTamanhoArray(Integer.valueOf(parent.getItemAtPosition(pos).toString()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		btSalvar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void ProcuraInformacao(final String kanji)
	{
		if(kanji == null)
		{
			
		}
		else
		{
			cursor = KanjisFavoritos.rawQuery("SELECT * FROM Kanjis where kanji = '" + kanji + "'" , null);
			cursor.moveToFirst();

			Kanji kanjis = new Kanji(cursor.getInt(cursor.getColumnIndex("id")),kanji,cursor.getString(cursor.getColumnIndex("kanjiClass"))
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
			btFavoritar.setText(textos[16]);
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
					DeletaItem(kanji);
					informacoesKanjis.cancel();
				}
			});
		}
	}

	public void DeletaItem(String kanji)
	{
		try {
			ContentValues cv = new ContentValues();
			cv.put("favoritado", 0);
			KanjisFavoritos.update("Kanjis", cv, "kanji = '" + kanji + "'", null);
			fKanjisFavoritos();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, sKanjis);

			gvKanjisFavoritos.setAdapter(adapter);

		} catch (Exception e) {
			Toast.makeText(this, "Erro ao deletar: " + e, Toast.LENGTH_LONG).show();
		}
	}

	private void ChamaTabela() {
		Intent chamatabela = new Intent(this, TabelaHKActivity.class);
		startActivity(chamatabela);
	}
	private void ChamaListaKanjis() {
		Intent chamaProcura = new Intent(this, KanjiActivity.class);
		startActivity(chamaProcura);
	}

	private void SalvaIdioma(String Idioma)
	{
		Editor editor = shared.edit();
		editor.putString(KEY_IDIOMAS, Idioma);
		editor.commit();
	}
	
	private void SalvaTamanhoArray(int tamanho)
	{
		Editor editor = shared.edit();
		editor.putInt("KEY_TAMANHOARRAY", tamanho);
		editor.commit();
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

			KanjisFavoritos  = openOrCreateDatabase(DBNAME+".db",MODE_PRIVATE,null);
			KanjisFavoritos.execSQL(sql);

		}
		catch(Exception err ){
			Toast.makeText(this, "Não foi possivel inicializar o Banco", Toast.LENGTH_LONG).show();
		}
	}

	private void fKanjisFavoritos() {
		try 
		{
			//cursor = ListaKanjis.query("Kanjis", new String[] {"id","kanji","kanjiClass","grade", "strokesnum", "radical", "translation" ,"kunReading","kunTranslation"} ,null, null, null,null,null);
			cursor = KanjisFavoritos.rawQuery("SELECT * FROM Kanjis where favoritado = '" + 1 + "'", null);

			if(cursor.getCount() != 0)
			{
				sKanjis = new String[cursor.getCount()];
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					sKanjis[i] = cursor.getString(cursor.getColumnIndex("kanji"));
					cursor.moveToNext();
				}
			}
			else
			{
				sKanjis = new String[1];
				sKanjis[0] = textos[3];
			}

		} 
		catch (Exception e) 
		{
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

		}

	}


	private void copyDataBase() throws IOException {

		try {
			// Open your local db as the input stream
			InputStream myInput = getAssets().open("db/BancoKanjis.db");

			// Path to the just created empty db
			String outFileName = "/data/data/com.example.kqa/databases/BancoKanjis.db";
			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

		} catch (Exception e) {

			Log.e("error", e.toString());

		}

	}

}
