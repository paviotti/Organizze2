package com.paviotti.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.paviotti.organizze.R;
import com.paviotti.organizze.config.ConfiguracaoFirebase;
import com.paviotti.organizze.model.Usuario;

public class CadastroActivity extends AppCompatActivity {
    private EditText editNome, editEmail, editSenha;
    private Button btnCadastrar;
    private FirebaseAuth autenticacao; //precisa instalar a dependencia
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> {
            String txtNome = editNome.getText().toString();
            String txtEmail = editEmail.getText().toString();
            String txtSenha = editSenha.getText().toString();

            if (!txtNome.isEmpty()) {
                if (!txtEmail.isEmpty()) {
                    if (!txtSenha.isEmpty()) {
                        usuario = new Usuario(); //cria nova instancia de um usuário

                        //salva no objeto usuario
                        usuario.setNome(txtNome);
                        usuario.setEmail(txtEmail);
                        usuario.setSenha(txtSenha);
                        cadastrarUsuario();
                    } else {
                        Toast.makeText(this, "Prenencha o senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Prenencha o E-mail!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Prenencha o nome!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()  //passa parqa o Firebase
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                }else{
                    //tratamento de exceção
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) { //exceção para senha fraca
                        excecao = "Digite uma senha mais forte, minimo 6 caracteres";
                    }catch (FirebaseAuthInvalidCredentialsException e){ //exceção para email digita é invalido
                        excecao = "Por favor digite um email valido ";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Email já cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário!" + e.getMessage();
                        e.printStackTrace(); //imprime no log
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}