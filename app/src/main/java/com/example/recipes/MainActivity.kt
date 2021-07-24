package com.example.recipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.add_eat,menu )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.adding_eat_item){
            try {
                val action = RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment("clicked from menu",0)
                Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
            }catch (e:Exception){
                val action= RecipeFragmentDirections.actionRecipeFragmentSelf("clicked from menu",0)
                Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
                e.printStackTrace()
            }

        }

        return super.onOptionsItemSelected(item)
    }
}