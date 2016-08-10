package com.example.kqa;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class TabelaHKActivity extends Activity {
	GridView gvHiraKa;
	String [] hiraka = new String[] {
			"あ/ア a	", "い /イ i",	"う/ウ u"	,"え/エ  e",	"お/オ  o"	,
			"か/カ ka"	,"き/キ ki"	,"く/ク ku"	,"け/ケ ke	","こ/コ ko	",
			"さ/サ sa"	,"し/シ shi"	,"す/ス su"	,"せ/セ se"	,"そ/ソ so"	,
			"た/タ ta"	,"ち/チ chi", "つ/ツ tsu	" ,"て/テ te"	,"と/ト to"	,
			"な/ナ na"	,"に/ニ ni",	"ぬ/ヌ nu",	"ね/ネ ne"	,"の/ノ no",	
			"は/ハ ha"	,"ひ/ヒ hi"	,  " ふ/フ fu",	"へ/ヘ he" 	,"ほ/ホ ho"	,	
			"ま/マ ma	","み/ミ mi"	,"む/ム mu"	,"め/メ me"	,"も/モ mo	"	,			
			"や/ヤ ya"	,"ゆ/ユ yu"	,"よ/ヨ yo"	,					
			"ら/ラ ra"	,"り/リ ri"	,"る/ル ru"	,"れ/レ re"	,"ろ/ロ ro"	,				
			"わ/ワ wa"	,"を/ヲ wo" , "ん/ン n"

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tabelahk);
		gvHiraKa = (GridView) findViewById(R.id.gvHiraKata);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hiraka);
 
		gvHiraKa.setAdapter(adapter);
 
		gvHiraKa.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
			   Toast.makeText(getApplicationContext(),
				((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		Main();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	private void Main()
	{

	}
}
