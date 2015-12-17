package com.example.rafael.weblivros;

/**
 * Created by Rafael on 12/17/2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para estabelecer conexão, baixar e manipular uma resposta JSON.
 * Usada numa Thread separada, estabelece a conexão, cria um objeto JSON a partir da URL,
 * e gera a List<Livros> a partir do objeto JSON(Que contem os livros).
 */
public class LivroHttp {

    public static final String LIVROS_URL_JSON =
            "https://raw.githubusercontent.com/nglauber/" +
                    "dominando_android/master/livros_novatec.json";

    //Configura e realiza a conexão...
    private static HttpURLConnection connectar(String urlArquivo) throws IOException {
        final int SEGUNDOS = 1000;
        URL url = new URL(urlArquivo);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setReadTimeout(10 * SEGUNDOS);
        conexao.setConnectTimeout(15 * SEGUNDOS);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);
        conexao.connect();
        return conexao;
    }

    public static boolean temConexao(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Faz uso de métodos para converter os bytes recebidos para uma String, e gerar também
     * uma lista de livros, a partir de um objeto JSONObject.
     * @return Lista de livros, a partir de um JSON de livros.
     *
     * @see LivroHttp#bytesParaString(InputStream)
     * @see LivroHttp#lerJsonLivros(JSONObject)
     */
    public static List<Livro> carregarLivrosJson() {
        try {
            HttpURLConnection conexao = connectar(LIVROS_URL_JSON);

            int resposta = conexao.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {
                InputStream is = conexao.getInputStream();
                JSONObject json = new JSONObject(bytesParaString(is));
                return lerJsonLivros(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Faz o "parse" de JSON para livro, gerando uma lista de livros...
    public static List<Livro> lerJsonLivros(JSONObject json) throws JSONException {
        List<Livro> listaDeLivros = new ArrayList<Livro>();

        String categoriaAtual;

        JSONArray jsonNovatec = json.getJSONArray("novatec");
        for (int i = 0; i < jsonNovatec.length(); i++) {
            JSONObject jsonCategoria = jsonNovatec.getJSONObject(i);
            categoriaAtual = jsonCategoria.getString("categoria");

            JSONArray jsonLivros = jsonCategoria.getJSONArray("livros");
            for (int j = 0; j < jsonLivros.length(); j++) {
                JSONObject jsonLivro = jsonLivros.getJSONObject(j);

                Livro livro = new Livro(
                        jsonLivro.getString("titulo"),
                        categoriaAtual,
                        jsonLivro.getString("autor"),
                        jsonLivro.getInt("ano"),
                        jsonLivro.getInt("paginas"),
                        jsonLivro.getString("capa")
                );

                listaDeLivros.add(livro);
            }
        }

        return listaDeLivros;
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        // O bufferzao vai armazenar todos os bytes lidos
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        // precisamos saber quantos bytes foram lidos
        int bytesLidos;
        // Vamos lendo de 1KB por vez...
        while ((bytesLidos = is.read(buffer)) != -1) {
            // copiando a quantidade de bytes lidos do buffer para o bufferzão
            bufferzao.write(buffer, 0, bytesLidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }
}