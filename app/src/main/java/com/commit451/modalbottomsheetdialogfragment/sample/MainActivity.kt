package com.commit451.modalbottomsheetdialogfragment.sample

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.commit451.modalbottomsheetdialogfragment.OptionRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ModalBottomSheetDialogFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonShow.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.options)
                    .add(OptionRequest(123, "Custom", R.drawable.ic_bluetooth_black_24dp))
                    .show(supportFragmentManager, "HI")
        }

        buttonShowWithHeader.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.option_lots)
                    .header("Neat")
                    .show(supportFragmentManager, "HI")
        }

        buttonCustom.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.option_lots)
                    .layout(R.layout.item_custom)
                    .header("Neat")
                    .columns(3)
                    .show(supportFragmentManager, "HI")
        }

        buttonOrder.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.options)
                    .add(OptionRequest(123, "Custom", R.drawable.ic_bluetooth_black_24dp))
                    .add(R.menu.option_money)
                    .show(supportFragmentManager, "HI")
        }
    }

    override fun onModalOptionSelected(tag: String?, option: Option) {
        Snackbar.make(root, "Selected option ${option.title} from tag $tag", Snackbar.LENGTH_SHORT)
                .show()
    }

}
