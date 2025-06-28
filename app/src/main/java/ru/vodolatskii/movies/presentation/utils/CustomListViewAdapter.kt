package ru.vodolatskii.movies.presentation.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import ru.vodolatskii.movies.R
import java.util.*
import kotlin.collections.ArrayList


class DataModel internal constructor(var pier: Pair<Int,String>, var checked: Boolean)


class CustomListViewAdapter(private val dataSet: ArrayList<*>, mContext: Context, private val onCheckChanged: (Int) -> Unit) :
    ArrayAdapter<Any?>(mContext, R.layout.listview_item, dataSet) {
    private val yourSelectedItems =  ArrayList<Int>()

    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var checkBox: CheckBox
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getItem(position: Int): DataModel {
        return dataSet[position] as DataModel
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.listview_item, parent, false)
            viewHolder.txtName =
                convertView.findViewById(R.id.txtName)
            viewHolder.checkBox =
                convertView.findViewById(R.id.checkBox)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item: DataModel = getItem(position)
        viewHolder.txtName.text = item.pier.second

        viewHolder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                yourSelectedItems.add(position);
            } else {
                if (yourSelectedItems.contains(position)) {
                    yourSelectedItems.remove((position));
                }
            }
            onCheckChanged(position)
        }
        viewHolder.checkBox.isChecked = yourSelectedItems.contains( position)

        return result
    }
}