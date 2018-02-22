package com.commit451.modalbottomsheetdialogfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.MenuRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * [BottomSheetDialogFragment] which can show a selection of options. Create using the [Builder]
 */
class ModalBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {

        private const val KEY_MENU = "menu"
        private const val KEY_LAYOUT = "layout"

        private fun newInstance(builder: Builder): ModalBottomSheetDialogFragment {
            val fragment = ModalBottomSheetDialogFragment()
            val args = Bundle()
            args.putInt(KEY_MENU, builder.menuRes)
            args.putInt(KEY_LAYOUT, builder.layoutRes)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var list: RecyclerView
    private lateinit var adapter: Adapter
    private var callback: ((id: Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.modal_bottom_sheet_dialog_fragment, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view.findViewById(R.id.list)
        val arguments = arguments
                ?: throw IllegalStateException("You need to create this via the builder")

        val menuRes = arguments.getInt(KEY_MENU)
        if (menuRes == 0) {
            throw IllegalStateException("You must provide a menu resource")
        }

        val menu = MenuBuilder(context)
        MenuInflater(context).inflate(menuRes, menu)
        val options = mutableListOf<Option>()
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val option = Option(item.itemId, item.icon, item.title)
            options.add(option)
        }
        adapter = Adapter({
            callback?.invoke(it)
            dismissAllowingStateLoss()
        })
        adapter.layoutRes = arguments.getInt(KEY_LAYOUT)

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        adapter.set(options)
    }

    fun show(fragmentManager: FragmentManager, tag: String, callback: (id: Int) -> Unit) {
        this.callback = callback
        show(fragmentManager, tag)
    }

    class Builder(@MenuRes internal val menuRes: Int) {

        @LayoutRes
        internal var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item

        fun layout(@LayoutRes layoutRes: Int): Builder {
            this.layoutRes = layoutRes
            return this
        }

        fun build(): ModalBottomSheetDialogFragment {
            return newInstance(this)
        }

        fun show(fragmentManager: FragmentManager, tag: String, callback: (id: Int) -> Unit): ModalBottomSheetDialogFragment {
            val dialog = build()
            dialog.show(fragmentManager, tag, callback)
            return dialog
        }
    }

    internal class Adapter(private val callback: (id: Int) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {

        private val options = mutableListOf<Option>()
        internal var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
            val holder = MyViewHolder(view)
            view.setOnClickListener {
                val option = options[holder.adapterPosition]
                callback.invoke(option.id)
            }
            return holder
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val option = options[position]
            holder.bind(option)
        }

        override fun getItemCount(): Int {
            return options.size
        }

        fun set(options: List<Option>) {
            this.options.clear()
            this.options.addAll(options)
            notifyDataSetChanged()
        }
    }

    internal class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var text: TextView = view.findViewById(android.R.id.text1)
        private var icon: ImageView = view.findViewById(android.R.id.icon)

        fun bind(option: Option) {
            text.text = option.title
            icon.setImageDrawable(option.icon)
        }
    }
}