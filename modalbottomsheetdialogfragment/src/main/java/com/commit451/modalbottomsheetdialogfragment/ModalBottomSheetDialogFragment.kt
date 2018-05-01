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
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment.Builder

@SuppressLint("RestrictedApi")
/**
 * [BottomSheetDialogFragment] which can show a selection of options. Create using the [Builder]
 */
class ModalBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {

        private const val KEY_OPTIONS = "options"
        private const val KEY_RESOURCES = "resources"
        private const val KEY_LAYOUT = "layout"
        private const val KEY_COLUMNS = "columns"
        private const val KEY_HEADER = "header"
        private const val KEY_HEADER_LAYOUT_RES = "header_layout_res"

        private fun newInstance(builder: Builder): ModalBottomSheetDialogFragment {
            val fragment = ModalBottomSheetDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(KEY_OPTIONS, builder.options)
            args.putIntegerArrayList(KEY_RESOURCES, builder.menuResources)
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
    private var listener: Listener? = null

    val menu by lazy {
        MenuBuilder(context)
    }

    val menuInflater by lazy {
        MenuInflater(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.modal_bottom_sheet_dialog_fragment, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view.findViewById(R.id.list)
        val arguments = arguments
                ?: throw IllegalStateException("You need to create this via the builder")

        val optionRequests = arguments.getParcelableArrayList<OptionRequest>(KEY_OPTIONS)
        val resources = arguments.getIntegerArrayList(KEY_RESOURCES)

        val options = mutableListOf<Option>()

        //resource inflation is done first, so it does not actually retain the true ordering.
        //Maybe fix this later?
        resources.forEach {
            inflate(it, options)
        }

        optionRequests.forEach {
            options.add(it.toOption(context!!))
        }

        adapter = Adapter({
            listener?.onModalOptionSelected(this@ModalBottomSheetDialogFragment.tag, it)
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
        listener = bindHost()
    }

    private fun inflate(menuRes: Int, options: MutableList<Option>) {
        menuInflater.inflate(menuRes, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val option = Option(item.itemId, item.title, item.icon)
            options.add(option)
        }
    }

    private fun bindHost(): Listener {
        if (parentFragment != null) {
            if (parentFragment is Listener) {
                return parentFragment as Listener
            }
        }
        if (context is Listener) {
            return context as Listener
        }
        throw IllegalStateException("ModalBottomSheetDialogFragment must be attached to a parent (activity or fragment) that implements the ModalBottomSheetDialogFragment.Listener")
    }

    class Builder {

        internal var menuResources = ArrayList<Int>()
        internal var options = ArrayList<OptionRequest>()
        @LayoutRes
        internal var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item
        internal var columns = 1
        internal var header: String? = null
        internal var headerLayoutRes = R.layout.modal_bottom_sheet_dialog_fragment_header

        fun add(@MenuRes menuRes: Int): Builder {
            menuResources.add(menuRes)
            return this
        }

        fun add(option: OptionRequest): Builder {
            options.add(option)
            return this
        }

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

        fun show(fragmentManager: FragmentManager, tag: String): ModalBottomSheetDialogFragment {
            val dialog = build()
            dialog.show(fragmentManager, tag)
            return dialog
        }
    }

    interface Listener {
        fun onModalOptionSelected(tag: String?, option: Option)
    }

    internal class Adapter(private val callback: (option: Option) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                        val position = if (header != null) {
                            holder.adapterPosition - 1
                        } else {
                            holder.adapterPosition
                        }
                        val option = options[position]
                        callback.invoke(option)
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