package com.commit451.modalbottomsheetdialogfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.MenuRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.GridLayoutManager
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
        private const val KEY_COLUMNS = "columns"
        private const val KEY_HEADER = "header"
        private const val KEY_HEADER_LAYOUT_RES = "header_layout_res"

        private fun newInstance(builder: Builder): ModalBottomSheetDialogFragment {
            val fragment = ModalBottomSheetDialogFragment()
            val args = Bundle()
            args.putInt(KEY_MENU, builder.menuRes)
            args.putInt(KEY_LAYOUT, builder.layoutRes)
            args.putInt(KEY_COLUMNS, builder.columns)
            args.putString(KEY_HEADER, builder.header)
            args.putInt(KEY_HEADER_LAYOUT_RES, builder.headerLayoutRes)
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
        adapter.header = arguments.getString(KEY_HEADER)
        adapter.headerLayoutRes = arguments.getInt(KEY_HEADER_LAYOUT_RES)

        list.adapter = adapter
        val columns = arguments.getInt(KEY_COLUMNS)
        if (columns == 1) {
            list.layoutManager = LinearLayoutManager(context)
        } else {
            val layoutManager = GridLayoutManager(context, columns)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.header != null && position == 0) {
                        columns
                    } else {
                        1
                    }
                }
            }
            list.layoutManager = layoutManager
        }

        adapter.set(options)
    }

    fun show(fragmentManager: FragmentManager, tag: String, callback: (id: Int) -> Unit) {
        this.callback = callback
        show(fragmentManager, tag)
    }

    class Builder(@MenuRes internal val menuRes: Int) {

        @LayoutRes
        internal var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item
        internal var columns = 1
        internal var header: String? = null
        internal var headerLayoutRes = R.layout.modal_bottom_sheet_dialog_fragment_header

        fun layout(@LayoutRes layoutRes: Int): Builder {
            this.layoutRes = layoutRes
            return this
        }

        fun columns(columns: Int): Builder {
            this.columns = columns
            return this
        }

        fun header(header: String, @LayoutRes layoutRes: Int = R.layout.modal_bottom_sheet_dialog_fragment_header): Builder {
            this.header = header
            this.headerLayoutRes = layoutRes
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

    internal class Adapter(private val callback: (id: Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_HEADER = 0
            const val VIEW_TYPE_ITEM = 1
        }

        private val options = mutableListOf<Option>()
        internal var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item
        internal var headerLayoutRes = R.layout.modal_bottom_sheet_dialog_fragment_header
        internal var header: String? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                VIEW_TYPE_HEADER -> {
                    val view = LayoutInflater.from(parent.context).inflate(headerLayoutRes, parent, false)
                    return HeaderViewHolder(view)
                }
                VIEW_TYPE_ITEM -> {
                    val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                    val holder = ItemViewHolder(view)
                    view.setOnClickListener {
                        val option = options[holder.adapterPosition]
                        callback.invoke(option.id)
                    }
                    return holder
                }
            }

            throw IllegalStateException("Wht is this")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val correctedPosition = if (header == null) position else position - 1
            if (holder is ItemViewHolder) {
                val option = options[correctedPosition]
                holder.bind(option)
            } else if (holder is HeaderViewHolder) {
                holder.bind(header)
            }
        }

        override fun getItemCount(): Int {
            return if (header == null) options.size else options.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            if (header != null) {
                if (position == 0) {
                    return VIEW_TYPE_HEADER
                }
            }
            return VIEW_TYPE_ITEM
        }

        fun set(options: List<Option>) {
            this.options.clear()
            this.options.addAll(options)
            notifyDataSetChanged()
        }
    }

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var text: TextView = view.findViewById(android.R.id.text1)
        private var icon: ImageView = view.findViewById(android.R.id.icon)

        fun bind(option: Option) {
            text.text = option.title
            icon.setImageDrawable(option.icon)
        }
    }

    internal class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var text: TextView = view.findViewById(android.R.id.text1)

        fun bind(header: String?) {
            text.text = header
        }
    }
}