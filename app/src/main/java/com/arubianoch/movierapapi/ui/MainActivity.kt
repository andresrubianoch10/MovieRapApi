package com.arubianoch.movierapapi.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.arubianoch.movierapapi.R
import com.arubianoch.movierapapi.ui.popular.PopularFragmentDirections
import com.arubianoch.movierapapi.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        bottom_nav.setupWithNavController(navController)

        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_search -> onSearchClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSearchClicked() {
        startActivityForResult(
            Intent(this, SearchActivity::class.java), SEARCH_REQUEST
        )
        overridePendingTransition(R.animator.fade_in, R.animator.fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SEARCH_REQUEST ->
                if (resultCode == Activity.RESULT_OK)
                    onMovieFounded(
                        data!!.getStringExtra("search_title"),
                        data!!.getIntExtra("search_id", 0)
                    )
        }
    }

    private fun onMovieFounded(movieTitle: String, movieId: Int) {
        try {
            val actionDetail = PopularFragmentDirections.actionDetail(movieId.toString())
            Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment).navigate(actionDetail)
            overridePendingTransition(R.animator.slide_up_in, R.animator.slide_up_out)
        } catch (e: Exception) {
            //TODO("Handle go to detail when user go to Search Activity from Fragment detail")
        }
    }

    companion object {
        public const val SEARCH_REQUEST = 10
    }
}
