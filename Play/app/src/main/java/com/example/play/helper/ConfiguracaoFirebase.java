package com.example.play.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFireBase;
    private static FirebaseAuth referenciaAuntenticacao;
    private static StorageReference referenciaStorage;

    //retorna a referencia do database
    public static DatabaseReference getFirebase(){
        if( referenciaFireBase == null){
            referenciaFireBase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFireBase;
    }


    //retorna a instancia do firebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if( referenciaAuntenticacao == null){
            referenciaAuntenticacao = FirebaseAuth.getInstance();
        }
        return referenciaAuntenticacao;
    }

    //retorna instancia do firebaseStotage
    public static StorageReference getFirebaseStorage(){
        if( referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }



}
