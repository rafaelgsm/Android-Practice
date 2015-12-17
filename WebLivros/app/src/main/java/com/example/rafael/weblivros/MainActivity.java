package com.example.rafael.weblivros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * App abre apenas uma lista de livros, a partir do JSON deste link:
 * https://raw.githubusercontent.com/nglauber/dominando_android/master/livros_novatec.json
 *
 * A View da lista é um fragment(LivroListFragment).
 *
 * -LivroHttp É a classe utilitaria para fazer conexao e baixar o json como lista de livros...
 * -LivroListFragment Tem uma AsyncTask aninhada, que usa o LivroHttp para pegar a lista de livros...
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
