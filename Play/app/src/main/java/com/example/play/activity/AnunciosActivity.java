package com.example.play.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.play.R;
import com.example.play.adapter.AdapterAnuncios;
import com.example.play.helper.ConfiguracaoFirebase;
import com.example.play.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();

    private RecyclerView recyclearAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //Configurações iniciais
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        //configurar RecyclearView
        recyclearAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclearAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclearAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();
    }

    public void recuperarAnunciosPublicos(){
        /*dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Listando dados, aguarde...")
                .setCancelable(false)
                .build();
        dialog.show();*/

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot estados: dataSnapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);


                        }
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
               // dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        autenticacao = FirebaseAuth.getInstance();
        //autenticacao.signOut();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //verificar se o usuario esta logado
        if(autenticacao.getCurrentUser() == null){//usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{//usuario logado
            menu.setGroupVisible(R.id.group_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity( new Intent(getApplicationContext(), CadastroActivity.class));
            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios :
                startActivity(new Intent(getApplicationContext(), MenuAnunciosActivity.class));

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){
        recyclearAnunciosPublicos = findViewById(R.id.recyclearAnunciosPublicos);
    }
}