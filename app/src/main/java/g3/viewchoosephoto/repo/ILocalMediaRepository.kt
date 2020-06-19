package g3.viewchoosephoto.repo

import androidx.lifecycle.MutableLiveData
import g3.viewchoosephoto.model.AlbumImage

interface ILocalMediaRepository {
    fun getAllLocalAlbums(): List<AlbumImage>
}
