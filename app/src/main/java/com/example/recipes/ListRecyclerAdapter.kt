package com.example.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipes_list.view.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.ArrayList

class ListRecyclerAdapter(val eatList:ArrayList<String>, val idList:ArrayList<Int>):RecyclerView.Adapter<ListRecyclerAdapter.EatHolder>() {
    class EatHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EatHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view= inflater.inflate(R.layout.recycler_row,parent,false)
        return EatHolder(view)
    }

    override fun onBindViewHolder(holder: EatHolder, position: Int) {
        holder.itemView.recycler_row_text.text=eatList[position]
        holder.itemView.setOnClickListener {
            val action=RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment("clicked from recycler list ",idList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return eatList.size
    }
}