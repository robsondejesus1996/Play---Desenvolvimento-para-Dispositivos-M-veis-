package com.example.play.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.play.R;
import com.example.play.helper.ConfiguracaoFirebase;
import com.example.play.helper.Permissoes;
import com.example.play.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoCategoria;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Anuncio anuncio;
    private StorageReference storage;


    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();//list de strings
    private List<String> ListaURLFotos = new ArrayList<>();//saber a url do firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        //storage
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        //validar as permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);
        inicializarComponentes();
        carregarDadosSpiner();
    }


    public void salvarAnuncio() {
        for (int i = 0; i < listaFotosRecuperadas.size(); i++) {
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador) {

        //Criar nó no storage
        StorageReference imagemAnuncio = storage
                .child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);

        // fazer o upload do arquivo
        String nomeArquivo = UUID.randomUUID().toString();
        final StorageReference imagemRef = imagemAnuncio.child( nomeArquivo + ".jpeg");
        UploadTask uploadTask = imagemRef.putFile(Uri.parse(urlString));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        String urlConvertida = url.toString();
                        ListaURLFotos.add(urlConvertida);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        if(totalFotos == ListaURLFotos.size()){
                            anuncio.setFotos( ListaURLFotos );
                            anuncio.salvar();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio() {
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }


    public void validarDadosAnuncio(View view) {
        String fone = "";
        anuncio = configurarAnuncio();
        if (campoTelefone.getRawText() != null) {
            fone = campoTelefone.getRawText().toString();
        }


        if (listaFotosRecuperadas.size() != 0) {
            if (!anuncio.getEstado().isEmpty()) {
                if (!anuncio.getCategoria().isEmpty()) {
                    if (!anuncio.getTitulo().isEmpty()) {
                        if (!anuncio.getValor().isEmpty() && !anuncio.getValor().equals("0")) {
                            if (!anuncio.getTelefone().isEmpty() && fone.length() >= 10) {
                                if (!anuncio.getDescricao().isEmpty()) {

                                    salvarAnuncio();

                                } else {
                                    exibirMensagemErro("Preencha o campo descrição!");
                                }
                            } else {
                                exibirMensagemErro("Preencha o campo telefone, digite ao menos 10 números!");
                            }
                        } else {
                            exibirMensagemErro("Preencha o campo valor!");
                        }
                    } else {
                        exibirMensagemErro("Preencha o campo titulo!");
                    }
                } else {
                    exibirMensagemErro("Preencha o campo categoria!");
                }

            } else {
                exibirMensagemErro("Preencha o campo estado!");
            }

        } else {
            exibirMensagemErro("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //recuperar a imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configura imagem no ImageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                imagem2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    private void carregarDadosSpiner() {
        /*String[] estados = new String[]{
          "SP", "MT"
        };*/
        //para estado
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        //para categoria
        String[] categoria = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categoria
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
    }

    private void inicializarComponentes() {
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);


        //configuração lacalidade para pt brasil
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaorResultado : grantResults) {
            if (permissaorResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app é necessários aceitar as permissoes");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}