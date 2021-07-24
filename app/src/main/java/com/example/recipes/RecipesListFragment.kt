package com.example.recipes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recipes_list.*


class RecipesListFragment : Fragment() {
    var eatNameList=ArrayList<String>()
    var eatIdList=ArrayList<Int>()
    private lateinit var listAdapter: ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter= ListRecyclerAdapter(eatNameList,eatIdList)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=listAdapter
        takeSqlData()
    }
    fun takeSqlData(){
        try {
            activity?.let {
                val database= it.openOrCreateDatabase("Eats", Context.MODE_PRIVATE,null)
                val cursor=database.rawQuery("SELECT * FROM Eats",null)
                val eatNameIndex=cursor.getColumnIndex("eatname")
                val eatIdIndex=cursor.getColumnIndex("id")
                eatNameList.clear()
                eatIdList.clear()
                while(cursor.moveToNext()){
                    eatNameList.add(cursor.getString(eatNameIndex))
                    eatIdList.add(cursor.getInt(eatIdIndex))
                }
                listAdapter.notifyDataSetChanged()  
                cursor.close()
            }
        }
        catch (e:Exception){

        }
    }



}