package g3.viewchoosephoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_photo_picker.*

class PhotoPickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)

        setUpViewPagerWithTabLayout()
        initViews()
    }

    private fun setUpViewPagerWithTabLayout() {


        TabLayoutMediator(photo_picker_tab_layout_folder,photo_picker_view_pager) { tab, position ->
            tab.text = "TAB $position"
        }.attach()
    }

    private fun initViews() {

    }

}