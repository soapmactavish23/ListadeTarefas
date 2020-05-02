package com.example.listadetarefas;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText txtTarefa;
    private Button btnSalvar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //Recuperando dados
            txtTarefa = (EditText) findViewById(R.id.txtTarefa);
            btnSalvar = (Button) findViewById(R.id.btnSalvar);
            listaTarefas = (ListView) findViewById(R.id.lista);
            try{

                //Criando banco de dados
                bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE, null);

                //Criando a tabela
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

                btnSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String textoDigitado = txtTarefa.getText().toString();
                        salvarTarefa(textoDigitado);
                        recuperarTarefas();
                    }
                });


                //Deletar
                listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        removerTarefas(ids.get(position));
                    }
                });

                recuperarTarefas();

            }catch (Exception e){
                e.printStackTrace();
            }
    }

    private void salvarTarefa(String texto){
        try{
            if(texto.equals("")){
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }else{
                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES('"+texto+"')");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperarTarefas(){
        try{
            //Recuperando Tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //Recuperando ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar o adaptador
            listaTarefas = (ListView) findViewById(R.id.lista);
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    itens
            );
            listaTarefas.setAdapter(itensAdaptador);

            //listar tarefas
            cursor.moveToFirst();
            while (cursor != null){
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add( Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefas(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id = "+ id);
            recuperarTarefas();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
