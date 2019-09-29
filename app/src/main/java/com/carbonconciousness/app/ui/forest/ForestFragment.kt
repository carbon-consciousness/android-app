package com.carbonconciousness.app.ui.forest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R
import kotlinx.android.synthetic.main.fragment_forest.*
import kotlinx.android.synthetic.main.tree_entry.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ForestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forest, container, false)

        var treeList = ArrayList<Tree>()
        for (i in 1..9) {
            var rand = Random.nextInt(0, 6)
            val trees = arrayOf(R.drawable.cactus_6_dead, R.drawable.cactus_5_sick, R.drawable.cactus_1, R.drawable.cactus_2, R.drawable.cactus_3, R.drawable.cactus_4)
            val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
            treeList.add(Tree(months[i], trees[rand]))
        }
        var gv_forest = root.findViewById<GridView>(R.id.gv_forest)
        gv_forest.adapter = TreeAdapter(context!!, treeList)

        return root
    }

    override fun onResume() {
        super.onResume()

    }

}

class TreeAdapter(var context: Context, var treeList: ArrayList<Tree>) : BaseAdapter() {

    override fun getCount(): Int {
        return treeList.size
    }

    override fun getItem(position: Int): Any {
        return treeList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tree = treeList[position]

        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val treeView = inflator.inflate(R.layout.tree_entry, null)
        treeView.imgTree.setImageResource(tree.image!!)
        treeView.tvName.text = tree.name

        return treeView
    }

}

class Tree {
    var name: String? = null
    var image: Int? = null
    constructor(name:String, image: Int) {
        this.name = name
        this.image = image
    }
}