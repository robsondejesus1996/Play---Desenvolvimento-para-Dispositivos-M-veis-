package com.example.play.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.play.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class MainActivity extends AppCompatActivity {
    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    //teste essa merda de autenticação



    //teste essa merda
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //teste essa merda
        autenticacao = FirebaseAuth.getInstance();
        inicializaComponentes();


        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                //caso não estiver vazio o email
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //verificar o estado do switch
                        if (tipoAcesso.isChecked()) {// ira fazer o cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        //direcionar o usuário para a tela principal do app
                                    } else {
                                        String erroExecao = "";
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExecao = "Digite uma senha mas forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExecao = "Por favor, digite um email válido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroExecao = "Esta conta já foi cadastrada";
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(MainActivity.this,
                                                "Erro: " + erroExecao,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {//vai fazer o login casso a conta de email exista
                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this,
                                                "logado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Erro ao fazer login!!" + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Preencha o senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Preencha o Email!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void inicializaComponentes() {
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
    }
}