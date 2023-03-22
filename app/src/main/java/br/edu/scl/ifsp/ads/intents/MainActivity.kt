package br.edu.scl.ifsp.ads.intents

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            }
            else {
                Toast.makeText(this, "Permissão necessária para continuar", Toast.LENGTH_SHORT).show()
                finish()
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
                val url: Uri = Uri.parse(amb.parametroTv.toString())
                val navegadorIntent: Intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // checar a permissão
                }
                else {
                    // chamar o número
                }
                true
            }
            R.id.dialMi -> true
            R.id.pickMi -> true
            R.id.chooserMi -> true
            else -> false
        }
    }
}