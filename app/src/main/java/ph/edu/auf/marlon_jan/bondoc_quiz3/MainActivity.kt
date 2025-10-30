package ph.edu.auf.marlon_jan.bondoc_quiz3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.navigation.AppNavHost
import ph.edu.auf.marlon_jan.bondoc_quiz3.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val nav = rememberNavController()
                AppNavHost(nav = nav)
            }
        }
    }
}