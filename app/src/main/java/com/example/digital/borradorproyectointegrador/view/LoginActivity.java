package com.example.digital.borradorproyectointegrador.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.digital.borradorproyectointegrador.R;
import com.example.digital.borradorproyectointegrador.model.usuario_perfil.UsuarioPerfil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private LoginButton loginButtonFacebook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_logins);


        // Firebase/Facebook Login
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = firebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        // TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbarMultiLogin);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Login");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loginButtonFacebook = findViewById(R.id.login_button_facebook);
        loginButtonFacebook.setReadPermissions("email", "public_profile");
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                // FIREBASE LOGIN
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                DatabaseReference mReference = mDatabase.getReference();
                StorageReference raiz = mStorage.getReference();

                if (user != null){
                    DatabaseReference baseUsuarios = mReference.child(getResources().getString(R.string.child_usuarios));

                    if (baseUsuarios.orderByChild(user.getUid()).equals(user.getUid())) {
                        Intent volver = TrailerActivity.respuestaLogin();
                        setResult(TrailerActivity.RESULT_OK);
                        finish();
                    } else {
                        //La idea aca es que cada persona tenga un perfil en la base desde la cual podamos ver sus favoritos y listas
                        File file = new File(user.getPhotoUrl().getPath());
                        final Uri uriTemp = Uri.fromFile(file);
                        //TODO cargar imagen de usuario al storage
                        /*
                        StorageReference fotoPerfil = raiz.child(getResources().getString(R.string.child_fotos_usuarios)).child(user.getUid());
                        UploadTask uploadTask = fotoPerfil.putFile(Uri.parse(user.getPhotoUrl().getLastPathSegment()));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                        */
                        agregarPerfilABaseDeDatos(user.getUid(),user.getEmail(), uriTemp.getLastPathSegment(), user.getDisplayName());
                    }
                    goMainActivity();
                }
            }
        };

    }

    private void handleFacebookAccessToken(AccessToken token){
        loginButtonFacebook.setVisibility(View.INVISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goPerfilUsuario(){
        Intent intent = new Intent(LoginActivity.this, PerfilUsuarioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void agregarPerfilABaseDeDatos(String uId,String email, String imagen,String nombre){
        DatabaseReference mReference = mDatabase.getReference();
        DatabaseReference id = mReference.child(getResources().getString(R.string.child_usuarios)).child(uId);
        id.setValue(new UsuarioPerfil(uId,email,imagen, nombre,0,0,0,new ArrayList<Integer>(),new ArrayList<Integer>(),new ArrayList<String>()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }


}
