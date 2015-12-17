package com.example.rafael.weblivros;

/**
 * Created by Rafael on 12/17/2015.
 *
 * Exibe uma lista de livros, a partir de um JSON.
 *
 * Como a conexão deve ser realizada em outra Thread(Diferente da principal), usamos uma AsyncTask.
 * A classe LivrosTask faz uso da classe utilitária LivroHttp para estabelecer a conexão,
 * baixar e manipular um objeto JSON, para uma lista de Livros.
 *
 * Possui uma AsyncTask aninhada, para poder solicitar as alterações na UI, diretamente pelos
 * métodos da classe LivrosListFragment.
 *
 * @see com.example.rafael.weblivros.Livro
 * @see com.example.rafael.weblivros.LivroHttp
 */
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LivrosListFragment extends Fragment {
    private LivrosTask task;
    private List<Livro> listaLivros;
    private ListView listView;
    private TextView tvMsg;
    private ProgressBar progressBar;
    private ArrayAdapter<Livro> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //Infla layout do fragment, pega a referencia dos seus elementos, e retorna a view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_livros_list, null);
        tvMsg = (TextView)layout.findViewById(R.id.tv_msg);
        progressBar = (ProgressBar)layout.findViewById(R.id.progressBar);
        listView = (ListView)layout.findViewById(R.id.list);
        listView.setEmptyView(tvMsg);
        return layout;
    }


    /**
     * Seta o adapter padrao para a lista;
     * Inicia o download(Atravez da classe Util, e a Asynctask);
     * Também ativa a barra de progresso.
     *
     * @see LivrosListFragment#exibirProgress(boolean)
     * @see com.example.rafael.weblivros.LivrosListFragment.LivrosTask
     * @see LivroHttp
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (listaLivros == null) {
            listaLivros = new ArrayList<Livro>();
        }
        adapter = new ArrayAdapter<Livro>(getActivity(),
                android.R.layout.simple_list_item_1, listaLivros);
        listView.setAdapter(adapter);

        if (task == null) {
            if (LivroHttp.temConexao(getActivity())) {
                iniciarDownload();
            } else {
                tvMsg.setText("Sem conexão"); //Vai estar sem visibilidade(Não vai aparecer nada na tela)
            }
        } else if (task.getStatus() == AsyncTask.Status.RUNNING) {
            exibirProgress(true);
        }
    }

    /**
     * Faz a barra de progresso e a mensagem aparecerem, ou não.
     * @param exibir Se a barra de progresso ficará ou não visivel.
     */
    private void exibirProgress(boolean exibir) {
        if (exibir) {
            tvMsg.setText("Baixando informações dos livros...");
        }
        tvMsg.setVisibility(exibir ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void iniciarDownload() {
        if (task == null ||  task.getStatus() != AsyncTask.Status.RUNNING) {
            task = new LivrosTask();
            task.execute();
        }
    }

    class LivrosTask extends AsyncTask<Void, Void, List<Livro>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            exibirProgress(true);
        }

        @Override
        protected List<Livro> doInBackground(Void... strings) {
            return LivroHttp.carregarLivrosJson();
        }

        @Override
        protected void onPostExecute(List<Livro> livros) {
            super.onPostExecute(livros);
            exibirProgress(false);
            if (livros != null) {
                listaLivros.clear();
                listaLivros.addAll(livros);
                adapter.notifyDataSetChanged();
            } else {
                tvMsg.setText("Falha ao obter livros");
            }
        }
    }
}