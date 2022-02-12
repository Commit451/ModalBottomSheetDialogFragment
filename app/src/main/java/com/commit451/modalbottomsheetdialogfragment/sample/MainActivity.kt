package com.commit451.modalbottomsheetdialogfragment.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.commit451.modalbottomsheetdialogfragment.OptionRequest
import com.commit451.modalbottomsheetdialogfragment.sample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), ModalBottomSheetDialogFragment.Listener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonShow.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.options)
                    .add(OptionRequest(123, "Custom", R.drawable.ic_bluetooth_black_24dp))
                    .show(supportFragmentManager, "HI")
        }

        binding.buttonShowWithHeader.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.option_lots)
                    .header("Neat")
                    .show(supportFragmentManager, "HI")
        }

        binding.buttonCustom.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.option_lots)
                    .layout(R.layout.item_custom)
                    .header("Neat")
                    .columns(3)
                    .show(supportFragmentManager, "HI")
        }

        binding.buttonOrder.setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                    .add(R.menu.options)
                    .add(OptionRequest(123, "Custom", R.drawable.ic_bluetooth_black_24dp))
                    .add(R.menu.option_money)
                    .show(supportFragmentManager, "HI")
        }
    }

    override fun onModalOptionSelected(tag: String?, option: Option) {
        Snackbar.make(binding.root, "Selected option ${option.title} from tag $tag", Snackbar.LENGTH_SHORT)
                .show()
    }

}
