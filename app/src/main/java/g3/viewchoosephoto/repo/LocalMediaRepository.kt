package g3.viewchoosephoto.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.util.ImageUtils

class LocalMediaRepository(
    private val context: Context
) : ILocalMediaRepository {

    override fun getAllLocalAlbums() : MutableLiveData<List<AlbumImage>> {
        return ImageUtils.getAllImage(context) as MutableLiveData<List<AlbumImage>>
    }
}