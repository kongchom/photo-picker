package g3.viewchoosephoto.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.util.ImageUtils
import javax.inject.Inject

class LocalMediaRepository @Inject constructor(
    private val context: Context
) : ILocalMediaRepository {

    override fun getAllLocalAlbums() : List<AlbumImage> {
        return ImageUtils.getAllImage(context)
    }
}