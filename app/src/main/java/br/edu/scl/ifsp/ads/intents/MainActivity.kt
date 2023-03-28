package br.edu.scl.ifsp.ads.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.ads.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object Constantes {
        const val PARAMETRO_EXTRA: String = "PARAMETRO_EXTRA"
    }

    private lateinit var parl: ActivityResultLauncher<Intent>
    private lateinit var permissaoChamadaArl: ActivityResultLauncher<String>
    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        parl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                val retorno: String? = result.data?.getStringExtra(PARAMETRO_EXTRA)
                amb.parametroTv.text = retorno
            }
        }

        amb.entrarParametroBt.setOnClickListener {
            val parametroIntent = Intent("PALMEIRAS_NAO_TEM_MUNDIAL_ACTION")
            parametroIntent.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString())
            parl.launch(parametroIntent)
        }

        permissaoChamadaArl = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissaoConcedida ->
            if (permissaoConcedida) {
                // chamar o número
                chamarNumero(chamar = true)
            }
            else {
                Toast.makeText(this, "Permissão necessária para continuar", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        pegarImagemArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                // pego a imagem retornada
                val imagemUri = resultado.data?.data
                imagemUri?.let {
                    amb.parametroTv.text = it.toString()
                    // abrindo imagem para visualização
                    val visualizarImagemIntent = Intent(ACTION_VIEW, it)
                    startActivity(visualizarImagemIntent)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent: Intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // checar a permissão
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        // chamar o número
                        chamarNumero(chamar = true)
                    }
                    else {
                        // solicitar a permissão
                        permissaoChamadaArl.launch(CALL_PHONE)
                    }
                }
                else {
                    // chamar o número
                    chamarNumero(chamar = true)
                }
                true
            }
            R.id.dialMi -> {
                chamarNumero(chamar = false)
                true
            }
            R.id.pickMi -> {
                val pegarImagemIntent: Intent = Intent(ACTION_PICK)
                val diretorioImagens = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true
            }
            R.id.chooserMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent: Intent = Intent(Intent.ACTION_VIEW, url)

                val escolherAppIntent: Intent = Intent(ACTION_CHOOSER)
                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador preferido")
                escolherAppIntent.putExtra(EXTRA_INTENT, navegadorIntent)

                startActivity(escolherAppIntent)
                true
            }
            else -> false
        }
    }

    private fun chamarNumero(chamar: Boolean) {
        val numeroUri: Uri = Uri.parse("tel: ${amb.parametroTv.text}")
        val chamarIntent: Intent = Intent(if (chamar) ACTION_CALL else ACTION_DIAL)
        chamarIntent.data = numeroUri
        startActivity(chamarIntent)
    }
}